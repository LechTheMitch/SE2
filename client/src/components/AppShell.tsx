import LogoutIcon from '@mui/icons-material/Logout';
import MenuIcon from '@mui/icons-material/Menu';
import QrCodeScannerIcon from '@mui/icons-material/QrCodeScanner';
import {
  AppBar,
  Avatar,
  Box,
  Button,
  Chip,
  Divider,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Toolbar,
  Typography
} from '@mui/material';
import { ReactElement, ReactNode, useState } from 'react';
import { Link, Outlet, useLocation } from 'react-router';
import DashboardIcon from '@mui/icons-material/Dashboard';
import GroupsIcon from '@mui/icons-material/Groups';
import EventIcon from '@mui/icons-material/Event';
import MeetingRoomIcon from '@mui/icons-material/MeetingRoom';
import FactCheckIcon from '@mui/icons-material/FactCheck';
import ManageAccountsIcon from '@mui/icons-material/ManageAccounts';
import { useAuth } from '../context/useAuth';
import type { Role } from '../lib/types';

const navItems = [
  { label: 'Dashboard', path: '/', icon: <DashboardIcon /> },
  { label: 'Attendance', path: '/attendance', icon: <QrCodeScannerIcon />, roles: ['ADMIN', 'TEACHING_ASSISTANT'] },
  { label: 'Students', path: '/students', icon: <GroupsIcon /> },
  { label: 'Records', path: '/records', icon: <FactCheckIcon />, roles: ['ADMIN', 'TEACHING_ASSISTANT'] },
  { label: 'Sessions', path: '/sessions', icon: <EventIcon />, roles: ['ADMIN', 'TEACHING_ASSISTANT'] },
  { label: 'Halls', path: '/halls', icon: <MeetingRoomIcon />, roles: ['ADMIN', 'TEACHING_ASSISTANT'] },
  { label: 'Users', path: '/users', icon: <ManageAccountsIcon />, roles: ['ADMIN'] }
] satisfies Array<{ label: string; path: string; icon: ReactElement; roles?: Role[] }>;

function Navigation({ onSelect }: { onSelect?: () => void }) {
  const location = useLocation();
  const { hasRole } = useAuth();
  const visibleItems = navItems.filter((item) => hasRole(item.roles));

  return (
    <List aria-label="Primary navigation" sx={{ px: 1.5, py: 2 }}>
      {visibleItems.map((item) => (
        <ListItemButton
          key={item.path}
          component={Link}
          to={item.path}
          selected={location.pathname === item.path}
          onClick={onSelect}
          sx={{
            borderRadius: 1.5,
            mb: 0.5,
            '&.Mui-selected': {
              bgcolor: 'primary.main',
              color: 'primary.contrastText',
              '& .MuiListItemIcon-root': { color: 'inherit' }
            }
          }}
        >
          <ListItemIcon sx={{ minWidth: 40 }}>{item.icon}</ListItemIcon>
          <ListItemText primary={item.label} />
        </ListItemButton>
      ))}
    </List>
  );
}

export function AppShell({ children }: { children?: ReactNode }) {
  const [open, setOpen] = useState(false);
  const { user, logout } = useAuth();
  const drawerWidth = 252;

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', bgcolor: 'background.default' }}>
      <AppBar
        position="fixed"
        color="inherit"
        elevation={0}
        sx={{ zIndex: 1201, borderBottom: '1px solid', borderColor: 'divider', backdropFilter: 'blur(12px)' }}
      >
        <Toolbar sx={{ minHeight: 68 }}>
          <IconButton
            aria-label="Open navigation"
            edge="start"
            onClick={() => setOpen(true)}
            sx={{ mr: 1, display: { md: 'none' } }}
          >
            <MenuIcon />
          </IconButton>
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="h6" sx={{ lineHeight: 1.1 }}>
              QR Attendance
            </Typography>
            <Typography variant="caption" color="text.secondary">
              Scan, manage, and review attendance
            </Typography>
          </Box>
          <Box sx={{ display: { xs: 'none', sm: 'flex' }, alignItems: 'center', gap: 1.25, mr: 2 }}>
            <Avatar sx={{ width: 34, height: 34, bgcolor: 'primary.main' }}>
              {user?.firstName?.[0]}
              {user?.lastName?.[0]}
            </Avatar>
            <Box>
              <Typography variant="body2" sx={{ fontWeight: 700, lineHeight: 1.2 }}>
                {user?.firstName} {user?.lastName}
              </Typography>
              <Chip size="small" label={user?.role ?? 'User'} sx={{ height: 20, fontSize: 11 }} />
            </Box>
          </Box>
          <Button startIcon={<LogoutIcon />} onClick={logout} color="inherit" variant="outlined" size="small">
            Logout
          </Button>
        </Toolbar>
      </AppBar>
      <Drawer
        variant="permanent"
        sx={{
          display: { xs: 'none', md: 'block' },
          width: drawerWidth,
          '& .MuiDrawer-paper': { width: drawerWidth, borderRight: '1px solid', borderColor: 'divider' }
        }}
      >
        <Toolbar />
        <Box sx={{ width: drawerWidth }}>
          <Navigation />
        </Box>
      </Drawer>
      <Drawer open={open} onClose={() => setOpen(false)}>
        <Toolbar />
        <Box sx={{ width: drawerWidth }}>
          <Navigation onSelect={() => setOpen(false)} />
        </Box>
      </Drawer>
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: { xs: 2, md: 3.5 },
          mt: 8,
          maxWidth: 1440,
          mx: 'auto',
          width: '100%'
        }}
      >
        <Divider sx={{ display: { md: 'none' }, mb: 2 }} />
        {children ?? <Outlet />}
      </Box>
    </Box>
  );
}
