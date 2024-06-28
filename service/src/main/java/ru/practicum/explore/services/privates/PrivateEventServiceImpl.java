package ru.practicum.explore.services.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.dto.event.NewEventDto;
import ru.practicum.explore.dto.event.UpdateEventUserRequest;
import ru.practicum.explore.dto.event_request.EventRequestStatusUpdateRequest;
import ru.practicum.explore.dto.event_request.EventRequestStatusUpdateResult;
import ru.practicum.explore.dto.event_request.ParticipationRequestDto;
import ru.practicum.explore.enums.EventLifecycle;
import ru.practicum.explore.enums.RequestStatus;
import ru.practicum.explore.enums.UserEventState;
import ru.practicum.explore.exceptions.MismatchException;
import ru.practicum.explore.exceptions.InvalidRequestException;
import ru.practicum.explore.mappers.EventMapper;
import ru.practicum.explore.mappers.EventRequestMapper;
import ru.practicum.explore.models.*;
import ru.practicum.explore.repositories.EventRepository;
import ru.practicum.explore.repositories.EventRequestRepository;
import ru.practicum.explore.repositories.LocationRepository;
import ru.practicum.explore.component.DataSearcher;
import ru.practicum.explore.utility.UtilityMergeProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrivateEventServiceImpl implements PrivateEventService {

    private final EventRepository eventRepository;
    private final EventRequestRepository eventRequestRepository;
    private final LocationRepository locationRepository;
    private final DataSearcher dataSearcher;

    @Transactional(readOnly = true)
    @Override
    public Collection<EventShortDto> getAllEvents(Long initiatorId, Pageable pageable) {
        Page<Event> events = eventRepository.getAllInitiatorEvents(initiatorId, pageable);

        if (events.getContent().isEmpty()) {
            return new ArrayList<>();
        }

        return events.getContent().stream()
                .map(EventMapper::mapToEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto createEvent(Long initiatorId, NewEventDto newEventDto) {
        LocalDateTime eventDate = newEventDto.getEventDate();

        checkEventDate(eventDate);

        User initiator = dataSearcher.findUserById(initiatorId);

        Category category = dataSearcher.findCategoryById(newEventDto.getCategory());

        Location location = locationRepository.save(newEventDto.getLocation());

        if (newEventDto.getParticipantLimit() < 0) {
            throw new InvalidRequestException("Participants' number can't be negative");
        }

        Event event = EventMapper.mapToEvent(newEventDto, category, location, initiator, eventDate,
                EventLifecycle.PENDING, 0L, 0L);

        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getEvent(Long initiatorId, Long eventId) {
        Event event = dataSearcher.findEventById(eventId);

        return EventMapper.mapToEventFullDto(event);
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long initiatorId, Long eventId, UpdateEventUserRequest updateEventUserRequest) {
        Event eventToUpdate = dataSearcher.findEventById(eventId);
        LocalDateTime newEventDate = updateEventUserRequest.getEventDate();

        if (newEventDate != null) {
            checkEventDate(newEventDate);
        }

        if (EventLifecycle.valueOf(eventToUpdate.getState()) == EventLifecycle.PUBLISHED) {
            throw new MismatchException("The only ones that can be changed are pending or canceled");
        }

        eventToUpdate.setState((updateEventUserRequest.getStateAction() != null &&
                updateEventUserRequest.getStateAction() == UserEventState.CANCEL_REVIEW) ?
                EventLifecycle.CANCELED.toString() : EventLifecycle.PENDING.toString());

        if (updateEventUserRequest.getParticipantLimit() != null && updateEventUserRequest.getParticipantLimit() < 0) {
            throw new InvalidRequestException("Participants' number can't be negative!");
        }

        UtilityMergeProperty.copyProperties(updateEventUserRequest, eventToUpdate);

        return EventMapper.mapToEventFullDto(eventRepository.save(eventToUpdate));
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<ParticipationRequestDto> getEventRequests(Long initiatorId, Long eventId) {
        Collection<EventRequest> requests = eventRequestRepository.getAllByEventId(eventId)
                .stream()
                .filter(request -> request.getEvent().getInitiator().getId().equals(initiatorId))
                .collect(Collectors.toList());

        if (requests.isEmpty()) {
            return new ArrayList<>();
        }

        return requests.stream()
                .map(EventRequestMapper::mapToParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestStatus(Long initiatorId, Long eventId,
                                                              EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = dataSearcher.findEventById(eventId);

        Collection<EventRequest> requestsToUpdate = eventRequestStatusUpdateRequest.getRequestIds()
                .stream()
                .map(dataSearcher::findEventRequestById)
                .collect(Collectors.toList());

        for (EventRequest request : requestsToUpdate) {
            if (!request.getStatus().equals(RequestStatus.PENDING.toString())) {
                throw new MismatchException("Request has to be PUBLISHED!");
            }
        }

        Collection<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        Collection<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        if (eventRequestStatusUpdateRequest.getStatus() == RequestStatus.REJECTED) {
            rejectedRequests = requestsToUpdate.stream()
                    .map(request -> {
                        request.setStatus(RequestStatus.REJECTED.toString());
                        eventRequestRepository.save(request);
                        return EventRequestMapper.mapToParticipationRequestDto(request);
                    })
                    .collect(Collectors.toList());
        }

        if (eventRequestStatusUpdateRequest.getStatus() == RequestStatus.CONFIRMED) {
            if (event.getParticipantLimit() == 0 || !event.getRequestModeration()) {
                confirmedRequests = requestsToUpdate.stream()
                        .map(request -> {
                            request.setStatus(RequestStatus.CONFIRMED.toString());
                            eventRequestRepository.save(request);
                            return EventRequestMapper.mapToParticipationRequestDto(request);
                        })
                        .collect(Collectors.toList());
            } else if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
                throw new MismatchException("The limit of participants has been reached!");
            }

            for (EventRequest request : requestsToUpdate) {
                if (event.getConfirmedRequests() < event.getParticipantLimit()) {
                    request.setStatus(RequestStatus.CONFIRMED.toString());
                    confirmedRequests.add(EventRequestMapper.mapToParticipationRequestDto(request));
                } else {
                    request.setStatus(RequestStatus.REJECTED.toString());
                    rejectedRequests.add(EventRequestMapper.mapToParticipationRequestDto(request));
                }
                eventRequestRepository.save(request);
                event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            }
        }

        eventRepository.save(event);

        return new EventRequestStatusUpdateResult(confirmedRequests, rejectedRequests);
    }

    private void checkEventDate(LocalDateTime eventDate) {
        if (eventDate.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new InvalidRequestException("Can't create the event because the event date can't be earlier " +
                    "than an hour after this moment!");
        }
    }
}