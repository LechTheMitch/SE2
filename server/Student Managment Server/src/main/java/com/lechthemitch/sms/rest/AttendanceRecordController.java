package com.lechthemitch.sms.rest;

import com.lechthemitch.sms.dto.AttendanceRecordDTO;
import com.lechthemitch.sms.dto.AttendanceRecordResponseDTO;
import com.lechthemitch.sms.dto.AttendanceScanRequestDTO;
import com.lechthemitch.sms.service.AttendanceRecordService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/attendance-records")
public class AttendanceRecordController {
    private final AttendanceRecordService attendanceRecordService;

    @PostMapping("/scan")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceRecordResponseDTO createFromScan(@Valid @RequestBody AttendanceScanRequestDTO dto) {
        return attendanceRecordService.createFromScan(dto);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceRecordResponseDTO create(@Valid @RequestBody AttendanceRecordDTO dto) {
        return attendanceRecordService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public List<AttendanceRecordResponseDTO> findAll() {
        return attendanceRecordService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public AttendanceRecordResponseDTO findById(@PathVariable Integer id) {
        return attendanceRecordService.findById(id);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT') or (hasRole('STUDENT') and #studentId == principal.user.id)")
    public List<AttendanceRecordResponseDTO> findByStudentId(@PathVariable Integer studentId) {
        return attendanceRecordService.findByStudentId(studentId);
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public List<AttendanceRecordResponseDTO> findBySessionId(@PathVariable Integer sessionId) {
        return attendanceRecordService.findBySessionId(sessionId);
    }

    @GetMapping("/hall/{hallId}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public List<AttendanceRecordResponseDTO> findByHallId(@PathVariable Integer hallId) {
        return attendanceRecordService.findByHallId(hallId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public AttendanceRecordResponseDTO update(@PathVariable Integer id, @Valid @RequestBody AttendanceRecordDTO dto) {
        return attendanceRecordService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        attendanceRecordService.deleteById(id);
    }
}
