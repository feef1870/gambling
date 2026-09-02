import { HttpErrorResponse } from '@angular/common/http';
import { ErrorResponse } from '../models/types';


export function extractErrorMessage(error: unknown, fallback: string) {
  if (error instanceof HttpErrorResponse) {
    if (error.status === 0) {
      return "Cannot reach the server";
    }

    const body = error.error as Partial<ErrorResponse> | null;
    if (body && typeof body.message === "string" && body.message.trim() !== "") {
      return body.message;
    }
  }

  return fallback;
}
