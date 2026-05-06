import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { QrImage } from './QrImage';
import { studentApi } from '../lib/api';

jest.mock('../lib/api', () => ({
  studentApi: { getQrBlob: jest.fn() },
  getApiErrorMessage: (error: unknown) => (error instanceof Error ? error.message : 'error')
}));

describe('QrImage', () => {
  beforeEach(() => {
    jest.spyOn(URL, 'createObjectURL').mockReturnValue('blob:qr');
    jest.spyOn(URL, 'revokeObjectURL').mockImplementation(jest.fn());
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('fetches and renders a QR blob', async () => {
    jest.mocked(studentApi.getQrBlob).mockResolvedValue(new Blob(['png'], { type: 'image/png' }));

    render(<QrImage studentId={7} />);

    expect(await screen.findByAltText('Student 7 QR code')).toHaveAttribute('src', 'blob:qr');
    expect(studentApi.getQrBlob).toHaveBeenCalledWith(7, 300, 300);
  });

  it('downloads the QR image', async () => {
    jest.mocked(studentApi.getQrBlob).mockResolvedValue(new Blob(['png'], { type: 'image/png' }));
    const click = jest.fn();
    jest.spyOn(HTMLAnchorElement.prototype, 'click').mockImplementation(click);

    render(<QrImage studentId={8} />);
    await screen.findByAltText('Student 8 QR code');
    await userEvent.click(screen.getByRole('button', { name: /download png/i }));

    expect(click).toHaveBeenCalled();
  });

  it('shows backend error text', async () => {
    jest.mocked(studentApi.getQrBlob).mockRejectedValue(new Error('QR unavailable'));

    render(<QrImage studentId={9} />);

    await waitFor(() => expect(screen.getByRole('alert')).toHaveTextContent('QR unavailable'));
  });
});
