package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserMapper mapper;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public UserServiceImpl(UserMapper mapper, UserRepository userRepository, BookRepository bookRepository) {
        this.mapper = mapper;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person person = mapper.userDtoToPerson(userDto);
        Person savedPerson = userRepository.save(person);
        log.info("Save user in data base {}", savedPerson);

        return mapper.personToUserDto(savedPerson);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person foundPerson = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        foundPerson.setFullName(userDto.getFullName());
        foundPerson.setTitle(userDto.getTitle());
        foundPerson.setAge(userDto.getAge());

        Person savedPerson = userRepository.save(foundPerson);
        log.info("Update user in data base {}", savedPerson);

        return  mapper.personToUserDto(savedPerson);
    }

    @Override
    public UserDto getUserById(Long id) {
        Person foundPerson = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.info("Get user from data base {}", foundPerson);

        return mapper.personToUserDto(foundPerson);
    }

    @Override
    public void deleteUserById(Long id) {
        Person foundPerson = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
        foundPerson.getBooks().forEach(bookRepository::delete);
        log.info("Delete all user books");

        userRepository.delete(foundPerson);
        log.info("Delete user from data base");
    }

}
