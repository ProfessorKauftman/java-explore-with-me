package ru.practicum.explore.services.admins;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore.dto.user.NewUserRequest;
import ru.practicum.explore.dto.user.UserDto;
import ru.practicum.explore.mappers.UserMapper;
import ru.practicum.explore.models.User;
import ru.practicum.explore.repositories.UserRepository;
import ru.practicum.explore.component.DataSearcher;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    private final UserRepository userRepository;
    private final DataSearcher dataSearcher;

    @Transactional(readOnly = true)
    @Override
    public Collection<UserDto> getAllUsers(List<Long> ids, Pageable pageable) {
        if (ids == null) {
            return userRepository.findAll(pageable).stream()
                    .map(UserMapper::mapToUserDto)
                    .collect(Collectors.toList());
        }

        Page<User> list = userRepository.getAllByIdIn(pageable, ids);

        if (list.getContent().isEmpty()) {
            return new ArrayList<>();
        }

        return list.stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public UserDto createUser(NewUserRequest newUserRequest) {
        User user = UserMapper.mapToUser(newUserRequest);

        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        dataSearcher.findUserById(userId);
        userRepository.deleteById(userId);
    }
}