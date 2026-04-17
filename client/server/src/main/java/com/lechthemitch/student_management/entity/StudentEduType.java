package com.lechthemitch.student_management.entity;

public enum StudentEduType {
    ONLINE("Online"),
    ONSITE("Onsite");
    //Other types will be added after SE2 Project, such as hybrid, etc.
    private final String type;

    StudentEduType(String type) {
        this.type = type;
    }
}
