package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {

        //Given

        UserDto userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");
        userDto.setCode(6);

        Person person = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");
        person.setCode(6);

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");
        savedPerson.setCode(6);

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");
        result.setCode(6);


        //When

        when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        when(userRepository.save(person)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //Then

        UserDto userDtoResult = userService.createUser(userDto);
        assertEquals(1L, userDtoResult.getId());
    }

    // update
    // get
    // get all
    // delete

    // * failed
    //         doThrow(dataInvalidException).when(testRepository)
    //                .save(same(test));
    // example failed
    //  assertThatThrownBy(() -> testeService.createTest(testRequest))
    //                .isInstanceOf(DataInvalidException.class)
    //                .hasMessage("Invalid data set");

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updatePerson_Test() {

        //Given

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setAge(22);
        userDto.setFullName("updated name");
        userDto.setTitle("updated title");
        userDto.setCode(6);

        Person foundPerson = new Person();
        foundPerson.setId(1L);
        foundPerson.setFullName("test name");
        foundPerson.setAge(11);
        foundPerson.setTitle("test title");
        foundPerson.setCode(6);

        Person savedPerson = new Person();
        savedPerson.setId(1L);
        savedPerson.setFullName("updated name");
        savedPerson.setAge(22);
        savedPerson.setTitle("updated title");
        savedPerson.setCode(6);

        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(22);
        result.setFullName("updated name");
        result.setTitle("updated title");
        result.setCode(6);


        //when

        when(userRepository.findById(1L)).thenReturn(Optional.of(foundPerson));
        when(userRepository.save(foundPerson)).thenReturn(savedPerson);
        when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.updateUser(userDto);
        assertEquals("updated title", userDtoResult.getTitle());
        assertEquals("updated name", userDtoResult.getFullName());
    }

    @Test
    @DisplayName("Получения пользователя из базы. Должно пройти успешно.")
    void getPerson_Test() {

        //given

        Person foundPerson = new Person();
        foundPerson.setId(1L);
        foundPerson.setFullName("test name");
        foundPerson.setAge(11);
        foundPerson.setTitle("test title");
        foundPerson.setCode(6);


        UserDto result = new UserDto();
        result.setId(1L);
        result.setAge(22);
        result.setFullName("updated name");
        result.setTitle("updated title");
        result.setCode(6);


        //when

        when(userRepository.findById(1L)).thenReturn(Optional.of(foundPerson));
        when(userMapper.personToUserDto(foundPerson)).thenReturn(result);


        //then

        UserDto userDtoResult = userService.getUserById(1L);
        assertEquals(1L, userDtoResult.getId());
    }

    @Test
    @DisplayName("Удаление пользователя из базы. Должно пройти успешно.")
    void deletePerson_Test() {

        //given

        Person foundPerson = new Person();
        foundPerson.setId(1L);
        foundPerson.setFullName("test name");
        foundPerson.setAge(11);
        foundPerson.setTitle("test title");
        foundPerson.setCode(6);

        //when

        when(userRepository.findById(1L)).thenReturn(Optional.of(foundPerson));

        //then

        userService.deleteUserById(1L);
        verify(userRepository, times(1)).delete(foundPerson);
    }

}


