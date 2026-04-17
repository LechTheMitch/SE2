package com.lechthemitch.student_management.entity.formalities;

import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "sessionDate")
    private ZonedDateTime sessionDate; //TODO: Change this to a date type later.

    public Hall() {

    }

    public Hall(String name, ZonedDateTime sessionDate) {
        this.name = name;
        this.sessionDate = sessionDate;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ZonedDateTime getSessionDate() {
        return sessionDate;
    }

    public void setSessionDate(ZonedDateTime sessionDate) {
        this.sessionDate = sessionDate;
    }
}
