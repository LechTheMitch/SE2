import LockIcon from '@mui/icons-material/Lock';
import {
  Alert,
  Box,
  Button,
  Checkbox,
  FormControlLabel,
  Paper,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import { FormEvent, useState } from 'react';
import { Navigate, useLocation, useNavigate, Link } from 'react-router';
import { useAuth } from '../context/useAuth';
import { getApiErrorMessage } from '../lib/api';
import { Link as MuiLink } from '@mui/material';

export function LoginPage() {
  const { login, token } = useAuth();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [remember, setRemember] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  if (token) return <Navigate to="/" replace />;

  async function submit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      await login(username, password, remember);
      const from = (location.state as { from?: Location } | null)?.from?.pathname ?? '/';
      navigate(from, { replace: true });
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <Box sx={{ minHeight: '100vh', display: 'grid', placeItems: 'center', bgcolor: 'background.default', p: 2 }}>
      <Paper sx={{ width: '100%', maxWidth: 420, p: 4 }} elevation={3}>
        <Stack component="form" spacing={2.5} onSubmit={submit}>
          <LockIcon color="primary" fontSize="large" />
          <Typography variant="h4" component="h1">
            Sign in
          </Typography>
          {error && <Alert severity="error">{error}</Alert>}
          <TextField
            label="Email or username"
            value={username}
            onChange={(event) => setUsername(event.target.value)}
            autoComplete="username"
            required
            fullWidth
          />
          <TextField
            label="Password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            autoComplete="current-password"
            required
            fullWidth
          />
          <FormControlLabel
            control={<Checkbox checked={remember} onChange={(event) => setRemember(event.target.checked)} />}
            label="Remember me"
          />
          <Button type="submit" variant="contained" size="large" disabled={submitting}>
            {submitting ? 'Signing in...' : 'Login'}
          </Button>

          <Typography variant="body2" textAlign="center" color="text.secondary">
            Don't have an account?{' '}
            <MuiLink component={Link} to="/register" sx={{ fontWeight: 700, textDecoration: 'none' }}>
              Sign Up
            </MuiLink>
          </Typography>
        </Stack>
      </Paper>
    </Box>
  );
}
