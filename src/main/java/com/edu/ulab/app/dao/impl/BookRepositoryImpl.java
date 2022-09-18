package com.edu.ulab.app.dao.impl;

import com.edu.ulab.app.dao.BookRepository;
import com.edu.ulab.app.dto.BookDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class BookRepositoryImpl implements BookRepository {

    @Override
    public BookDto saveBook(BookDto bookDto) {
        return null;
    }

    @Override
    public BookDto findBookById(int bookId) {
        return null;
    }

    @Override
    public void deleteBookById(int bookId) {

    }
}
