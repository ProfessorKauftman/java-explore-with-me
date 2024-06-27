package ru.practicum.explore.services.privates;

import ru.practicum.explore.dto.event_request.ParticipationRequestDto;

import java.util.Collection;

public interface PrivateRequestService {
    Collection<ParticipationRequestDto> getAllRequests(Long requesterId);

    ParticipationRequestDto createRequest(Long requesterId, Long eventId);

    ParticipationRequestDto cancelRequest(Long requesterId, Long requestId);
}