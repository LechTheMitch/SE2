package com.lechthemitch.sms.entity;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

public enum PermissionType {
    READ_USERS,
    DELETE_USERS,
    READ_STUDENTS,
    DELETE_STUDENTS,
    ASSIGN_STUDENT_TO_HALL,
    VIEW_STUDENTS_ASSIGNED_TO_HALL,
    VIEW_STUDENT_DATA

}