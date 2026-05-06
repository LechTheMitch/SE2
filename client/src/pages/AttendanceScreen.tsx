import CheckCircleIcon from '@mui/icons-material/CheckCircle';
import ErrorIcon from '@mui/icons-material/Error';
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Grid,
  MenuItem,
  Paper,
  Snackbar,
  Stack,
  TextField,
  Typography
} from '@mui/material';
import { FormEvent, useCallback, useEffect, useMemo, useState } from 'react';
import { PageHeader } from '../components/PageHeader';
import { QrScanner } from '../components/QrScanner';
import { attendanceApi, getApiErrorMessage, hallApi, sessionApi } from '../lib/api';
import type { AttendanceRecord, Hall, Session } from '../lib/types';

type QueueItem = {
  token: string;
  status: 'success' | 'error';
  message: string;
  record?: AttendanceRecord;
  at: string;
};

export function AttendanceScreen() {
  const [sessions, setSessions] = useState<Session[]>([]);
  const [halls, setHalls] = useState<Hall[]>([]);
  const [sessionId, setSessionId] = useState('');
  const [hallId, setHallId] = useState('');
  const [manualToken, setManualToken] = useState('');
  const [queue, setQueue] = useState<QueueItem[]>([]);
  const [pending, setPending] = useState(false);
  const [loadError, setLoadError] = useState<string | null>(null);
  const [snackbar, setSnackbar] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([sessionApi.list(), hallApi.list()])
      .then(([loadedSessions, loadedHalls]) => {
        setSessions(loadedSessions);
        setHalls(loadedHalls);
      })
      .catch((err) => setLoadError(getApiErrorMessage(err)));
  }, []);

  const selectedSession = sessions.find((session) => session.id === Number(sessionId));
  const selectedHall = halls.find((hall) => hall.id === Number(hallId));
  const ready = Boolean(sessionId && hallId);

  const submitScan = useCallback(
    async (qrCode: string) => {
      const token = qrCode.trim();
      if (!ready) {
        setSnackbar('Select a session and hall before scanning.');
        return;
      }
      if (!token || pending) return;

      setPending(true);
      try {
        const record = await attendanceApi.scan({
          qrCode: token,
          sessionId: Number(sessionId),
          hallId: Number(hallId)
        });
        const message = `Recorded student ${record.studentId} at ${new Date(record.attendanceDate).toLocaleString()}`;
        const item: QueueItem = { token, status: 'success', message, record, at: new Date().toISOString() };
        setQueue((items) => [item, ...items].slice(0, 5));
        setSnackbar(message);
      } catch (err) {
        const message = getApiErrorMessage(err);
        const item: QueueItem = { token, status: 'error', message, at: new Date().toISOString() };
        setQueue((items) => [item, ...items].slice(0, 5));
        setSnackbar(message);
      } finally {
        setPending(false);
      }
    },
    [hallId, pending, ready, sessionId]
  );

  function submitManual(event: FormEvent) {
    event.preventDefault();
    submitScan(manualToken);
    setManualToken('');
  }

  const scannerDisabled = useMemo(() => !ready || pending, [pending, ready]);

  return (
    <>
      <PageHeader title="Attendance scanner" />
      {loadError && <Alert severity="error" sx={{ mb: 2 }}>{loadError}</Alert>}
      <Grid container spacing={2}>
        <Grid size={{ xs: 12, md: 4 }}>
          <Paper sx={{ p: 2 }}>
            <Stack spacing={2}>
              <TextField
                select
                label="Session"
                value={sessionId}
                onChange={(event) => setSessionId(event.target.value)}
                required
                fullWidth
              >
                {sessions.map((session) => (
                  <MenuItem key={session.id} value={session.id}>
                    {session.title}
                  </MenuItem>
                ))}
              </TextField>
              <TextField
                select
                label="Hall"
                value={hallId}
                onChange={(event) => setHallId(event.target.value)}
                required
                fullWidth
              >
                {halls.map((hall) => (
                  <MenuItem key={hall.id} value={hall.id}>
                    {hall.name}
                  </MenuItem>
                ))}
              </TextField>
              <Alert severity={ready ? 'success' : 'info'}>
                {ready
                  ? `Scanning for ${selectedSession?.title ?? 'session'} in ${selectedHall?.name ?? 'hall'}`
                  : 'Choose a session and hall to enable scanning.'}
              </Alert>
              <Box component="form" onSubmit={submitManual}>
                <Stack spacing={1}>
                  <TextField
                    label="Manual QR token"
                    value={manualToken}
                    onChange={(event) => setManualToken(event.target.value)}
                    disabled={pending}
                    fullWidth
                    inputProps={{ 'aria-label': 'Manual QR token' }}
                  />
                  <Button type="submit" variant="contained" disabled={!ready || pending || !manualToken.trim()}>
                    Submit token
                  </Button>
                </Stack>
              </Box>
            </Stack>
          </Paper>
        </Grid>
        <Grid size={{ xs: 12, md: 8 }}>
          <Paper sx={{ p: 2 }}>
            <Stack spacing={2}>
              <Box sx={{ position: 'relative' }}>
                <QrScanner onScan={submitScan} disabled={scannerDisabled} cooldownMs={2000} />
                {pending && (
                  <Box sx={{ position: 'absolute', inset: 0, display: 'grid', placeItems: 'center', bgcolor: 'rgba(255,255,255,.58)' }}>
                    <CircularProgress aria-label="Recording attendance" />
                  </Box>
                )}
              </Box>
              <Typography variant="h6">Recent scans</Typography>
              <Stack spacing={1} aria-live="polite">
                {queue.length === 0 && <Typography color="text.secondary">No scans yet.</Typography>}
                {queue.map((item) => (
                  <Alert key={`${item.at}-${item.token}`} severity={item.status} icon={item.status === 'success' ? <CheckCircleIcon /> : <ErrorIcon />}>
                    {item.message}
                  </Alert>
                ))}
              </Stack>
            </Stack>
          </Paper>
        </Grid>
      </Grid>
      <Snackbar open={!!snackbar} autoHideDuration={4000} onClose={() => setSnackbar(null)} message={snackbar} />
    </>
  );
}
