package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.BookStorage;
import com.edu.ulab.app.storage.Storage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.edu.ulab.app.storage.generatorid.GeneratorBookId.bookId;


@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final BookMapper mapper;
    private final BookStorage bookStorage;
    private final Storage storage;

    public BookServiceImpl(BookMapper mapper, BookStorage bookStorage, Storage<Book> storage) {
        this.mapper = mapper;
        this.bookStorage = bookStorage;
        this.storage = storage;
    }


    @Override
    public BookDto createBook(BookDto bookDto) {
        if(bookDto.getId() != null)
            return updateBook(bookDto);

       User result = Optional.ofNullable(userIsExist(bookDto.getUserId()))
               .orElseThrow(() -> new NotFoundException("User not found"));

        Book book = mapper.bookDtoToBook(bookDto);
        book.setId(bookId());
        Book savedBook = (Book) storage.save(book);
        return mapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        User result = Optional.ofNullable(userIsExist(bookDto.getUserId()))
                .orElseThrow(() -> new NotFoundException("User not found"));

        Book book = mapper.bookDtoToBook(bookDto);
        Book savedBook = (Book) Optional.ofNullable(storage.update(book))
                .orElseThrow(() -> new NotFoundException("Book not found"));
        return mapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = (Book) Optional.ofNullable(storage.find(id))
                .orElseThrow(() -> new NotFoundException("Book not found"));
        return mapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Long id) {
        storage.delete(id);
    }

    @Override
    public List<Long> getBooksIdByUserId(long userId){
        User result = Optional.ofNullable(userIsExist(userId))
                .orElseThrow(() -> new NotFoundException("User not found"));
        return bookStorage.findBooksIdByUserId(userId);

    }

    @Override
    public List<BookDto> getBooksByUserId(long userId) {
        User result = Optional.ofNullable(userIsExist(userId))
                .orElseThrow(() -> new NotFoundException("User not found"));

        List<Book> books = Optional.ofNullable(bookStorage.findBooksByUserId(userId))
                .orElseThrow(() -> new NotFoundException("Book not found"));

        return books.stream()
                .filter(Objects::nonNull)
                .map(mapper::bookToBookDto)
                .collect(Collectors.toList());
    }

    public User userIsExist(long userId){
        return bookStorage.isExist(userId) ? new User() : null;
    }

}
