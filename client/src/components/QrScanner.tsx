import CameraswitchIcon from '@mui/icons-material/Cameraswitch';
import { BrowserMultiFormatReader, IScannerControls } from '@zxing/browser';
import { Alert, Box, Button, CircularProgress, Stack, Typography } from '@mui/material';
import { useEffect, useRef, useState } from 'react';

interface QrScannerProps {
  onScan: (token: string) => void;
  disabled?: boolean;
  cooldownMs?: number;
}

export function QrScanner({ onScan, disabled = false, cooldownMs = 2000 }: QrScannerProps) {
  const videoRef = useRef<HTMLVideoElement | null>(null);
  const controlsRef = useRef<IScannerControls | null>(null);
  const lastScanRef = useRef<{ value: string; at: number } | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [starting, setStarting] = useState(false);
  const [cameraKey, setCameraKey] = useState(0);

  useEffect(() => {
    if (disabled) {
      controlsRef.current?.stop();
      controlsRef.current = null;
      return;
    }

    let active = true;
    const reader = new BrowserMultiFormatReader();

    async function start() {
      setStarting(true);
      setError(null);
      try {
        if (!videoRef.current) return;
        controlsRef.current = await reader.decodeFromVideoDevice(undefined, videoRef.current, (result) => {
          const text = result?.getText();
          if (!text) return;
          const now = Date.now();
          const last = lastScanRef.current;
          if (last?.value === text && now - last.at < cooldownMs) return;
          lastScanRef.current = { value: text, at: now };
          onScan(text);
        });
      } catch (err) {
        if (active) setError(err instanceof Error ? err.message : 'Camera scanner could not start.');
      } finally {
        if (active) setStarting(false);
      }
    }

    start();
    return () => {
      active = false;
      controlsRef.current?.stop();
      controlsRef.current = null;
    };
  }, [cameraKey, cooldownMs, disabled, onScan]);

  return (
    <Stack spacing={1.5}>
      <Box
        sx={{
          position: 'relative',
          overflow: 'hidden',
          borderRadius: 1,
          bgcolor: 'grey.900',
          aspectRatio: '16 / 10',
          minHeight: 260
        }}
      >
        <Box
          component="video"
          ref={videoRef}
          muted
          aria-label="QR camera scanner"
          sx={{ width: '100%', height: '100%', objectFit: 'cover' }}
        />
        {(starting || disabled) && (
          <Box sx={{ position: 'absolute', inset: 0, display: 'grid', placeItems: 'center', bgcolor: 'rgba(0,0,0,.48)' }}>
            {starting ? <CircularProgress color="inherit" aria-label="Starting camera" /> : <Typography color="white">Select a session and hall</Typography>}
          </Box>
        )}
      </Box>
      {error && <Alert severity="warning">{error}</Alert>}
      <Button startIcon={<CameraswitchIcon />} onClick={() => setCameraKey((key) => key + 1)} disabled={disabled || starting}>
        Restart camera
      </Button>
    </Stack>
  );
}
