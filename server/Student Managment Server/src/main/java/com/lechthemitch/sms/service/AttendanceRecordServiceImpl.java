package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dao.AttendanceRecordRepository;
import com.lechthemitch.sms.dao.HallRepository;
import com.lechthemitch.sms.dao.SessionRepository;
import com.lechthemitch.sms.dao.StudentRepository;
import com.lechthemitch.sms.dto.AttendanceRecordDTO;
import com.lechthemitch.sms.dto.AttendanceRecordResponseDTO;
import com.lechthemitch.sms.dto.AttendanceScanRequestDTO;
import com.lechthemitch.sms.entity.AttendanceRecord;
import com.lechthemitch.sms.entity.Hall;
import com.lechthemitch.sms.entity.Session;
import com.lechthemitch.sms.entity.Student;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@Service
public class AttendanceRecordServiceImpl implements AttendanceRecordService {
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentRepository studentRepository;
    private final SessionRepository sessionRepository;
    private final HallRepository hallRepository;

    @Override
    public AttendanceRecordResponseDTO create(AttendanceRecordDTO dto) {
        AttendanceRecord attendanceRecord = new AttendanceRecord();
        applyDto(attendanceRecord, dto);
        return toResponse(attendanceRecordRepository.save(attendanceRecord));
    }

    @Override
    public AttendanceRecordResponseDTO createFromScan(AttendanceScanRequestDTO dto) {
        Student student = studentRepository.findByQrCode(dto.qrCode())
                .orElseThrow(() -> new IllegalArgumentException("Invalid student QR code"));

        if (attendanceRecordRepository.existsByStudentIdAndSessionId(student.getId(), dto.sessionId())) {
            throw new IllegalArgumentException("Attendance already recorded for student " + student.getId() + " in session " + dto.sessionId());
        }

        Session session = getSession(dto.sessionId());
        Hall hall = getHall(dto.hallId());
        if (hall.getSession() != null && !hall.getSession().getId().equals(session.getId())) {
            throw new IllegalArgumentException("Hall " + dto.hallId() + " does not belong to session " + dto.sessionId());
        }

        AttendanceRecord attendanceRecord = new AttendanceRecord();
        attendanceRecord.setStudent(student);
        attendanceRecord.setSession(session);
        attendanceRecord.setHall(hall);
        attendanceRecord.setAttendanceDate(dto.attendanceDate() != null ? dto.attendanceDate() : OffsetDateTime.now());

        return toResponse(attendanceRecordRepository.save(attendanceRecord));
    }

    @Override
    public List<AttendanceRecordResponseDTO> findAll() {
        return attendanceRecordRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AttendanceRecordResponseDTO findById(Integer id) {
        return toResponse(getAttendanceRecord(id));
    }

    @Override
    public List<AttendanceRecordResponseDTO> findByStudentId(Integer studentId) {
        return attendanceRecordRepository.findByStudentId(studentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AttendanceRecordResponseDTO> findBySessionId(Integer sessionId) {
        return attendanceRecordRepository.findBySessionId(sessionId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public List<AttendanceRecordResponseDTO> findByHallId(Integer hallId) {
        return attendanceRecordRepository.findByHallId(hallId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public AttendanceRecordResponseDTO update(Integer id, AttendanceRecordDTO dto) {
        AttendanceRecord attendanceRecord = getAttendanceRecord(id);
        applyDto(attendanceRecord, dto);
        return toResponse(attendanceRecordRepository.save(attendanceRecord));
    }

    @Override
    public void deleteById(Integer id) {
        if (!attendanceRecordRepository.existsById(id)) {
            throw new IllegalArgumentException("Attendance record not found: " + id);
        }
        attendanceRecordRepository.deleteById(id);
    }

    private AttendanceRecord getAttendanceRecord(Integer id) {
        return attendanceRecordRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Attendance record not found: " + id));
    }

    private void applyDto(AttendanceRecord attendanceRecord, AttendanceRecordDTO dto) {
        Student student = studentRepository.findById(dto.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.studentId()));
        Session session = getSession(dto.sessionId());
        Hall hall = getHall(dto.hallId());

        attendanceRecord.setStudent(student);
        attendanceRecord.setSession(session);
        attendanceRecord.setHall(hall);
        attendanceRecord.setAttendanceDate(dto.attendanceDate());
    }

    private Session getSession(Integer sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    }

    private Hall getHall(Integer hallId) {
        return hallRepository.findById(hallId)
                .orElseThrow(() -> new IllegalArgumentException("Hall not found: " + hallId));
    }

    private AttendanceRecordResponseDTO toResponse(AttendanceRecord attendanceRecord) {
        return new AttendanceRecordResponseDTO(
                attendanceRecord.getId(),
                attendanceRecord.getStudent().getId(),
                attendanceRecord.getSession().getId(),
                attendanceRecord.getHall().getId(),
                attendanceRecord.getAttendanceDate()
        );
    }
}
