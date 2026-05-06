import { Box, CircularProgress, Typography } from '@mui/material';
import { Navigate, Outlet, useLocation } from 'react-router';
import { useAuth } from '../context/useAuth';
import type { Role } from '../lib/types';

export function ProtectedRoute({ roles }: { roles?: Role[] }) {
  const { token, loading, hasRole } = useAuth();
  const location = useLocation();

  if (loading) {
    return (
      <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center' }}>
        <CircularProgress aria-label="Loading session" />
      </Box>
    );
  }

  if (!token) {
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  if (!hasRole(roles)) {
    return (
      <Box sx={{ p: 4 }}>
        <Typography variant="h5">Access denied</Typography>
        <Typography color="text.secondary">Your account does not have access to this page.</Typography>
      </Box>
    );
  }

  return <Outlet />;
}
