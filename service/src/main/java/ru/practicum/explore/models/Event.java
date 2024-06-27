package ru.practicum.explore.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 2000)
    private String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Category category;

    @Column(nullable = false)
    private Long confirmedRequests;

    @Column(nullable = false)
    private LocalDateTime createdOn;

    @Column(nullable = false, length = 7000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime eventDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private User initiator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "id")
    private Location location;

    @Column(nullable = false)
    private Boolean paid;

    @Column(nullable = false)
    private Long participantLimit;

    @Column(nullable = false)
    private LocalDateTime publishedOn;

    @Column(nullable = false)
    private Boolean requestModeration;

    @Column(nullable = false)
    private String state;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    private Long views;

    public Event(String annotation, Category category, Long confirmedRequests, LocalDateTime createdOn,
                 String description, LocalDateTime eventDate, User initiator, Location location, Boolean paid,
                 Long participantLimit, LocalDateTime publishedOn, Boolean requestModeration, String state,
                 String title, Long views) {
        this.annotation = annotation;
        this.category = category;
        this.confirmedRequests = confirmedRequests;
        this.createdOn = createdOn;
        this.description = description;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.location = location;
        this.paid = paid;
        this.participantLimit = participantLimit;
        this.publishedOn = publishedOn;
        this.requestModeration = requestModeration;
        this.state = state;
        this.title = title;
        this.views = views;
    }
}