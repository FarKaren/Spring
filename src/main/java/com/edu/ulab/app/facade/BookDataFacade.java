package com.edu.ulab.app.facade;


import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.response.BookResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class BookDataFacade {

    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public BookDataFacade(UserServiceImplTemplate userService,
                          BookServiceImplTemplate bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public BookResponse createBook(@NonNull BookRequest bookRequest, @NonNull Long userId) {
        log.info("Got  book create request: {}", bookRequest);
        BookDto bookDto = bookMapper.bookRequestToBookDto(bookRequest);
        bookDto.setUserId(userId);
        log.info("Mapped book request: {}", bookDto);

        BookDto createdBook = bookService.createBook(bookDto);
        log.info("Created book: {}", createdBook);

        return buildBookResponse(createdBook);
    }

    public BookResponse updateBook(@NonNull BookRequest bookRequest, @NonNull Long userId, @NonNull Long bookId) {
        log.info("Got  book update request: {}", bookRequest);
        BookDto bookDto = bookMapper.bookRequestToBookDto(bookRequest);
        bookDto.setUserId(userId);
        bookDto.setId(bookId);
        log.info("Mapped book request: {}", bookDto);

        BookDto updatedBook = bookService.updateBook(bookDto);
        log.info("Updated book: {}", updatedBook);

        return buildBookResponse(updatedBook);
    }

    public List<BookResponse> getBooksByUserId(@NonNull Long userId) {
        List<BookDto> books = bookService.getBooksByUserId(userId);
        log.info("Collected book: {}", books);

        return books.stream()
                .map(this::buildBookResponse)
                .collect(Collectors.toList());
    }

    public BookResponse getBook(@NonNull Long bookId) {
        log.info("Got book get request: {}", bookId);
        BookDto getBook = bookService.getBookById(bookId);
        log.info("Get book: {}", getBook);

        return buildBookResponse(getBook);
    }


    public void deleteBook(@NonNull Long bookId) {
        log.info("Got book delete request: {}", bookId);
        bookService.deleteBookById(bookId);
        log.info("Book deleted");
    }

    private BookResponse buildBookResponse(BookDto bookDto) {
        return BookResponse.builder()
                .id(bookDto.getId())
                .userId(bookDto.getUserId())
                .title(bookDto.getTitle())
                .author(bookDto.getAuthor())
                .pageCount(bookDto.getPageCount())
                .build();
    }

}
