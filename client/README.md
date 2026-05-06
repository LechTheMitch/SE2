# QR Attendance Frontend

React + TypeScript frontend for the Spring QR attendance backend in `../server`.

## Setup

```bash
npm install
cp .env.example .env
npm run dev
```

Open `http://localhost:5173`.

## Commands

```bash
npm run build
npm run test
npx playwright install
npm run test:e2e
npm run lint
npm run format
```

## Environment

`VITE_API_BASE_URL` points to the Spring backend, for example:

```bash
VITE_API_BASE_URL=http://localhost:8080
```

The prompt mentioned `REACT_APP_API_BASE_URL`; Vite exposes client environment variables only when they start with `VITE_`, so this project uses `VITE_API_BASE_URL`.

## Backend API Assumptions

The app uses Bearer JWT authorization for protected calls. Tokens are kept in memory after login. If "Remember me" is selected, the token is also stored in `localStorage`; prefer an httpOnly cookie backend mode for production if available.

Login uses this repository backend's actual `POST /login` with `{ "emailOrPhone", "password" }`, then falls back to the prompt's `POST /api/auth/login` shape if needed. After login it loads `GET /api/users/me`.

Attendance records use the repository backend's filtered endpoints:

- `GET /api/attendance-records/session/{sessionId}`
- `GET /api/attendance-records/hall/{hallId}`
- `GET /api/attendance-records/student/{studentId}`
- `GET /api/users` for the admin-only Users page
- `POST /api/sessions` for session creation
- `POST /api/halls` for hall creation

The scanner posts exactly:

```json
{ "qrCode": "TOKEN", "sessionId": 1, "hallId": 2 }
```

to `POST /api/attendance-records/scan`.

## Demo Data

The backend has `DataSeeder` credentials:

- Admin: `admin@example.com` / `admin1234`
- Parent: `parent@example.com` / `parent1234`
- Student: `student@example.com` / `student1234`

It seeds a demo student with a QR token. Sessions and halls must exist before scanning. You can create them with an admin token:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/login \
  -H 'Content-Type: application/json' \
  -d '{"emailOrPhone":"admin@example.com","password":"admin1234"}' | jq -r .token)

curl -X POST http://localhost:8080/api/sessions \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"title":"Demo Session","description":"Frontend smoke test"}'

curl -X POST http://localhost:8080/api/halls \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"Main Hall","location":"Building A","sessionTime":"2026-05-06T09:00:00Z","sessionId":1}'
```

Adjust `sessionId` if your created session ID differs.

## Main Files

- `src/lib/api.ts`: Axios instance, auth interceptor, API adapter functions, PNG blob fetching.
- `src/context/AuthContext.tsx`: login/logout, profile restore, token expiry handling.
- `src/components/ProtectedRoute.tsx`: authentication and role-based route protection.
- `src/components/QrScanner.tsx`: webcam QR scanner using `@zxing/browser` with duplicate cooldown.
- `src/components/QrImage.tsx`: fetches `/api/students/{id}/qr` as PNG bytes, renders and downloads it.
- `src/pages/AttendanceScreen.tsx`: session/hall selection, camera scanner, manual fallback, last five scan results.
- `src/pages/SessionsPage.tsx` and `src/pages/HallsPage.tsx`: list and create sessions/halls.
- `src/pages/UsersPage.tsx`: admin-only account overview backed by `GET /api/users`.

## Developer Summary

The attendance flow starts on `AttendanceScreen`: the teacher selects a session and hall loaded from `/api/sessions` and `/api/halls`. Until both are selected, camera and manual submission are blocked.

When the camera scans a QR code, `QrScanner` emits the decoded token and suppresses identical scans inside a 2 second cooldown. `AttendanceScreen` sends `{ qrCode, sessionId, hallId }` through `attendanceApi.scan`, then shows either the returned attendance record details or the backend's error text for duplicate or invalid QR codes. Manual token entry uses the same path, so it is testable without camera access.

Students and student detail pages use `QrImage`, which requests PNG bytes with Axios `responseType: 'arraybuffer'`, converts them to a blob URL for `<img>`, and exposes a PNG download button.
