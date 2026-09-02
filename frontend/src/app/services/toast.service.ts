import { Injectable, signal } from '@angular/core';


const TOAST_DISPLAY_MS = 5000;

@Injectable({
  providedIn: 'root',
})
export class ToastService {
  readonly message = signal<string | null>(null);

  private timeout: ReturnType<typeof setTimeout> | null = null;

  show(text: string) {
    this.message.set(text);

    if (this.timeout !== null) {
      clearTimeout(this.timeout);
    }

    this.timeout = setTimeout(() => {
      this.message.set(null);
      this.timeout = null;
    }, TOAST_DISPLAY_MS);
  }

  clear() {
    if (this.timeout !== null) {
      clearTimeout(this.timeout);
      this.timeout = null;
    }
    this.message.set(null);
  }
}

