package ru.practicum.explore.services.publics;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.dto.EventsParamsReader;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

public interface PublicEventService {
    Collection<EventShortDto> getAllEvents(EventsParamsReader params, Pageable pageable);

    EventFullDto getEvent(Long eventId, HttpServletRequest request);
}