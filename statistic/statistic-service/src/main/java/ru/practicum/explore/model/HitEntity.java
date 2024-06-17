package ru.practicum.explore.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hit")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, length = 50)
    private String app;

    @Column(nullable = false, length = 2000)
    private String uri;

    @Column(nullable = false, length = 50)
    private String ip;

    @Column(nullable = false)
    private LocalDateTime dates;
}
