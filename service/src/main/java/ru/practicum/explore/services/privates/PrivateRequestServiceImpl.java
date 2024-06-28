package ru.practicum.explore.services.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.event_request.ParticipationRequestDto;
import ru.practicum.explore.enums.EventLifecycle;
import ru.practicum.explore.enums.RequestStatus;
import ru.practicum.explore.exceptions.MismatchException;
import ru.practicum.explore.mappers.EventRequestMapper;
import ru.practicum.explore.models.Event;
import ru.practicum.explore.models.EventRequest;
import ru.practicum.explore.models.User;
import ru.practicum.explore.repositories.EventRepository;
import ru.practicum.explore.repositories.EventRequestRepository;
import ru.practicum.explore.component.DataSearcher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateRequestServiceImpl implements PrivateRequestService {

    private final EventRequestRepository eventRequestRepository;
    private final EventRepository eventRepository;
    private final DataSearcher dataSearcher;

    @Override
    @Transactional(readOnly = true)
    public Collection<ParticipationRequestDto> getAllRequests(Long requesterId) {
        dataSearcher.findUserById(requesterId);

        Collection<EventRequest> requests = eventRequestRepository.getAllByRequesterId(requesterId);

        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        return requests.stream()
                .map(EventRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ParticipationRequestDto createRequest(Long requesterId, Long eventId) {
        if (!eventRequestRepository.getUserEventRequestByEvent(requesterId, eventId).isEmpty()) {
            throw new MismatchException("The request already exists!");
        }

        Event event = dataSearcher.findEventById(eventId);

        if (!event.getState().equals(EventLifecycle.PUBLISHED.toString())) {
            throw new MismatchException("The event has to be published!");
        }

        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new MismatchException("The limit of participants has been reached!");
        }

        if (requesterId.equals(event.getInitiator().getId())) {
            throw new MismatchException("It's impossible to create the event for user to his own event!");
        }

        User requester = dataSearcher.findUserById(requesterId);

        EventRequest request = new EventRequest(LocalDateTime.now(),
                event,
                requester,
                RequestStatus.PENDING.toString());

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED.toString());
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        }

        return EventRequestMapper.mapToParticipationRequestDto(eventRequestRepository.save(request));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        EventRequest requestToUpdate = dataSearcher.findEventRequestById(requestId);

        requestToUpdate.setStatus(RequestStatus.CANCELED.toString());

        return EventRequestMapper.mapToParticipationRequestDto(eventRequestRepository.save(requestToUpdate));
    }
}