import '@testing-library/jest-dom';

Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: jest.fn().mockImplementation((query: string) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: jest.fn(),
    removeListener: jest.fn(),
    addEventListener: jest.fn(),
    removeEventListener: jest.fn(),
    dispatchEvent: jest.fn()
  }))
});

Object.defineProperty(URL, 'createObjectURL', {
  writable: true,
  value: jest.fn(() => 'blob:test')
});

Object.defineProperty(URL, 'revokeObjectURL', {
  writable: true,
  value: jest.fn()
});
