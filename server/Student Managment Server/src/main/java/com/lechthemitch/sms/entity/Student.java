package com.lechthemitch.sms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Student {
    @Id
    private Integer id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId", nullable = false, unique = true)
    private User user;

    private String parentPhoneNumber;

    @ManyToOne
    @JoinColumn(name = "parentId", nullable = false)
    private Parent parent;

    @Column(name = "qrCode", nullable = false, unique = true, length = 64)
    private String qrCode;
}
