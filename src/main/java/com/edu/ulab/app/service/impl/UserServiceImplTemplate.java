package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImplTemplate implements UserService {

    private final UserMapper mapper;
    private final JdbcTemplate jdbcTemplate;

    public UserServiceImplTemplate(UserMapper mapper, JdbcTemplate jdbcTemplate) {
        this.mapper = mapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        final String INSERT_SQL = "INSERT INTO PERSON(FULL_NAME, TITLE, AGE, CODE) VALUES(?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, userDto.getFullName());
                        ps.setString(2, userDto.getTitle());
                        ps.setLong(3, userDto.getAge());
                        ps.setInt(4, userDto.getCode());
                        return ps;
                    }, keyHolder);
        }catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
        log.info("Save user in data base {}", userDto);
        userDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return userDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        final String UPDATE_SQL = ("UPDATE PERSON SET FULL_NAME = ?, TITLE = ?, AGE = ?, CODE = ?, WHERE ID = ?");
        try {
            jdbcTemplate.update(UPDATE_SQL, userDto.getFullName(), userDto.getTitle(),
                    userDto.getAge(), userDto.getId(), userDto.getCode());
        }catch (DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }
        log.info("Update user in data base {}", userDto);

        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        final String GET_SQL = ("SELECT * FROM PERSON WHERE ID = ?");
        Person foundPerson;
        try{
            foundPerson = jdbcTemplate.queryForObject(GET_SQL, new BeanPropertyRowMapper<>(Person.class), id);
        } catch (DataAccessException e){
            log.error(e.getMessage());
            throw new NotFoundException("User not found");
        }
        if(foundPerson == null)
            throw new NotFoundException("User not found");
        log.info("Get user from data base {}", foundPerson);

        return mapper.personToUserDto(foundPerson);
    }

    @Override
    public void deleteUserById(Long id) {
        final String DELETE_USER_SQL = ("DELETE FROM PERSON WHERE ID = ?");
        jdbcTemplate.update(DELETE_USER_SQL, id);
        log.info("Delete user from data base");
    }

}
