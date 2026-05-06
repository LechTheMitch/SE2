import AddIcon from '@mui/icons-material/Add';
import {
  Alert,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  MenuItem,
  Paper,
  Snackbar,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  TextField
} from '@mui/material';
import { FormEvent, useEffect, useState } from 'react';
import { PageHeader } from '../components/PageHeader';
import { useAuth } from '../context/useAuth';
import { getApiErrorMessage, hallApi, sessionApi } from '../lib/api';
import type { Hall, Session } from '../lib/types';

export function HallsPage() {
  const [halls, setHalls] = useState<Hall[] | null>(null);
  const [sessions, setSessions] = useState<Session[]>([]);
  const [error, setError] = useState<string | null>(null);
  const [open, setOpen] = useState(false);
  const [name, setName] = useState('');
  const [location, setLocation] = useState('');
  const [sessionId, setSessionId] = useState('');
  const [sessionTime, setSessionTime] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);
  const { hasRole } = useAuth();

  useEffect(() => {
    hallApi.list().then(setHalls).catch((err) => setError(getApiErrorMessage(err)));
    sessionApi.list().then(setSessions).catch(() => undefined);
  }, []);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const created = await hallApi.create({
        name: name.trim(),
        location: location.trim() || undefined,
        sessionId: sessionId ? Number(sessionId) : undefined,
        sessionTime: sessionTime ? new Date(sessionTime).toISOString() : undefined
      });
      setHalls((items) => [created, ...(items ?? [])]);
      setNotice('Hall created.');
      setOpen(false);
      setName('');
      setLocation('');
      setSessionId('');
      setSessionTime('');
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      <PageHeader
        title="Halls"
        subtitle="Manage physical rooms and optionally attach them to sessions."
        action={
          hasRole(['ADMIN']) ? (
            <Button startIcon={<AddIcon />} variant="contained" onClick={() => setOpen(true)}>
              New hall
            </Button>
          ) : undefined
        }
      />
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {!halls ? (
        <CircularProgress />
      ) : (
        <Paper sx={{ overflowX: 'auto', border: '1px solid', borderColor: 'divider' }} elevation={0}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Name</TableCell>
                <TableCell>Location</TableCell>
                <TableCell>Session time</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {halls.map((hall) => (
                <TableRow key={hall.id}>
                  <TableCell>{hall.id}</TableCell>
                  <TableCell>{hall.name}</TableCell>
                  <TableCell>{hall.location ?? '-'}</TableCell>
                  <TableCell>{hall.sessionTime ? new Date(hall.sessionTime).toLocaleString() : '-'}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      )}
      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Create hall</DialogTitle>
        <DialogContent>
          <Stack component="form" id="create-hall-form" spacing={2} onSubmit={submit} sx={{ pt: 1 }}>
            <TextField label="Name" value={name} onChange={(event) => setName(event.target.value)} required autoFocus />
            <TextField label="Location" value={location} onChange={(event) => setLocation(event.target.value)} />
            <TextField
              select
              label="Session"
              value={sessionId}
              onChange={(event) => setSessionId(event.target.value)}
              helperText="Optional"
            >
              <MenuItem value="">No session</MenuItem>
              {sessions.map((session) => (
                <MenuItem key={session.id} value={session.id}>
                  {session.title}
                </MenuItem>
              ))}
            </TextField>
            <TextField
              label="Session time"
              type="datetime-local"
              value={sessionTime}
              onChange={(event) => setSessionTime(event.target.value)}
              InputLabelProps={{ shrink: true }}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button type="submit" form="create-hall-form" variant="contained" disabled={submitting || !name.trim()}>
            Create
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar open={!!notice} autoHideDuration={3000} onClose={() => setNotice(null)} message={notice} />
    </>
  );
}
