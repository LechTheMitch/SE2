import { expect, test } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  const token =
    'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.' +
    'eyJzdWIiOiJhZG1pbkBleGFtcGxlLmNvbSIsImV4cCI6MTg2MjM0MjQwMH0.' +
    'demo-signature';
  await page.route('**/login', async (route) => route.fulfill({ json: { token } }));
  await page.route('**/api/auth/login', async (route) => route.fulfill({ status: 404, body: 'Not found' }));
  await page.route('**/api/users/me', async (route) =>
    route.fulfill({
      json: {
        userId: 1,
        firstName: 'Admin',
        lastName: 'User',
        email: 'admin@example.com',
        phoneNumber: '+15550000001',
        role: 'ADMIN',
        permissions: [],
        forcePasswordChange: false,
        forceEmailChange: false
      }
    })
  );
  await page.route('**/api/sessions', async (route) => route.fulfill({ json: [{ id: 11, title: 'Algorithms' }] }));
  await page.route('**/api/halls', async (route) => route.fulfill({ json: [{ id: 22, name: 'Main Hall' }] }));
  await page.route('**/api/students', async (route) => route.fulfill({ json: [] }));
  await page.route('**/api/attendance-records', async (route) => route.fulfill({ json: [] }));

  let scans = 0;
  await page.route('**/api/attendance-records/scan', async (route) => {
    scans += 1;
    const request = route.request().postDataJSON();
    expect(request).toEqual({ qrCode: 'TOKEN-123', sessionId: 11, hallId: 22 });
    if (scans === 1) {
      await route.fulfill({
        status: 201,
        json: { id: 1, studentId: 5, sessionId: 11, hallId: 22, attendanceDate: '2026-05-06T08:30:00Z' }
      });
      return;
    }
    await route.fulfill({ status: 400, body: 'Duplicate attendance for this session' });
  });
});

test('login flow', async ({ page }) => {
  await page.goto('/login');
  await page.getByLabel(/email or username/i).fill('admin@example.com');
  await page.getByLabel(/password/i).fill('admin1234');
  await page.getByRole('button', { name: /login/i }).click();

  await expect(page.getByRole('heading', { name: 'Dashboard' })).toBeVisible();
});

test('attendance manual flow handles success and duplicate', async ({ page }) => {
  await page.goto('/login');
  await page.getByLabel(/email or username/i).fill('admin@example.com');
  await page.getByLabel(/password/i).fill('admin1234');
  await page.getByRole('button', { name: /login/i }).click();
  await page.getByRole('link', { name: /attendance/i }).click();

  await page.getByLabel('Session').click();
  await page.getByRole('option', { name: 'Algorithms' }).click();
  await page.getByLabel('Hall').click();
  await page.getByRole('option', { name: 'Main Hall' }).click();
  await page.getByLabel(/manual qr token/i).fill('TOKEN-123');
  await page.getByRole('button', { name: /submit token/i }).click();
  await expect(page.getByText(/recorded student 5/i)).toBeVisible();

  await page.getByLabel(/manual qr token/i).fill('TOKEN-123');
  await page.getByRole('button', { name: /submit token/i }).click();
  await expect(page.getByText('Duplicate attendance for this session')).toBeVisible();
});
