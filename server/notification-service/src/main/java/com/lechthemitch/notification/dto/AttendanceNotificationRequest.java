package com.lechthemitch.notification.dto;

import java.time.OffsetDateTime;

public class AttendanceNotificationRequest {
    private String studentName;
    private String parentEmail;
    private String parentPhoneNumber;
    private String sessionName;
    private OffsetDateTime attendanceTime;

    public AttendanceNotificationRequest() {
    }

    public AttendanceNotificationRequest(String studentName, String parentEmail, String parentPhoneNumber, String sessionName, OffsetDateTime attendanceTime) {
        this.studentName = studentName;
        this.parentEmail = parentEmail;
        this.parentPhoneNumber = parentPhoneNumber;
        this.sessionName = sessionName;
        this.attendanceTime = attendanceTime;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getParentEmail() {
        return parentEmail;
    }

    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }

    public String getParentPhoneNumber() {
        return parentPhoneNumber;
    }

    public void setParentPhoneNumber(String parentPhoneNumber) {
        this.parentPhoneNumber = parentPhoneNumber;
    }

    public String getSessionName() {
        return sessionName;
    }

    public void setSessionName(String sessionName) {
        this.sessionName = sessionName;
    }

    public OffsetDateTime getAttendanceTime() {
        return attendanceTime;
    }

    public void setAttendanceTime(OffsetDateTime attendanceTime) {
        this.attendanceTime = attendanceTime;
    }
}
