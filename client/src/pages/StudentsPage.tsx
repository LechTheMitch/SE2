import QrCode2Icon from '@mui/icons-material/QrCode2';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import {
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  Paper,
  Stack,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  Tooltip
} from '@mui/material';
import { useEffect, useState } from 'react';
import { Link } from 'react-router';
import { PageHeader } from '../components/PageHeader';
import { QrImage } from '../components/QrImage';
import { getApiErrorMessage, studentApi } from '../lib/api';
import type { Student } from '../lib/types';

export function StudentsPage() {
  const [students, setStudents] = useState<Student[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showTokens, setShowTokens] = useState(false);
  const [qrStudent, setQrStudent] = useState<Student | null>(null);

  useEffect(() => {
    studentApi
      .list()
      .then(setStudents)
      .catch((err) => setError(getApiErrorMessage(err)))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <CircularProgress aria-label="Loading students" />;

  return (
    <>
      <PageHeader
        title="Students"
        action={
          <Tooltip title={showTokens ? 'Hide QR tokens' : 'Show QR tokens'}>
            <IconButton aria-label={showTokens ? 'Hide QR tokens' : 'Show QR tokens'} onClick={() => setShowTokens((v) => !v)}>
              {showTokens ? <VisibilityOffIcon /> : <VisibilityIcon />}
            </IconButton>
          </Tooltip>
        }
      />
      {error && <Box role="alert">{error}</Box>}
      <Paper sx={{ overflowX: 'auto' }}>
        <Table aria-label="Students table">
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>Name</TableCell>
              <TableCell>Parent phone</TableCell>
              <TableCell>QR token</TableCell>
              <TableCell align="right">Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {students.map((student) => (
              <TableRow key={student.id}>
                <TableCell>{student.id}</TableCell>
                <TableCell>{student.firstName ? `${student.firstName} ${student.lastName ?? ''}` : `Student ${student.id}`}</TableCell>
                <TableCell>{student.parentPhoneNumber ?? '-'}</TableCell>
                <TableCell sx={{ maxWidth: 280, wordBreak: 'break-all' }}>{showTokens ? student.qrCode : '••••••••••••'}</TableCell>
                <TableCell align="right">
                  <Stack direction="row" spacing={1} justifyContent="flex-end">
                    <Button component={Link} to={`/students/${student.id}`} size="small">
                      Details
                    </Button>
                    <Button startIcon={<QrCode2Icon />} onClick={() => setQrStudent(student)} size="small" variant="outlined">
                      QR
                    </Button>
                  </Stack>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </Paper>
      <Dialog open={!!qrStudent} onClose={() => setQrStudent(null)}>
        <DialogTitle>{qrStudent && `Student ${qrStudent.id} QR`}</DialogTitle>
        <DialogContent>{qrStudent && <QrImage studentId={qrStudent.id} />}</DialogContent>
      </Dialog>
    </>
  );
}
