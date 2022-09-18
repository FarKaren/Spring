package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.edu.ulab.app.storage.generatorid.GeneratorUserId.userId;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final Storage storage;

    public UserServiceImpl(UserMapper mapper, Storage<User> storage) {
        this.mapper = mapper;
        this.storage = storage;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if(userDto.getId() != null)
            return updateUser(userDto);

        User user = mapper.userDtoToUser(userDto);
        user.setId(userId());
        User savedUser = (User) storage.save(user);
        return mapper.userToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        User user = mapper.userDtoToUser(userDto);
        User savedUser = (User) Optional.ofNullable(storage.update(user))
                .orElseThrow(() -> new NotFoundException("User not found"));
        return mapper.userToUserDto(savedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = (User) Optional.ofNullable(storage.find(id))
                .orElseThrow(() -> new NotFoundException("User not found"));
        return mapper.userToUserDto(user);
    }

    @Override
    public void deleteUserById(Long id) {
       storage.delete(id);
    }
}
