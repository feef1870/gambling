import { Component, inject, OnInit, signal } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { User } from '../../models/types';
import { FormGroup, FormsModule } from '@angular/forms';
import { ToastService } from '../../services/toast.service';
import { extractErrorMessage } from '../../util/errors';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-admin',
  imports: [FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent implements OnInit {
  private readonly adminService = inject(AdminService);
  private readonly toast = inject(ToastService);

  readonly users = signal<User[]>([]);
  readonly amounts = signal<Record<string, number>>({});
  readonly pendingUserIds = signal<ReadonlySet<string>>(new Set<string>());

  readonly currentPage = signal(0);
  readonly totalPages = signal(0);
  readonly searchTerm = signal('');
  readonly isLoading = signal(false);

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.isLoading.set(true);

    this.adminService.getUsers(this.searchTerm(), this.currentPage(), 10).subscribe({
      next: (page) => {
        this.users.set(page.content);
        this.totalPages.set(page.totalPages);
        this.isLoading.set(false);
      },
      error: (err) => {
        this.toast.show(extractErrorMessage(err, "Failed to load users"));
        this.isLoading.set(false);
      },
    });
  }

  onSearch() {
    this.currentPage.set(0);
    this.loadUsers();
  }

  nextPage() {
    if (this.isLoading() || this.currentPage() >= this.totalPages() - 1) {
      return;
    }
    this.currentPage.update((p) => p + 1);
    this.loadUsers();
  }

  prevPage() {
    if (this.isLoading() || this.currentPage() <= 0) {
      return;
    }
    this.currentPage.update((p) => p - 1);
    this.loadUsers();
  }

  setAmount(userId: string, value: number | null) {
    this.amounts.update((current) => ({ ...current, [userId]: value ?? 0 }));
  }

  isPending(userId: string): boolean {
    return this.pendingUserIds().has(userId);
  }

  addMoney(userId: string) {
    if (this.isPending(userId)) {
      return;
    }

    const amount = this.amounts()[userId];
    if (!Number.isInteger(amount) || amount <= 0) {
      this.toast.show("Amount must be a positive whole number");
      return;
    }

    this.setPending(userId, true);

    this.adminService
      .addBalance(userId, amount)
      .pipe(finalize(() => this.setPending(userId, false)))
      .subscribe({
        next: () => {
          this.toast.show(`Added ${amount} coins`);
          this.setAmount(userId, 0);
          this.loadUsers();
        },
        error: (err) => this.toast.show(extractErrorMessage(err, "Failed to add balance")),
      });
  }

  private setPending(userId: string, pending: boolean) {
    this.pendingUserIds.update((ids) => {
      const next = new Set(ids);
      if (pending) {
        next.add(userId);
      } else {
        next.delete(userId);
      }
      return next;
    });
  }
}
