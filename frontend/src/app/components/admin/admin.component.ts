import { Component, inject, OnInit, signal } from '@angular/core';
import { AdminService } from '../../services/admin.service';
import { User } from '../../models/types';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin',
  imports: [FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css',
})
export class AdminComponent implements OnInit {
  private adminService = inject(AdminService);

  users = signal<User[]>([]);
  addAmount = signal<{ [key: string]: number }>({});

  currentPage = signal<number>(0);
  totalPages = signal<number>(0);
  searchTerm = signal<string>('');

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.adminService.getUsers(this.searchTerm(), this.currentPage(), 10).subscribe((page) => {
      this.users.set(page.content);
      this.totalPages.set(page.totalPages);
    });
  }

  onSearch() {
    this.currentPage.set(0);
    this.loadUsers();
  }

  nextPage() {
    if (this.currentPage() < this.totalPages() - 1) {
      this.currentPage.update((p) => p + 1);
      this.loadUsers();
    }
  }

  prevPage() {
    if (this.currentPage() > 0) {
      this.currentPage.update((p) => p - 1);
      this.loadUsers();
    }
  }

  addMoney(userId: string) {
    const amount = this.addAmount()[userId];
    if (!amount || amount <= 0) return;

    this.adminService.addBalance(userId, amount).subscribe(() => {
      this.loadUsers();
      this.addAmount.update((amounts) => ({ ...amounts, [userId]: 0 }));
    });
  }
}
