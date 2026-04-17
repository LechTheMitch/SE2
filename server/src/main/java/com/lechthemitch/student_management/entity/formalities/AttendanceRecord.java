package com.lechthemitch.student_management.entity.formalities;

import jakarta.persistence.*;

@Entity
public class AttendanceRecord {
 //TODO: This is a terrible way of implementing this, should use lists and advanced hibernate mappings later or Foreign Keys.
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "studentId")
    private int studentId;

    @Column(name = "sessionId")
    private int sessionId;

    @Column(name = "attendanceHallId")
    private int attendanceHallId;

    public AttendanceRecord() {

    }

    public AttendanceRecord(int studentId, int sessionId, int attendanceHallId) {
        this.studentId = studentId;
        this.sessionId = sessionId;
        this.attendanceHallId = attendanceHallId;
    }

    public int getStudentId() {
        return studentId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getAttendanceHallId() {
        return attendanceHallId;
    }

}
