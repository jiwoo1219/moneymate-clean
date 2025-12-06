package com.moneymate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "pets")
@Getter
@Setter
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private int level = 1;
    private int xp = 0;
    private int bones = 0;

    @Column(name = "pet_name")
    private String petName = "강아지";

    @Column(name = "last_check_date")
    private LocalDate lastCheckDate;

    @Column(name = "last_box_date")
    private LocalDate lastBoxDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
