import { render, screen, waitFor, within } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { AttendanceScreen } from './AttendanceScreen';
import { attendanceApi, hallApi, sessionApi } from '../lib/api';

jest.mock('../components/QrScanner', () => ({
  QrScanner: ({ disabled }: { disabled?: boolean }) => <div data-testid="scanner">{disabled ? 'disabled' : 'enabled'}</div>
}));

jest.mock('../lib/api', () => ({
  sessionApi: { list: jest.fn() },
  hallApi: { list: jest.fn() },
  attendanceApi: { scan: jest.fn() },
  getApiErrorMessage: (error: unknown) => (error instanceof Error ? error.message : 'error')
}));

describe('AttendanceScreen', () => {
  beforeEach(() => {
    jest.mocked(sessionApi.list).mockResolvedValue([{ id: 1, title: 'Math' }]);
    jest.mocked(hallApi.list).mockResolvedValue([{ id: 2, name: 'Hall A' }]);
    jest.mocked(attendanceApi.scan).mockResolvedValue({
      id: 10,
      studentId: 3,
      sessionId: 1,
      hallId: 2,
      attendanceDate: '2026-05-06T10:00:00Z'
    });
  });

  it('blocks manual submission until session and hall are selected', async () => {
    render(<AttendanceScreen />);

    await userEvent.type(screen.getByLabelText(/manual qr token/i), 'abc');

    expect(screen.getByRole('button', { name: /submit token/i })).toBeDisabled();
  });

  it('submits selected session and hall with token', async () => {
    render(<AttendanceScreen />);

    await userEvent.click(await screen.findByLabelText(/session/i));
    await userEvent.click(await screen.findByRole('option', { name: 'Math' }));
    await userEvent.click(screen.getByLabelText(/hall/i));
    await userEvent.click(await screen.findByRole('option', { name: 'Hall A' }));
    await userEvent.type(screen.getByLabelText(/manual qr token/i), 'qr-token-1');
    await userEvent.click(screen.getByRole('button', { name: /submit token/i }));

    await waitFor(() =>
      expect(attendanceApi.scan).toHaveBeenCalledWith({ qrCode: 'qr-token-1', sessionId: 1, hallId: 2 })
    );
    expect((await screen.findAllByText(/recorded student 3/i)).length).toBeGreaterThan(0);
  });

  it('shows duplicate or invalid backend messages', async () => {
    jest.mocked(attendanceApi.scan).mockRejectedValue(new Error('Duplicate attendance for this session'));
    render(<AttendanceScreen />);

    await userEvent.click(await screen.findByLabelText(/session/i));
    await userEvent.click(within(screen.getByRole('presentation')).getByRole('option', { name: 'Math' }));
    await userEvent.click(screen.getByLabelText(/hall/i));
    await userEvent.click(within(screen.getByRole('presentation')).getByRole('option', { name: 'Hall A' }));
    await userEvent.type(screen.getByLabelText(/manual qr token/i), 'qr-token-1');
    await userEvent.click(screen.getByRole('button', { name: /submit token/i }));

    expect((await screen.findAllByText('Duplicate attendance for this session')).length).toBeGreaterThan(0);
  });
});
