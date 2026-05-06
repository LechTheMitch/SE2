package com.lechthemitch.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Parent {
    @Id
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private Set<Student> children = new HashSet<>();
}
