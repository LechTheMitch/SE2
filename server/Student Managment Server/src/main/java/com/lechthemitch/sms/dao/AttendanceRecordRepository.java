package com.lechthemitch.sms.dao;

import com.lechthemitch.sms.entity.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Integer> {
    List<AttendanceRecord> findByStudentId(Integer studentId);

    List<AttendanceRecord> findBySessionId(Integer sessionId);

    List<AttendanceRecord> findByHallId(Integer hallId);

    boolean existsByStudentIdAndSessionId(Integer studentId, Integer sessionId);
}
