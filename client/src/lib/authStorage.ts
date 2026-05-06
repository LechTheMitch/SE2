const STORAGE_KEY = 'qr-attendance-token';

export function readStoredToken(): string | null {
  return localStorage.getItem(STORAGE_KEY);
}

export function writeStoredToken(token: string) {
  localStorage.setItem(STORAGE_KEY, token);
}

export function clearStoredToken() {
  localStorage.removeItem(STORAGE_KEY);
}
