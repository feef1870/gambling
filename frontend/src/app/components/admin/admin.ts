import { Component, inject, OnInit, signal } from '@angular/core';
import { AdminService } from '../../services/admin';
import { User } from '../../models/types';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-admin',
  imports: [FormsModule],
  templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class AdminComponent implements OnInit {
  private adminService = inject(AdminService);

  users = signal<User[]>([]);
  addAmount = signal<{ [key: string]: number }>({});

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.adminService.getUsers().subscribe((data) => this.users.set(data));
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
