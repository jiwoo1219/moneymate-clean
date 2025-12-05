package com.moneymate.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private int level = 1;
    private int xp = 0;
    private int bones = 0;

    @Column(name = "last_check_date")
    private LocalDate lastCheckDate;

    @Column(name = "last_box_date")
    private LocalDate lastBoxDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // === getter / setter ===
    // (롬복 쓰면 @Getter @Setter @NoArgsConstructor @Builder 등 붙여도 OK)
}
