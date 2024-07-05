package ru.practicum.explore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.explore.models.Friends;

import java.util.Collection;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
    Friends findByUserIdAndFriendId(Long userId, Long friendId);

    Collection<Friends> findByUserIdAndFriendshipStat(Long userId, Boolean friendshipStat);
}
