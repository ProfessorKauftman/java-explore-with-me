package ru.practicum.explore.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "endpoint_hit")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public HitEntity(String app, String uri, String ip, LocalDateTime dates) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.dates = dates;
    }
}