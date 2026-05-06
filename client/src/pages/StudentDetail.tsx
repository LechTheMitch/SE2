import { Button, CircularProgress, Paper, Stack, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router';
import { PageHeader } from '../components/PageHeader';
import { QrImage } from '../components/QrImage';
import { getApiErrorMessage, studentApi } from '../lib/api';
import type { Student } from '../lib/types';

export function StudentDetail() {
  const { id } = useParams();
  const studentId = Number(id);
  const [student, setStudent] = useState<Student | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!studentId) return;
    studentApi.get(studentId).then(setStudent).catch((err) => setError(getApiErrorMessage(err)));
  }, [studentId]);

  if (!student && !error) return <CircularProgress aria-label="Loading student" />;

  return (
    <>
      <PageHeader title={`Student ${studentId}`} action={<Button component={Link} to="/students">Back</Button>} />
      {error && <Typography color="error">{error}</Typography>}
      {student && (
        <Paper sx={{ p: 3 }}>
          <Stack spacing={2}>
            <Typography>Parent phone: {student.parentPhoneNumber ?? '-'}</Typography>
            <Typography sx={{ wordBreak: 'break-all' }}>QR token: {student.qrCode}</Typography>
            <QrImage studentId={student.id} />
          </Stack>
        </Paper>
      )}
    </>
  );
}
