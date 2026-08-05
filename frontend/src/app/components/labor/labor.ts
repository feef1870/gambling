import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../services/user';

@Component({
  selector: 'app-labor',
  imports: [],
  templateUrl: './labor.html',
  styleUrl: './labor.css',
})
export class LaborComponent {
  private userService = inject(UserService);

  clicks = signal<number>(0);
  isShaking = signal<boolean>(false);
  isWorking = signal<boolean>(false);

  work() {
    if (this.isWorking()) return;

    this.clicks.update(c => c + 1);

    this.isShaking.set(false);
    setTimeout(() => this.isShaking.set(true), 0);

    if (this.clicks() >= 20) {
      this.isWorking.set(true);

      this.userService.claimLaborWage().subscribe({
        next: () => {
          this.clicks.set(0);
          this.userService.refreshUser();
          this.isWorking.set(false);
        },
        error: () => {
          this.isWorking.set(false);
        }
      });
    }
  }

  stopShake() {
    this.isShaking.set(false);
  }
}
