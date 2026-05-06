import '@fontsource/roboto/300.css';
import '@fontsource/roboto/400.css';
import '@fontsource/roboto/500.css';
import '@fontsource/roboto/700.css';
import { CssBaseline, ThemeProvider, createTheme } from '@mui/material';
import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter, Route, Routes } from 'react-router';
import { AppShell } from './components/AppShell';
import { ProtectedRoute } from './components/ProtectedRoute';
import { AuthProvider } from './context/AuthContext';
import { AttendanceRecordsPage } from './pages/AttendanceRecordsPage';
import { AttendanceScreen } from './pages/AttendanceScreen';
import { Dashboard } from './pages/Dashboard';
import { HallsPage } from './pages/HallsPage';
import { LoginPage } from './pages/LoginPage';
import { SessionsPage } from './pages/SessionsPage';
import { StudentDetail } from './pages/StudentDetail';
import { StudentsPage } from './pages/StudentsPage';
import { UsersPage } from './pages/UsersPage';

const theme = createTheme({
  palette: {
    mode: 'light',
    primary: { main: '#0f766e' },
    secondary: { main: '#4f46e5' },
    background: { default: '#f5f7fb', paper: '#ffffff' }
  },
  shape: { borderRadius: 8 },
  typography: {
    fontFamily: '"Roboto", "Helvetica", "Arial", sans-serif',
    h4: { fontWeight: 800 },
    h5: { fontWeight: 800 },
    h6: { fontWeight: 700 },
    button: { textTransform: 'none', fontWeight: 700 }
  },
  components: {
    MuiPaper: {
      styleOverrides: {
        root: {
          backgroundImage: 'none'
        }
      }
    },
    MuiButton: {
      defaultProps: {
        disableElevation: true
      }
    },
    MuiTableCell: {
      styleOverrides: {
        head: {
          fontWeight: 800,
          color: '#475569',
          backgroundColor: '#f8fafc'
        }
      }
    }
  }
});

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <BrowserRouter>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route element={<ProtectedRoute />}>
              <Route element={<AppShell />}>
                <Route index element={<Dashboard />} />
                <Route path="students" element={<StudentsPage />} />
                <Route path="students/:id" element={<StudentDetail />} />
                <Route element={<ProtectedRoute roles={['ADMIN', 'TEACHING_ASSISTANT']} />}>
                  <Route path="attendance" element={<AttendanceScreen />} />
                  <Route path="records" element={<AttendanceRecordsPage />} />
                  <Route path="sessions" element={<SessionsPage />} />
                  <Route path="halls" element={<HallsPage />} />
                </Route>
                <Route element={<ProtectedRoute roles={['ADMIN']} />}>
                  <Route path="users" element={<UsersPage />} />
                </Route>
              </Route>
            </Route>
          </Routes>
        </AuthProvider>
      </BrowserRouter>
    </ThemeProvider>
  </StrictMode>
);
