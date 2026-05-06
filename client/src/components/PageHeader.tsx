import { Box, Typography } from '@mui/material';
import type { ReactNode } from 'react';

export function PageHeader({
  title,
  subtitle,
  action
}: {
  title: string;
  subtitle?: string;
  action?: ReactNode;
}) {
  return (
    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 2, mb: 3 }}>
      <Box>
        <Typography variant="h4" component="h1" sx={{ fontWeight: 800, letterSpacing: 0 }}>
          {title}
        </Typography>
        {subtitle && (
          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            {subtitle}
          </Typography>
        )}
      </Box>
      {action}
    </Box>
  );
}
