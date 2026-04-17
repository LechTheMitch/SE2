package com.lechthemitch.student_management.entity.formalities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "sessionReleaseDate") //Needed for Online Students
    private ZonedDateTime sessionTime;

    public Session(String title, String description, ZonedDateTime sessionTime) {
        this.title = title;
        this.description = description;
        this.sessionTime = sessionTime;
    }
}
