package ru.practicum.explore.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.explore.dto.event.EventShortDto;

import java.util.Collection;

@Data
@AllArgsConstructor
public class FriendEventsDto {
    private final Long id;
    private String name;
    private Collection<EventShortDto> confirmedEvents;
}
