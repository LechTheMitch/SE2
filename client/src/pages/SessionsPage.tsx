import AddIcon from '@mui/icons-material/Add';
import {
  Alert,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
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
import { getApiErrorMessage, sessionApi } from '../lib/api';
import type { Session } from '../lib/types';

export function SessionsPage() {
  const [sessions, setSessions] = useState<Session[] | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [open, setOpen] = useState(false);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [notice, setNotice] = useState<string | null>(null);

  function loadSessions() {
    sessionApi.list().then(setSessions).catch((err) => setError(getApiErrorMessage(err)));
  }

  useEffect(() => {
    loadSessions();
  }, []);

  async function submit(event: FormEvent) {
    event.preventDefault();
    setSubmitting(true);
    setError(null);
    try {
      const created = await sessionApi.create({
        title: title.trim(),
        description: description.trim() || undefined
      });
      setSessions((items) => [created, ...(items ?? [])]);
      setNotice('Session created.');
      setOpen(false);
      setTitle('');
      setDescription('');
    } catch (err) {
      setError(getApiErrorMessage(err));
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <>
      <PageHeader
        title="Sessions"
        subtitle="Create course sessions and see which halls are attached."
        action={
          <Button startIcon={<AddIcon />} variant="contained" onClick={() => setOpen(true)}>
            New session
          </Button>
        }
      />
      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {!sessions ? (
        <CircularProgress />
      ) : (
        <Paper sx={{ overflowX: 'auto', border: '1px solid', borderColor: 'divider' }} elevation={0}>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell>ID</TableCell>
                <TableCell>Title</TableCell>
                <TableCell>Description</TableCell>
                <TableCell>Halls</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {sessions.map((session) => (
                <TableRow key={session.id}>
                  <TableCell>{session.id}</TableCell>
                  <TableCell>{session.title}</TableCell>
                  <TableCell>{session.description ?? '-'}</TableCell>
                  <TableCell>{session.halls?.map((hall) => hall.name).join(', ') || '-'}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
        </Paper>
      )}
      <Dialog open={open} onClose={() => setOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Create session</DialogTitle>
        <DialogContent>
          <Stack component="form" id="create-session-form" spacing={2} onSubmit={submit} sx={{ pt: 1 }}>
            <TextField label="Title" value={title} onChange={(event) => setTitle(event.target.value)} required autoFocus />
            <TextField
              label="Description"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              multiline
              minRows={3}
            />
          </Stack>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button type="submit" form="create-session-form" variant="contained" disabled={submitting || !title.trim()}>
            Create
          </Button>
        </DialogActions>
      </Dialog>
      <Snackbar open={!!notice} autoHideDuration={3000} onClose={() => setNotice(null)} message={notice} />
    </>
  );
}
