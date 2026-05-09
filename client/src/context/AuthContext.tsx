import { jwtDecode } from 'jwt-decode';
import { ReactNode, useCallback, useEffect, useMemo, useState } from 'react';
import { authApi, setAuthToken, setUnauthorizedHandler } from '../lib/api';
import { clearStoredToken, readStoredToken, writeStoredToken } from '../lib/authStorage';
import type { CurrentUser, RegisterRequest, Role } from '../lib/types';
import { AuthContext } from './authContextValue';

interface JwtPayload {
  exp?: number;
}

function isExpired(token: string): boolean {
  try {
    const decoded = jwtDecode<JwtPayload>(token);
    return typeof decoded.exp === 'number' && decoded.exp * 1000 <= Date.now();
  } catch {
    return true;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => readStoredToken());
  const [user, setUser] = useState<CurrentUser | null>(null);
  const [loading, setLoading] = useState(true);

  const logout = useCallback(() => {
    setToken(null);
    setUser(null);
    setAuthToken(null);
    clearStoredToken();
  }, []);

  useEffect(() => {
    setUnauthorizedHandler(logout);
    return () => setUnauthorizedHandler(null);
  }, [logout]);

  useEffect(() => {
    let cancelled = false;
    async function restore() {
      if (!token || isExpired(token)) {
        logout();
        setLoading(false);
        return;
      }
      setAuthToken(token);
      try {
        const profile = await authApi.me();
        if (!cancelled) setUser(profile);
      } catch {
        if (!cancelled) logout();
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    restore();
    return () => {
      cancelled = true;
    };
  }, [logout, token]);

  const login = useCallback(async (username: string, password: string, remember: boolean) => {
    const response = await authApi.login(username, password);
    setAuthToken(response.token);
    if (remember) writeStoredToken(response.token);
    else clearStoredToken();
    const profile = await authApi.me();
    setToken(response.token);
    setUser(profile);
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    const response = await authApi.register(data);
    setAuthToken(response.token);
    clearStoredToken(); // Don't persist on register by default, or could make it optional
    const profile = await authApi.me();
    setToken(response.token);
    setUser(profile);
  }, []);

  const hasRole = useCallback(
    (roles?: Role[]) => !roles?.length || (!!user?.role && roles.includes(user.role)),
    [user]
  );

  const value = useMemo(
    () => ({ token, user, loading, login, register, logout, hasRole }),
    [token, user, loading, login, register, logout, hasRole]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
