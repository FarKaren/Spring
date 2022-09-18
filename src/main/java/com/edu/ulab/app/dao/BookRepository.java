package com.edu.ulab.app.dao;

import com.edu.ulab.app.dto.BookDto;

public interface BookRepository {
    BookDto saveBook(BookDto bookDto);

    BookDto findBookById(int bookId);

    void deleteBookById(int bookId);
}
