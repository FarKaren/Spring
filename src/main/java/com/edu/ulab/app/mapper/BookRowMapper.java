package com.edu.ulab.app.mapper;

import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;


public class BookRowMapper implements RowMapper<Book> {
    private JdbcTemplate jdbcTemplate;

    public BookRowMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();
        book.setId(rs.getLong("ID"));
        book.setPerson(getPersonById(rs.getLong("USER_ID")));
        book.setTitle(rs.getString("TITLE"));
        book.setAuthor(rs.getString("AUTHOR"));
        book.setPageCount(rs.getInt("PAGE_COUNT"));
        return book;
    }

    private Person getPersonById(long id) {
        final String FIND_PERSON_SQL = "SELECT * FROM PERSON WHERE ID = ?";
        return Optional.ofNullable(jdbcTemplate.queryForObject(FIND_PERSON_SQL, new BeanPropertyRowMapper<>(Person.class), id))
                .orElseThrow(() -> new NotFoundException("Person not found"));
    }
}
