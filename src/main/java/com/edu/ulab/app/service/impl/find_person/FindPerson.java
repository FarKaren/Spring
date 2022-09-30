package com.edu.ulab.app.service.impl.find_person;

import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
public class FindPerson {

    public static void personIsExist(@NonNull Long id, JdbcTemplate jdbcTemplate){
        final String FIND_SQL = ("SELECT * FROM PERSON WHERE ID = ?");
        Person foundPerson;
        try{
            foundPerson = jdbcTemplate.queryForObject(FIND_SQL, new BeanPropertyRowMapper<>(Person.class), id);
        } catch (DataAccessException e){
            log.error(e.getMessage());
            throw new NotFoundException("Person not found");
        }
        if(foundPerson == null)
            throw new NotFoundException("Person not found");
    }
}
