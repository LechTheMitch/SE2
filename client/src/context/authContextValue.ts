import { createContext } from 'react';
import type { CurrentUser, Role } from '../lib/types';

export interface AuthContextValue {
  token: string | null;
  user: CurrentUser | null;
  loading: boolean;
  login: (username: string, password: string, remember: boolean) => Promise<void>;
  logout: () => void;
  hasRole: (roles?: Role[]) => boolean;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);
