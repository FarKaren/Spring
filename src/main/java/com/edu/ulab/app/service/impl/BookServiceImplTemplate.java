package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.BookRowMapper;
import com.edu.ulab.app.service.BookService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookServiceImplTemplate implements BookService {
    private final JdbcTemplate jdbcTemplate;
    private final BookMapper mapper;

    public BookServiceImplTemplate(JdbcTemplate jdbcTemplate, BookMapper mapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, PERSON_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(
                    connection -> {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getPerson().getId());
                        return ps;
                    }, keyHolder);
        }catch (DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }
        log.info("Save book in data base");
        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        final String UPDATE_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ?, PERSON_ID = ? WHERE ID = ?";
        BookDto checkIfExist = getBookById(bookDto.getId());
        try {
            jdbcTemplate.update(UPDATE_SQL, bookDto.getTitle(), bookDto.getAuthor(), bookDto.getPageCount(),
                    bookDto.getPerson().getId(), bookDto.getId());
        }catch (DataAccessException e){
            throw new RuntimeException(e.getMessage());
        }
        log.info("Update book in data base {}", bookDto);

        return bookDto;
    }

    @Override
    public BookDto getBookById(Long id) {
        final String GET_SQL = "SELECT * FROM BOOK WHERE ID = ?";
        Book foundBook;
        try{
            foundBook = jdbcTemplate.queryForObject(GET_SQL, new BeanPropertyRowMapper<>(Book.class), id);
        } catch (DataAccessException e){
            log.error(e.getMessage());
            throw new NotFoundException("Book not found");
        }
        if(foundBook == null)
            throw new NotFoundException("Book not found");
        log.info("Get book from data base");

        return mapper.bookToBookDto(foundBook);
    }

    @Override
    public void deleteBookById(Long id) {
        final String DELETE_SQL = ("DELETE FROM BOOK WHERE ID = ?");
        jdbcTemplate.update(DELETE_SQL, id);
        log.info("Delete book from data base");
    }

    @Override
    public List<Long> getBooksIdByUserId(long id) {
        final String GET_LIST_SQL = "SELECT * FROM BOOK WHERE PERSON_ID = ?";
        List<Book> foundBooks = getBookList(GET_LIST_SQL, id);
        log.info("Get books from data base");

        return foundBooks.stream()
                .filter(Objects::nonNull)
                .map(Book::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDto> getBooksByUserId(long id) {
        final String GET_LIST_SQL = "SELECT * FROM BOOK WHERE PERSON_ID = ?";
        List<Book> foundBooks = getBookList(GET_LIST_SQL, id);
        log.info("Get books from data base");

        return foundBooks.stream()
                .filter(Objects::nonNull)
                .map(mapper::bookToBookDto)
                .collect(Collectors.toList());
    }


    private List<Book> getBookList(@NonNull String sqlQuery, @NonNull Long id){
        List<Book> bookList;
        try{
            bookList = jdbcTemplate.query(sqlQuery, new BookRowMapper(jdbcTemplate), id);
        } catch (DataAccessException e){
            log.error(e.getMessage());
            throw new NotFoundException("Book not found");
        }
        if(bookList.isEmpty())
            throw new NotFoundException("Book not found");
        return bookList;
    }
}
