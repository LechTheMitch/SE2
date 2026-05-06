import DownloadIcon from '@mui/icons-material/Download';
import RefreshIcon from '@mui/icons-material/Refresh';
import { Box, Button, CircularProgress, Stack, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { getApiErrorMessage, studentApi } from '../lib/api';

interface QrImageProps {
  studentId: number;
  size?: number;
}

export function QrImage({ studentId, size = 300 }: QrImageProps) {
  const [url, setUrl] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);
  const [reloadKey, setReloadKey] = useState(0);

  useEffect(() => {
    let objectUrl: string | null = null;
    let cancelled = false;

    async function load() {
      setLoading(true);
      setError(null);
      try {
        const blob = await studentApi.getQrBlob(studentId, size, size);
        objectUrl = URL.createObjectURL(blob);
        if (!cancelled) setUrl(objectUrl);
      } catch (err) {
        if (!cancelled) setError(getApiErrorMessage(err));
      } finally {
        if (!cancelled) setLoading(false);
      }
    }

    load();
    return () => {
      cancelled = true;
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [reloadKey, size, studentId]);

  const download = () => {
    if (!url) return;
    const link = document.createElement('a');
    link.href = url;
    link.download = `student-${studentId}-qr.png`;
    link.click();
  };

  if (loading) {
    return (
      <Box sx={{ width: size, height: size, display: 'grid', placeItems: 'center' }}>
        <CircularProgress aria-label="Loading QR image" />
      </Box>
    );
  }

  if (error) {
    return (
      <Stack spacing={1}>
        <Typography role="alert" color="error">
          {error}
        </Typography>
        <Button startIcon={<RefreshIcon />} onClick={() => setReloadKey((key) => key + 1)}>
          Retry
        </Button>
      </Stack>
    );
  }

  return (
    <Stack spacing={1.5} alignItems="flex-start">
      {url && <Box component="img" src={url} alt={`Student ${studentId} QR code`} sx={{ width: size, maxWidth: '100%' }} />}
      <Button variant="outlined" startIcon={<DownloadIcon />} onClick={download} disabled={!url}>
        Download PNG
      </Button>
    </Stack>
  );
}
