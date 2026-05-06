import DownloadIcon from '@mui/icons-material/Download';
import { Button, MenuItem, Paper, Stack, Table, TableBody, TableCell, TableHead, TableRow, TextField } from '@mui/material';
import { useEffect, useState } from 'react';
import { PageHeader } from '../components/PageHeader';
import { attendanceApi, hallApi, sessionApi } from '../lib/api';
import type { AttendanceRecord, Hall, Session } from '../lib/types';

function toCsv(records: AttendanceRecord[]) {
  const rows = [['id', 'studentId', 'sessionId', 'hallId', 'attendanceDate']];
  records.forEach((record) => rows.push([record.id, record.studentId, record.sessionId, record.hallId, record.attendanceDate].map(String)));
  return rows.map((row) => row.map((cell) => `"${cell.replace(/"/g, '""')}"`).join(',')).join('\n');
}

export function AttendanceRecordsPage() {
  const [records, setRecords] = useState<AttendanceRecord[]>([]);
  const [sessions, setSessions] = useState<Session[]>([]);
  const [halls, setHalls] = useState<Hall[]>([]);
  const [sessionId, setSessionId] = useState('');
  const [hallId, setHallId] = useState('');

  useEffect(() => {
    Promise.all([sessionApi.list(), hallApi.list()]).then(([loadedSessions, loadedHalls]) => {
      setSessions(loadedSessions);
      setHalls(loadedHalls);
    });
  }, []);

  useEffect(() => {
    attendanceApi
      .list({ sessionId: sessionId ? Number(sessionId) : undefined, hallId: hallId ? Number(hallId) : undefined })
      .then(setRecords);
  }, [hallId, sessionId]);

  function exportCsv() {
    const url = URL.createObjectURL(new Blob([toCsv(records)], { type: 'text/csv' }));
    const link = document.createElement('a');
    link.href = url;
    link.download = 'attendance-records.csv';
    link.click();
    URL.revokeObjectURL(url);
  }

  return (
    <>
      <PageHeader title="Attendance records" action={<Button startIcon={<DownloadIcon />} onClick={exportCsv}>Export CSV</Button>} />
      <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2} sx={{ mb: 2 }}>
        <TextField select label="Session" value={sessionId} onChange={(event) => setSessionId(event.target.value)} sx={{ minWidth: 220 }}>
          <MenuItem value="">All sessions</MenuItem>
          {sessions.map((session) => <MenuItem key={session.id} value={session.id}>{session.title}</MenuItem>)}
        </TextField>
        <TextField select label="Hall" value={hallId} onChange={(event) => setHallId(event.target.value)} sx={{ minWidth: 220 }}>
          <MenuItem value="">All halls</MenuItem>
          {halls.map((hall) => <MenuItem key={hall.id} value={hall.id}>{hall.name}</MenuItem>)}
        </TextField>
      </Stack>
      <Paper sx={{ overflowX: 'auto' }}>
        <Table aria-label="Attendance records table">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Student</TableCell>
              <TableCell>Session</TableCell>
              <TableCell>Hall</TableCell>
              <TableCell>Time</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {records.map((record) => (
              <TableRow key={record.id}>
                <TableCell>{record.id}</TableCell>
                <TableCell>{record.studentId}</TableCell>
                <TableCell>{record.sessionId}</TableCell>
                <TableCell>{record.hallId}</TableCell>
                <TableCell>{new Date(record.attendanceDate).toLocaleString()}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
    </>
  );
}
