import EventAvailableIcon from '@mui/icons-material/EventAvailable';
import FactCheckIcon from '@mui/icons-material/FactCheck';
import GroupsIcon from '@mui/icons-material/Groups';
import MeetingRoomIcon from '@mui/icons-material/MeetingRoom';
import { Box, Grid, Paper, Stack, Typography } from '@mui/material';
import { useEffect, useState } from 'react';
import { PageHeader } from '../components/PageHeader';
import { attendanceApi, hallApi, sessionApi, studentApi } from '../lib/api';

export function Dashboard() {
  const [stats, setStats] = useState({ sessions: 0, students: 0, halls: 0, records: 0 });

  useEffect(() => {
    Promise.allSettled([sessionApi.list(), studentApi.list(), hallApi.list(), attendanceApi.list()]).then((results) => {
      setStats({
        sessions: results[0].status === 'fulfilled' ? results[0].value.length : 0,
        students: results[1].status === 'fulfilled' ? results[1].value.length : 0,
        halls: results[2].status === 'fulfilled' ? results[2].value.length : 0,
        records: results[3].status === 'fulfilled' ? results[3].value.length : 0
      });
    });
  }, []);

  const cards = [
    { label: 'Sessions', value: stats.sessions, icon: <EventAvailableIcon /> },
    { label: 'Students', value: stats.students, icon: <GroupsIcon /> },
    { label: 'Halls', value: stats.halls, icon: <MeetingRoomIcon /> },
    { label: 'Attendance records', value: stats.records, icon: <FactCheckIcon /> }
  ];

  return (
    <>
      <PageHeader title="Dashboard" subtitle="A quick snapshot of your attendance setup." />
      <Grid container spacing={2}>
        {cards.map((card) => (
          <Grid key={card.label} size={{ xs: 12, sm: 6, md: 3 }}>
            <Paper sx={{ p: 2.5, border: '1px solid', borderColor: 'divider' }} elevation={0}>
              <Stack spacing={1.5}>
                <Box sx={{ color: 'primary.main', display: 'flex' }}>{card.icon}</Box>
                <Typography color="text.secondary" sx={{ fontWeight: 700 }}>
                  {card.label}
                </Typography>
                <Typography variant="h3">{card.value}</Typography>
              </Stack>
            </Paper>
          </Grid>
        ))}
      </Grid>
    </>
  );
}
