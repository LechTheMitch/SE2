package com.lechthemitch.sms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String location;

    @Column(name = "session_time", nullable = false)
    private OffsetDateTime sessionTime;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = true)
    private Session session;

    @OneToMany(mappedBy = "hall", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<AttendanceRecord> attendanceRecords;

}
