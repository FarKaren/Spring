package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
public class BookServiceImpl implements BookService {
    private final BookMapper mapper;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public BookServiceImpl(BookMapper mapper, BookRepository bookRepository, UserRepository userRepository) {
        this.mapper = mapper;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Person person = userRepository.findById(bookDto.getPerson().getId())
                .orElseThrow(() -> new NoSuchElementException("Person not found"));
        Book book = mapper.bookDtoToBook(bookDto);
        book.setPerson(person);
        Book savedBook = bookRepository.save(book);
        log.info("Save book in data base {}", savedBook);

        return mapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book foundBook = bookRepository.findById(bookDto.getId())
                .orElseThrow(() -> new NotFoundException("Book not found"));

        foundBook.setTitle(bookDto.getTitle());
        foundBook.setAuthor(bookDto.getAuthor());
        foundBook.setPageCount(bookDto.getPageCount());

        Book savedBook = bookRepository.save(foundBook);
        log.info("Update book in data base {}", savedBook);

        return mapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto getBookById(Long id) {
        Book foundBook = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        log.info("Get user from data base {}", foundBook);

        return mapper.bookToBookDto(foundBook);

    }

    @Override
    public List<Long> getBooksIdByUserId(long id) {
        List<Book> foundBooks = bookRepository.findBooksByUserId(id)
                .orElseThrow(() -> new NotFoundException("Books not found"));
        log.info("Get books from data base");

        return foundBooks.stream()
                .filter(Objects::nonNull)
                .map(Book::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDto> getBooksByUserId(long id) {
        List<Book> foundBooks = bookRepository.findBooksByUserId(id)
                .orElseThrow(() -> new NotFoundException("Books not found"));
        log.info("Get books from data base");

        return foundBooks.stream()
                .filter(Objects::nonNull)
                .map(mapper::bookToBookDto)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteBookById(Long id) {
        Book foundBook = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found"));
        log.info("Book deleted from data base {}", foundBook);
        bookRepository.delete(foundBook);
    }
}
