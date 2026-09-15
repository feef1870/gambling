import { Component, inject, signal } from '@angular/core';
import { UserService } from '../../services/user.service';
import { ToastService } from '../../services/toast.service';
import { finalize } from 'rxjs';
import { extractErrorMessage } from '../../util/errors';

const REQUIRED_CLICKS = 20;

@Component({
  selector: 'app-labor',
  imports: [],
  templateUrl: './labor.component.html',
  styleUrl: './labor.component.css',
})
export class LaborComponent {
  private readonly userService = inject(UserService);
  private readonly toast = inject(ToastService);

  readonly clicks = signal(0);
  readonly isShaking = signal(false);
  readonly isWorking = signal(false);

  work() {
    if (this.isWorking()) return;

    this.isShaking.set(false);
    setTimeout(() => this.isShaking.set(true), 0);

    this.clicks.set(Math.min(this.clicks() + 1, REQUIRED_CLICKS));

    if (this.clicks() >= REQUIRED_CLICKS) {
      this.claimWage();
    }
  }

  private claimWage() {
    this.isWorking.set(true);

    this.userService
      .claimLaborWage()
      .pipe(finalize(() => this.isWorking.set(false)))
      .subscribe({
        next: () => {
          this.clicks.set(0);
          this.userService.refreshUser();
        },
        error: (err) => {
          this.toast.show(extractErrorMessage(err, "Could not claim your wage"));
        },
      })
  }

  stopShake() {
    this.isShaking.set(false);
  }
}
