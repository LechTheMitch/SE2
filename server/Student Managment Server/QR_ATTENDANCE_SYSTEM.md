# QR Code Attendance System

## Overview

The Student Management Server now includes a complete QR code-based attendance tracking system. Each student receives a unique QR code that can be scanned to automatically record their attendance.

## System Flow

```
1. Student Creation → Unique QR code generated automatically
2. Retrieve QR Image → GET /api/students/{id}/qr
3. Display/Print QR → Use PNG image in UI or print
4. Scan QR Code → Scanner gets the QR token string
5. Create Attendance → POST /api/attendance-records/scan
   - Resolves student from QR token
   - Checks for duplicate attendance in session
   - Records attendance with timestamp
   - Returns attendance record
```

## API Endpoints

### 1. Get Student QR Code Image

**Endpoint:** `GET /api/students/{id}/qr`

**Parameters:**
- `width` (optional, default: 300) - QR code width in pixels
- `height` (optional, default: 300) - QR code height in pixels

**Response:** PNG image bytes

**Example:**
```bash
curl -X GET "http://localhost:8080/api/students/1/qr?width=400&height=400" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output student_qr.png
```

### 2. Get Student QR Code for Attendance Context

**Endpoint:** `GET /api/attendance-records/student/{studentId}/qr`

**Parameters:**
- Same as above

**Response:** PNG image bytes

**Authorization:** ADMIN or TEACHING_ASSISTANT

**Example:**
```bash
curl -X GET "http://localhost:8080/api/attendance-records/student/1/qr" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  --output attendance_qr.png
```

### 3. Record Attendance from QR Scan

**Endpoint:** `POST /api/attendance-records/scan`

**Authorization:** ADMIN or TEACHING_ASSISTANT

**Request Body:**
```json
{
  "qrCode": "base64_encoded_qr_token_here",
  "sessionId": 1,
  "hallId": 2,
  "attendanceDate": "2026-05-06T14:30:00+03:00"
}
```

**Parameters:**
- `qrCode` (required) - The QR token string scanned from the QR code
- `sessionId` (required) - The session ID for which attendance is being recorded
- `hallId` (required) - The hall/location ID where attendance is being taken
- `attendanceDate` (optional) - ISO 8601 formatted timestamp. If omitted, uses current server time

**Response:**
```json
{
  "id": 1,
  "studentId": 5,
  "sessionId": 1,
  "hallId": 2,
  "attendanceDate": "2026-05-06T14:30:00+03:00"
}
```

**Example:**
```bash
curl -X POST "http://localhost:8080/api/attendance-records/scan" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "qrCode": "aB3dEf9gHiJkL2mNoP4qRs5tUv6w",
    "sessionId": 1,
    "hallId": 2
  }'
```

### 4. Get Student Details with QR Code

**Endpoint:** `GET /api/students/{id}`

**Response includes:**
```json
{
  "id": 1,
  "userId": 5,
  "parentId": 2,
  "parentPhoneNumber": "+1555000001",
  "qrCode": "aB3dEf9gHiJkL2mNoP4qRs5tUv6w"
}
```

This returns the QR token string which you can then encode into a QR image on the client side if needed.

### 5. Batch QR Codes (Future Enhancement)

**Endpoint:** `POST /api/students/qr/batch`

**Purpose:** Currently returns the first student's QR for demo purposes. Can be enhanced for PDF generation with multiple QR codes.

## Features

### 1. Unique QR Generation
- Each student receives a cryptographically secure random token
- Generated using `SecureRandom` and Base64-URL encoded
- Collision detection prevents duplicate tokens
- Tokens are 32 characters long (256-bit entropy)

### 2. Duplicate Prevention
- The system prevents recording the same student's attendance twice in the same session
- Error: `"Attendance already recorded for student {studentId} in session {sessionId}"`
- Enforced at service layer with `existsByStudentIdAndSessionId()` check

### 3. Validation
- Verifies session exists
- Verifies hall exists and belongs to session
- Verifies student exists via QR token
- Validates hall-session relationship

### 4. PNG Image Generation
- Uses Google ZXing library for reliable QR encoding
- Configurable size (default 300x300 pixels)
- PNG format for broad compatibility
- Optimized compression

## Client Integration Examples

### JavaScript/Frontend

```javascript
// 1. Fetch QR image for student
async function getStudentQR(studentId, width = 300, height = 300) {
  const response = await fetch(
    `/api/students/${studentId}/qr?width=${width}&height=${height}`,
    { headers: { 'Authorization': `Bearer ${token}` } }
  );
  const blob = await response.blob();
  return URL.createObjectURL(blob);
}

// 2. Display QR in HTML
const qrUrl = await getStudentQR(1);
document.getElementById('qrImage').src = qrUrl;

// 3. Record attendance from scanned QR
async function recordAttendance(qrToken, sessionId, hallId) {
  const response = await fetch('/api/attendance-records/scan', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      qrCode: qrToken,
      sessionId: sessionId,
      hallId: hallId
    })
  });
  return response.json();
}
```

### Printing QR Codes

```html
<!-- Display and print QR codes -->
<div id="qr-printable">
  <img id="qrImage" src="" alt="Student QR Code" />
</div>

<script>
function printStudentQR(studentId) {
  const qrUrl = await getStudentQR(studentId, 400, 400);
  const printWindow = window.open('', '', 'width=400,height=400');
  printWindow.document.write(`<img src="${qrUrl}" />`);
  printWindow.print();
}
</script>
```

## Error Handling

### Invalid QR Code
```json
{
  "timestamp": "2026-05-06T14:35:00.123+03:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid student QR code"
}
```

### Duplicate Attendance
```json
{
  "timestamp": "2026-05-06T14:35:00.123+03:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Attendance already recorded for student 5 in session 1"
}
```

### Student Not Found
```json
{
  "timestamp": "2026-05-06T14:35:00.123+03:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Student not found: 999"
}
```

## Database Schema

### Student Table Changes
```sql
ALTER TABLE student ADD COLUMN qr_code VARCHAR(64) NOT NULL UNIQUE;
```

The `qr_code` column:
- Length: 64 characters (Base64-URL encoded 48-byte tokens)
- Unique: Ensures no duplicate QR codes
- Not Null: Every student must have a QR code

### Attendance Uniqueness
```sql
ALTER TABLE attendance_record ADD UNIQUE KEY unique_student_session (student_id, session_id);
```

## Security Considerations

1. **QR Token Security**
   - Tokens are random 48-byte values, not reversible
   - No sensitive information encoded in QR
   - Tokens are stored hashed in database (via unique constraint)

2. **Access Control**
   - QR scan endpoint requires ADMIN or TEACHING_ASSISTANT role
   - Student QR retrieval can be restricted to authorized personnel

3. **Rate Limiting** (Future Enhancement)
   - Consider adding rate limiting to attendance scan endpoint
   - Prevent brute force QR scanning

4. **Audit Trail** (Current)
   - All attendance records include timestamp
   - Can track who scanned and when

## Configuration

Add to `application.properties` if needed:

```properties
# QR Code Settings (optional)
qr.default-width=300
qr.default-height=300
qr.max-width=1000
qr.max-height=1000
```

## Troubleshooting

### QR Code Not Found
- Verify student ID is correct
- Check student exists: `GET /api/students/{id}`
- Confirm authorization token is valid

### Attendance Record Rejected as Duplicate
- Check existing attendance: `GET /api/attendance-records/student/{studentId}`
- Filters by session to find prior records
- Use same QR token to re-record if needed (will fail with duplicate error)

### QR Image Not Rendering
- Check image MIME type is `image/png`
- Verify browser supports PNG
- Try different size parameters (width/height)

## Future Enhancements

1. **Batch QR PDF Generation** - Generate printable sheets with multiple QR codes
2. **QR Code Rotation** - Regenerate tokens periodically for security
3. **Mobile App Integration** - Native QR scanner for iOS/Android
4. **Real-time Dashboard** - Live attendance tracking during session
5. **Rate Limiting** - Prevent rapid repeated scans
6. **Attendance Verification** - Confirm student identity before recording
7. **Multi-location Support** - Track attendance across multiple halls

## Testing

To test QR attendance flow:

```bash
# 1. Get a student's details including QR token
curl -X GET "http://localhost:8080/api/students/1" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Response includes: "qrCode": "aB3dEf..."

# 2. Use that QR token to record attendance
curl -X POST "http://localhost:8080/api/attendance-records/scan" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "qrCode": "aB3dEf...",
    "sessionId": 1,
    "hallId": 1
  }'

# 3. Verify attendance was recorded
curl -X GET "http://localhost:8080/api/attendance-records/student/1" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

## Dependencies

The QR generation system uses:
- **Google ZXing 3.5.3** - QR code encoding
  - `com.google.zxing:core:3.5.3`
  - `com.google.zxing:javase:3.5.3`

Make sure these are included in your Maven build via `pom.xml`.

