import AdminPanelSettingsIcon from '@mui/icons-material/AdminPanelSettings';
import PersonIcon from '@mui/icons-material/Person';
import {
  Alert,
  Box,
  Chip,
  CircularProgress,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Typography
} from '@mui/material';
import { useEffect, useMemo, useState } from 'react';
import { PageHeader } from '../components/PageHeader';
import { getApiErrorMessage, userApi } from '../lib/api';
import type { UserProfile } from '../lib/types';

export function UsersPage() {
  const [users, setUsers] = useState<UserProfile[] | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    userApi.list().then(setUsers).catch((err) => setError(getApiErrorMessage(err)));
  }, []);

  const roleCounts = useMemo(() => {
    return (users ?? []).reduce<Record<string, number>>((counts, user) => {
      const role = user.role ?? 'UNASSIGNED';
      counts[role] = (counts[role] ?? 0) + 1;
      return counts;
    }, {});
  }, [users]);

  return (
    <>
      <PageHeader title="Users" subtitle="Admin-only view of accounts, roles, and profile flags." />
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {!users ? (
        <CircularProgress />
      ) : (
        <Stack spacing={2}>
          <Stack direction="row" spacing={1} useFlexGap flexWrap="wrap">
            {Object.entries(roleCounts).map(([role, count]) => (
              <Chip key={role} icon={<AdminPanelSettingsIcon />} label={`${role}: ${count}`} />
            ))}
          </Stack>
          <Paper sx={{ overflowX: 'auto', border: '1px solid', borderColor: 'divider' }} elevation={0}>
            <Table aria-label="Users table">
              <TableHead>
                <TableRow>
                  <TableCell>User</TableCell>
                  <TableCell>Email</TableCell>
                  <TableCell>Phone</TableCell>
                  <TableCell>Role</TableCell>
                  <TableCell>Permissions</TableCell>
                  <TableCell>Flags</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {users.map((user) => (
                  <TableRow key={user.userId} hover>
                    <TableCell>
                      <Stack direction="row" spacing={1.5} alignItems="center">
                        <Box
                          sx={{
                            width: 36,
                            height: 36,
                            borderRadius: '50%',
                            display: 'grid',
                            placeItems: 'center',
                            bgcolor: 'rgba(15, 118, 110, 0.1)',
                            color: 'primary.main'
                          }}
                        >
                          <PersonIcon fontSize="small" />
                        </Box>
                        <Box>
                          <Typography sx={{ fontWeight: 700 }}>
                            {user.firstName} {user.lastName}
                          </Typography>
                          <Typography variant="caption" color="text.secondary">
                            ID {user.userId}
                          </Typography>
                        </Box>
                      </Stack>
                    </TableCell>
                    <TableCell>{user.email}</TableCell>
                    <TableCell>{user.phoneNumber ?? '-'}</TableCell>
                    <TableCell>
                      <Chip size="small" label={user.role ?? 'Unassigned'} color={user.role === 'ADMIN' ? 'primary' : 'default'} />
                    </TableCell>
                    <TableCell sx={{ maxWidth: 280 }}>
                      <Stack direction="row" spacing={0.5} useFlexGap flexWrap="wrap">
                        {user.permissions.length ? (
                          user.permissions.map((permission) => <Chip key={permission} size="small" label={permission} variant="outlined" />)
                        ) : (
                          <Typography variant="body2" color="text.secondary">
                            None
                          </Typography>
                        )}
                      </Stack>
                    </TableCell>
                    <TableCell>
                      <Stack direction="row" spacing={0.5} useFlexGap flexWrap="wrap">
                        {user.forceEmailChange && <Chip size="small" color="warning" label="Email change" />}
                        {user.forcePasswordChange && <Chip size="small" color="warning" label="Password change" />}
                        {!user.forceEmailChange && !user.forcePasswordChange && (
                          <Typography variant="body2" color="text.secondary">
                            Clear
                          </Typography>
                        )}
                      </Stack>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </Paper>
        </Stack>
      )}
    </>
  );
}
