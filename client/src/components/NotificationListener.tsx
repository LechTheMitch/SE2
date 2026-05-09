import { useEffect, useState } from 'react';
import { Snackbar, Alert, AlertTitle } from '@mui/material';
import { useAuth } from '../context/useAuth';

interface AttendanceNotification {
  studentName: string;
  studentEmail: string;
  sessionName: string;
  status: string;
  timestamp: string;
}

export function NotificationListener() {
  const { user } = useAuth();
  const [notification, setNotification] = useState<AttendanceNotification | null>(null);
  const [open, setOpen] = useState(false);

  useEffect(() => {
    if (!user?.email) return;

    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
    // The notification service is likely routed through the gateway or on port 8081
    // Given the architecture, let's try the gateway route first, or fall back to direct if needed.
    const sseUrl = `${API_BASE_URL}/api/notifications/subscribe/${user.email}`;
    console.log(`Subscribing to notifications at: ${sseUrl}`);
    
    const eventSource = new EventSource(sseUrl, { withCredentials: true });

    eventSource.onopen = () => {
      console.log('SSE connection opened successfully');
    };

    // Listen for the specific named event from the backend
    eventSource.addEventListener('ATTENDANCE', (event) => {
      console.log('Received ATTENDANCE notification:', event.data);
      try {
        const data: AttendanceNotification = JSON.parse(event.data);
        setNotification(data);
        setOpen(true);
      } catch (error) {
        console.error('Failed to parse SSE message', error);
      }
    });

    // Handle initial connection confirmation
    eventSource.addEventListener('INIT', (event) => {
      console.log('Notification service initial event:', event.data);
    });

    eventSource.onmessage = (event) => {
      console.log('Received generic SSE message:', event.data);
    };

    eventSource.onerror = (error) => {
      console.error('SSE connection error', error);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [user?.email]);

  const handleClose = () => {
    setOpen(false);
  };

  if (!notification) return null;

  return (
    <Snackbar
      open={open}
      autoHideDuration={6000}
      onClose={handleClose}
      anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
    >
      <Alert onClose={handleClose} severity="success" variant="filled" sx={{ width: '100%' }}>
        <AlertTitle>Attendance Recorded</AlertTitle>
        <strong>{notification.studentName}</strong> has been marked as{' '}
        <strong>{notification.status}</strong> for <strong>{notification.sessionName}</strong>.
      </Alert>
    </Snackbar>
  );
}
