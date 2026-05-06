import axios, { AxiosError, AxiosInstance } from 'axios';
import type {
  ApiErrorPayload,
  AttendanceRecord,
  AttendanceScanRequest,
  AuthResponse,
  CreateHallRequest,
  CreateSessionRequest,
  CurrentUser,
  Hall,
  Session,
  Student,
  UserProfile
} from './types';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

let authToken: string | null = null;
let onUnauthorized: (() => void) | null = null;

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  timeout: 15_000
});

apiClient.interceptors.request.use((config) => {
  if (authToken) {
    config.headers.Authorization = `Bearer ${authToken}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ApiErrorPayload | string>) => {
    if (error.response?.status === 401) {
      onUnauthorized?.();
    }
    return Promise.reject(error);
  }
);

export function setAuthToken(token: string | null) {
  authToken = token;
}

export function setUnauthorizedHandler(handler: (() => void) | null) {
  onUnauthorized = handler;
}

export function getApiErrorMessage(error: unknown): string {
  if (axios.isAxiosError<ApiErrorPayload | string>(error)) {
    const data = error.response?.data;
    if (typeof data === 'string') return data;
    return data?.message ?? data?.detail ?? data?.error ?? error.message;
  }
  return error instanceof Error ? error.message : 'Something went wrong.';
}

async function withRetry<T>(operation: () => Promise<T>, attempts = 2): Promise<T> {
  let lastError: unknown;
  for (let attempt = 0; attempt < attempts; attempt += 1) {
    try {
      return await operation();
    } catch (error) {
      lastError = error;
      if (!axios.isAxiosError(error) || !error.response || error.response.status < 500) {
        throw error;
      }
    }
  }
  throw lastError;
}

export const authApi = {
  async login(username: string, password: string): Promise<AuthResponse> {
    const credentials = { emailOrPhone: username, password };
    try {
      const { data } = await apiClient.post<AuthResponse>('/login', credentials);
      return data;
    } catch (error) {
      if (axios.isAxiosError(error) && error.response?.status === 404) {
        const { data } = await apiClient.post<AuthResponse>('/api/auth/login', {
          ...credentials,
          email: username,
          username
        });
        return data;
      }
      throw error;
    }
  },
  async me(): Promise<CurrentUser> {
    const { data } = await apiClient.get<CurrentUser>('/api/users/me');
    return data;
  }
};

export const studentApi = {
  async list(): Promise<Student[]> {
    return withRetry(async () => (await apiClient.get<Student[]>('/api/students')).data);
  },
  async get(id: number): Promise<Student> {
    return withRetry(async () => (await apiClient.get<Student>(`/api/students/${id}`)).data);
  },
  async getQrBlob(id: number, width = 300, height = 300): Promise<Blob> {
    const { data } = await apiClient.get<ArrayBuffer>(`/api/students/${id}/qr`, {
      params: { width, height },
      responseType: 'arraybuffer'
    });
    return new Blob([data], { type: 'image/png' });
  }
};

export const sessionApi = {
  async list(): Promise<Session[]> {
    return withRetry(async () => (await apiClient.get<Session[]>('/api/sessions')).data);
  },
  async create(payload: CreateSessionRequest): Promise<Session> {
    const { data } = await apiClient.post<Session>('/api/sessions', payload);
    return data;
  }
};

export const hallApi = {
  async list(): Promise<Hall[]> {
    return withRetry(async () => (await apiClient.get<Hall[]>('/api/halls')).data);
  },
  async create(payload: CreateHallRequest): Promise<Hall> {
    const { data } = await apiClient.post<Hall>('/api/halls', payload);
    return data;
  }
};

export const userApi = {
  async list(): Promise<UserProfile[]> {
    return withRetry(async () => (await apiClient.get<UserProfile[]>('/api/users')).data);
  }
};

export const attendanceApi = {
  async scan(payload: AttendanceScanRequest): Promise<AttendanceRecord> {
    const { data } = await apiClient.post<AttendanceRecord>('/api/attendance-records/scan', payload);
    return data;
  },
  async list(filters?: { sessionId?: number; hallId?: number; studentId?: number }): Promise<AttendanceRecord[]> {
    if (filters?.studentId) {
      const { data } = await apiClient.get<AttendanceRecord[]>(
        `/api/attendance-records/student/${filters.studentId}`
      );
      return data;
    }
    if (filters?.sessionId) {
      const { data } = await apiClient.get<AttendanceRecord[]>(
        `/api/attendance-records/session/${filters.sessionId}`
      );
      return filters.hallId ? data.filter((record) => record.hallId === filters.hallId) : data;
    }
    if (filters?.hallId) {
      const { data } = await apiClient.get<AttendanceRecord[]>(
        `/api/attendance-records/hall/${filters.hallId}`
      );
      return data;
    }
    const { data } = await apiClient.get<AttendanceRecord[]>('/api/attendance-records');
    return data;
  }
};
