package ru.practicum.explore.services.admins;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.event.EventFullDto;
import ru.practicum.explore.dto.event.UpdateEventAdminRequest;
import ru.practicum.explore.enums.AdminEventState;
import ru.practicum.explore.enums.EventLifecycle;
import ru.practicum.explore.exceptions.InvalidRequestException;
import ru.practicum.explore.exceptions.ValidationException;
import ru.practicum.explore.mappers.EventMapper;
import ru.practicum.explore.models.Event;
import ru.practicum.explore.models.Location;
import ru.practicum.explore.repositories.EventRepository;
import ru.practicum.explore.repositories.LocationRepository;
import ru.practicum.explore.component.DataSearcher;
import ru.practicum.explore.utility.UtilityMergeProperty;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminEventServiceImpl implements AdminEventService {

    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final DataSearcher dataSearcher;

    @Transactional(readOnly = true)
    @Override
    public Collection<EventFullDto> getAllEvents(List<Long> initiators,
                                                 List<String> states,
                                                 List<Long> categories,
                                                 LocalDateTime rangeStart,
                                                 LocalDateTime rangeEnd,
                                                 Pageable pageable) {

        Collection<Event> events = eventRepository.getEventsByAdminParameters(initiators, states, categories,
                rangeStart, rangeEnd, pageable).getContent();

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventAdminRequest) {
        Event eventToUpdate = dataSearcher.findEventById(eventId);
        LocalDateTime newEventDate = updateEventAdminRequest.getEventDate();

        if (newEventDate != null) {
            if (newEventDate.isBefore(LocalDateTime.now())) {
                throw new InvalidRequestException("Date can't be earlier than current moment");
            }

            LocalDateTime publishDate = eventToUpdate.getPublishedOn();

            if (newEventDate.isBefore(publishDate.minusMinutes(30))) {
                throw new InvalidRequestException("Impossible to update the event because it's earlier than " +
                        "the publication date more than 30 minutes");
            }
        }

        if (updateEventAdminRequest.getStateAction() != null) {
            if (EventLifecycle.valueOf(eventToUpdate.getState()) != EventLifecycle.PENDING) {
                throw new ValidationException("Impossible to publish or reject the event because " +
                        "of the wrong state -> " + eventToUpdate.getState());
            }

            if (updateEventAdminRequest.getStateAction() == AdminEventState.PUBLISH_EVENT) {
                eventToUpdate.setState(EventLifecycle.PUBLISHED.toString());
                eventToUpdate.setPublishedOn(LocalDateTime.now());
            } else {
                eventToUpdate.setState(EventLifecycle.CANCELED.toString());
            }
        }

        if (updateEventAdminRequest.getLocation() != null) {
            Location location = locationRepository.save(updateEventAdminRequest.getLocation());
            eventToUpdate.setLocation(location);
        }

        UtilityMergeProperty.copyProperties(updateEventAdminRequest, eventToUpdate);

        return EventMapper.mapToEventFullDto(eventRepository.save(eventToUpdate));
    }
}