import { render, screen, waitFor } from '@testing-library/react';
import { QrScanner } from './QrScanner';

const stop = jest.fn();
const decodeFromVideoDevice = jest.fn();

jest.mock('@zxing/browser', () => ({
  BrowserMultiFormatReader: jest.fn().mockImplementation(() => ({ decodeFromVideoDevice })),
  IScannerControls: {}
}));

describe('QrScanner', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    decodeFromVideoDevice.mockResolvedValue({ stop });
  });

  it('emits scanned token text', async () => {
    const onScan = jest.fn();
    decodeFromVideoDevice.mockImplementation(async (_device, _video, callback) => {
      callback({ getText: () => 'student-token' });
      return { stop };
    });

    render(<QrScanner onScan={onScan} />);

    await waitFor(() => expect(onScan).toHaveBeenCalledWith('student-token'));
    expect(screen.getByLabelText('QR camera scanner')).toBeInTheDocument();
  });

  it('does not start camera when disabled', () => {
    render(<QrScanner onScan={jest.fn()} disabled />);

    expect(decodeFromVideoDevice).not.toHaveBeenCalled();
    expect(screen.getByText(/select a session and hall/i)).toBeInTheDocument();
  });
});
