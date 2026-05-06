package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dto.AttendanceRecordDTO;
import com.lechthemitch.sms.dto.AttendanceRecordResponseDTO;
import com.lechthemitch.sms.dto.AttendanceScanRequestDTO;

import java.util.List;

public interface AttendanceRecordService {
    AttendanceRecordResponseDTO create(AttendanceRecordDTO dto);

    AttendanceRecordResponseDTO createFromScan(AttendanceScanRequestDTO dto);

    List<AttendanceRecordResponseDTO> findAll();

    AttendanceRecordResponseDTO findById(Integer id);

    List<AttendanceRecordResponseDTO> findByStudentId(Integer studentId);

    List<AttendanceRecordResponseDTO> findBySessionId(Integer sessionId);

    List<AttendanceRecordResponseDTO> findByHallId(Integer hallId);

    AttendanceRecordResponseDTO update(Integer id, AttendanceRecordDTO dto);

    void deleteById(Integer id);
}
