package ru.practicum.explore.services.privates;

import org.springframework.data.domain.Pageable;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.dto.event.NewEventDto;
import ru.practicum.explore.dto.event.UpdateEventUserRequest;
import ru.practicum.explore.dto.event_request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.event_request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.event_request.ParticipationRequestDto;

import java.util.Collection;

public interface PrivateEventService {
    Collection<EventShortDto> getAllEvents(Long initiatorId, Pageable pageable);

    EventFullDto createEvent(Long initiatorId, NewEventDto newEventDto);

    EventFullDto getEvent(Long initiatorId, Long eventId);

    EventFullDto updateEvent(Long initiatorId, Long eventId, UpdateEventUserRequest updateEventUserRequest);

    Collection<ParticipationRequestDto> getEventRequests(Long initiatorId, Long eventId);

    EventRequestStatusUpdateResult updateRequestStatus(Long initiatorId, Long eventId,
                                                       EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest);
}