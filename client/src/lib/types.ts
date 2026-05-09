export type Role = 'ADMIN' | 'TEACHING_ASSISTANT' | 'STUDENT' | 'PARENT' | string;

export interface AuthResponse {
  token: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName?: string;
  email: string;
  password?: string;
  phoneNumber: string;
  role: Role;
}

export interface CurrentUser {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  role: Role | null;
  permissions: string[];
  forcePasswordChange: boolean;
  forceEmailChange: boolean;
  parentId?: number | null;
  studentId?: number | null;
}

export type UserProfile = CurrentUser;

export interface Student {
  id: number;
  userId?: number;
  parentId?: number;
  parentPhoneNumber?: string;
  qrCode: string;
  firstName?: string;
  lastName?: string;
  email?: string;
}

export interface Hall {
  id: number;
  name: string;
  location?: string;
  sessionTime?: string;
  sessionId?: number;
}

export interface CreateHallRequest {
  name: string;
  location?: string;
  sessionTime?: string;
  sessionId?: number;
}

export interface Session {
  id: number;
  title: string;
  description?: string;
  halls?: Hall[];
}

export interface CreateSessionRequest {
  title: string;
  description?: string;
}

export interface AttendanceRecord {
  id: number;
  studentId: number;
  sessionId: number;
  hallId: number;
  attendanceDate: string;
  studentName?: string;
}

export interface AttendanceScanRequest {
  qrCode: string;
  sessionId: number;
  hallId: number;
  attendanceDate?: string;
}

export interface ApiErrorPayload {
  message?: string;
  error?: string;
  detail?: string;
  timestamp?: string;
  status?: number;
}
