package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAllUsers() {
        log.info("Запрос списка всех пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Запрос пользователя с id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Создание нового пользователя: {}", userDto.getEmail());

        // Проверка уникальности email
        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Email {} уже используется", userDto.getEmail());
            throw new ConflictException("Пользователь с email " + userDto.getEmail() + " уже существует");
        }

        User user = UserMapper.toUser(userDto);
        User createdUser = userRepository.save(user);
        log.info("Пользователь создан с id: {}", createdUser.getId());

        return UserMapper.toUserDto(createdUser);
    }

    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        log.info("Обновление пользователя с id: {}", id);

        // Проверяем, что пользователь существует
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));

        // Проверка уникальности email, если он меняется
        if (userDto.getEmail() != null && !userDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), id)) {
                log.warn("Email {} уже используется другим пользователем", userDto.getEmail());
                throw new ConflictException("Email " + userDto.getEmail() + " уже используется");
            }
            existingUser.setEmail(userDto.getEmail());
        }

        // Обновляем имя, если оно передано
        if (userDto.getName() != null) {
            existingUser.setName(userDto.getName());
        }

        User updatedUser = userRepository.update(existingUser);
        log.info("Пользователь с id: {} обновлен", id);

        return UserMapper.toUserDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Удаление пользователя с id: {}", id);

        if (!userRepository.findById(id).isPresent()) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        userRepository.deleteById(id);
        log.info("Пользователь с id: {} удален", id);
    }
}