package com.lechthemitch.student_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
public class Student extends User{

    @Column(name = "parentNumber")
    private String parentNumber;

    @Column(name = "grade")
    private String grade;

    @Column(name = "lectureHall")
    private String lectureHall;

    @Column(name = "qrCode")
    private String qrCode; //Will be used for attendance tracking.

    @Column(name = "studentEduType")
    @Enumerated(EnumType.STRING)
    private StudentEduType studentEduType;

    @Override
    public int generateId() {
        return 0;
    }
}
