package ru.practicum.explore.services.privates;

import ru.practicum.explore.dto.friends.FriendEventsDto;
import ru.practicum.explore.dto.friends.FriendsDto;
import ru.practicum.explore.dto.user.UserShortDto;

import java.util.Collection;

public interface PrivateFriendsService {

    FriendsDto addFriend(Long userId, Long friendId);

    void deleteFriend(Long userId, Long friendId);

    Collection<UserShortDto> getAllFriends(Long userId);

    Collection<FriendEventsDto> getFriendsEvents(Long userId);
}
