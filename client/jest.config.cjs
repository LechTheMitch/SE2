/** @type {import('jest').Config} */
module.exports = {
  preset: 'ts-jest',
  testEnvironment: 'jsdom',
  transform: {
    '^.+\\.(ts|tsx)$': ['ts-jest', { tsconfig: '<rootDir>/tsconfig.app.json' }]
  },
  setupFilesAfterEnv: ['<rootDir>/src/test/setup.ts'],
  moduleNameMapper: {
    '^.+\\.(css|less|scss)$': '<rootDir>/src/test/styleMock.ts'
  },
  testMatch: ['<rootDir>/src/**/*.test.{ts,tsx}']
};
