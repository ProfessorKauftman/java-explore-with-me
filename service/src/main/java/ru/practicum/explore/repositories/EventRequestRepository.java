package ru.practicum.explore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.models.EventRequest;

import java.util.Collection;

@Repository
public interface EventRequestRepository extends JpaRepository<EventRequest, Long> {
    @Query("SELECT er FROM EventRequest er " +
            "WHERE er.requester.id = ?1 " +
            "AND er.event.id = ?2")
    Collection<EventRequest> getUserEventRequestByEvent(Long userId, Long eventId);

    Collection<EventRequest> getAllByRequesterId(Long userId);

    Collection<EventRequest> getAllByEventId(Long eventId);

    @Query("SELECT er FROM EventRequest er " +
            "WHERE er.requester.id = ?1 " +
            "AND er.status = 'CONFIRMED'")
    Collection<EventRequest> getConfirmedUserRequests(Collection<Long> friends);
}