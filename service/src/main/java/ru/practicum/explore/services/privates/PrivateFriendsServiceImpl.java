package ru.practicum.explore.services.privates;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.component.DataSearcher;
import ru.practicum.explore.dto.event.EventShortDto;
import ru.practicum.explore.dto.event_request.ParticipationRequestDto;
import ru.practicum.explore.dto.friends.FriendEventsDto;
import ru.practicum.explore.dto.friends.FriendsDto;
import ru.practicum.explore.dto.user.UserShortDto;
import ru.practicum.explore.mappers.EventMapper;
import ru.practicum.explore.mappers.EventRequestMapper;
import ru.practicum.explore.mappers.FriendMapper;
import ru.practicum.explore.mappers.UserMapper;
import ru.practicum.explore.models.Friends;
import ru.practicum.explore.models.User;
import ru.practicum.explore.repositories.EventRequestRepository;
import ru.practicum.explore.repositories.FriendsRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;


@Service
@RequiredArgsConstructor
public class PrivateFriendsServiceImpl implements PrivateFriendsService {

    private final FriendsRepository friendsRepository;
    private final EventRequestRepository eventRequestRepository;
    private final DataSearcher dataSearcher;

    @Transactional
    @Override
    public FriendsDto addFriend(Long userId, Long friendId) {
        User user = dataSearcher.findUserById(userId);
        User friend = dataSearcher.findUserById(friendId);

        Friends request = friendsRepository.findByUserIdAndFriendId(friendId, userId);
        boolean friendshipState = false;

        if (request != null) {
            friendshipState = true;
            request.setFriendshipStat(friendshipState);
            friendsRepository.save(request);
        }

        Friends newFriend = friendsRepository.save(new Friends(user, friend, friendshipState));

        return FriendMapper.mapToFriendsDto(newFriend);
    }

    @Transactional
    @Override
    public void deleteFriend(Long userId, Long friendId) {
        dataSearcher.findUserById(userId);
        dataSearcher.findUserById(friendId);

        Friends friend1 = friendsRepository.findByUserIdAndFriendId(userId, friendId);
        Friends friend2 = friendsRepository.findByUserIdAndFriendId(friendId, userId);

        if (friend1 != null && friend2 != null) {
            CompletableFuture.allOf(CompletableFuture.runAsync(() -> friendsRepository.deleteById(friend1.getId())),
                    CompletableFuture.runAsync(() -> friendsRepository.deleteById(friend2.getId()))).join();
        }
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<UserShortDto> getAllFriends(Long userId) {
        dataSearcher.findUserById(userId);

        Collection<UserShortDto> friends = friendsRepository.findByUserIdAndFriendshipStat(userId, true)
                .stream()
                .map(Friends::getFriend)
                .map(UserMapper::mapToUserShortDto)
                .collect(toList());

        if (friends.isEmpty()) {
            return new ArrayList<>();
        }

        return friends;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<FriendEventsDto> getFriendsEvents(Long userId) {
        dataSearcher.findUserById(userId);

        Collection<UserShortDto> friends = getAllFriends(userId);

        if (friends.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, List<ParticipationRequestDto>> friendsRequest = getRequestByFriend(friends);

        if (friendsRequest.isEmpty()) {
            return new ArrayList<>();
        }

        Collection<FriendEventsDto> friendEvents = new ArrayList<>();
        Collection<EventShortDto> confirmedEvents;

        for (UserShortDto friend : friends) {
            confirmedEvents = friendsRequest.get(friend.getId()).stream()
                    .map(ParticipationRequestDto::getEvent)
                    .map(dataSearcher::findEventById)
                    .map(EventMapper::mapToEventShortDto)
                    .collect(toList());

            friendEvents.add(new FriendEventsDto(friend.getId(), friend.getName(), confirmedEvents));
        }

        return friendEvents;
    }

    private Map<Long, List<ParticipationRequestDto>> getRequestByFriend(Collection<UserShortDto> friends) {
        return eventRequestRepository.getConfirmedUserRequests(friends.stream()
                        .map(UserShortDto::getId)
                        .collect(toList()))
                .stream()
                .map(EventRequestMapper::mapToParticipationRequestDto)
                .collect(groupingBy(ParticipationRequestDto::getRequester, toList()));
    }
}
