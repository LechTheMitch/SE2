import { createContext } from 'react';
import type { CurrentUser, RegisterRequest, Role } from '../lib/types';

export interface AuthContextValue {
  token: string | null;
  user: CurrentUser | null;
  loading: boolean;
  login: (username: string, password: string, remember: boolean) => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: () => void;
  hasRole: (roles?: Role[]) => boolean;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);
