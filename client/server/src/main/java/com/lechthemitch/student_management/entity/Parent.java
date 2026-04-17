package com.lechthemitch.student_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class Parent extends User{

    @Column(name = "childrenIds")
    private List<Integer> childrenIds;

    public List<Integer> getChildrenIds() {
        return childrenIds;
    }

    public void setChildrenIds(List<Integer> childrenIds) {
        this.childrenIds = childrenIds;
    }

    public void addChildId(int childId) {
        this.childrenIds.add(childId);
    }

    //TODO: Children Performance and Attendance Tracking.

    @Override
    public int generateId() {
        return 0;
    }
}
