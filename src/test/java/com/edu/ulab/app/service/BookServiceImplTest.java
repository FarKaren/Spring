package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Тестирование функционала {@link BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {

        //given

        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setPerson(person);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setPerson(person);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(userRepository.findById(1L)).thenReturn(Optional.of(person));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then

        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());

    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {

        //given

        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setPerson(person);
        bookDto.setAuthor("updated author");
        bookDto.setTitle("updated title");
        bookDto.setPageCount(1000);

        Book foundBook = new Book();
        foundBook.setId(1L);
        foundBook.setPageCount(1000);
        foundBook.setTitle("test title");
        foundBook.setAuthor("test author");
        foundBook.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPerson(person);
        savedBook.setAuthor("updated author");
        savedBook.setTitle("updated title");
        savedBook.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setPerson(person);
        result.setAuthor("updated author");
        result.setTitle("updated title");
        result.setPageCount(1000);


        //when

        when(bookRepository.findById(1L)).thenReturn(Optional.of(foundBook));
        when(bookRepository.save(foundBook)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then

        BookDto bookDtoResult = bookService.updateBook(bookDto);
        assertEquals("updated title", bookDtoResult.getTitle());
        assertEquals("updated author", bookDtoResult.getAuthor());
    }

    @Test
    @DisplayName("Получить книгу. Должно пройти успешно.")
    void getBook_Test() {

        //given

        Person person = new Person();
        person.setId(1L);


        Book foundBook = new Book();
        foundBook.setId(1L);
        foundBook.setPageCount(1000);
        foundBook.setTitle("test title");
        foundBook.setAuthor("test author");
        foundBook.setPerson(person);


        BookDto result = new BookDto();
        result.setId(1L);
        result.setPerson(person);
        result.setAuthor("updated author");
        result.setTitle("updated title");
        result.setPageCount(1000);

        //when

        when(bookRepository.findById(1L)).thenReturn(Optional.of(foundBook));
        when(bookMapper.bookToBookDto(foundBook)).thenReturn(result);

        //then

        BookDto bookDtoResult = bookService.getBookById(1L);
        assertEquals("updated title", bookDtoResult.getTitle());
        assertEquals("updated author", bookDtoResult.getAuthor());
    }

    @Test
    @DisplayName("Получить список id книг по id юзера. Должно пройти успешно.")
    void getBooksIdByUserId_Test() {

        //given

        Person person = new Person();
        person.setId(1L);

        Book foundBook1 = new Book();
        foundBook1.setId(1L);
        foundBook1.setPageCount(1000);
        foundBook1.setTitle("test title");
        foundBook1.setAuthor("test author");
        foundBook1.setPerson(person);

        Book foundBook2 = new Book();
        foundBook2.setId(2L);
        foundBook2.setPageCount(2000);
        foundBook2.setTitle("test title2");
        foundBook2.setAuthor("test author2");
        foundBook2.setPerson(person);

        List<Book> bookList = List.of(foundBook1, foundBook2);

        //when

        when(bookRepository.findBooksByUserId(1L)).thenReturn(Optional.of(bookList));

        //then

        List<Long> bookDtoResult = bookService.getBooksIdByUserId(1L);
        assertEquals(1L, bookDtoResult.get(0));
        assertEquals(2L, bookDtoResult.get(1));
    }

    @Test
    @DisplayName("Получить список книг по id юзера. Должно пройти успешно.")
    void getBooksByUserId_Test() {

        //given

        Person person = new Person();
        person.setId(1L);

        Book foundBook1 = new Book();
        foundBook1.setId(1L);
        foundBook1.setPageCount(1000);
        foundBook1.setTitle("test title");
        foundBook1.setAuthor("test author");
        foundBook1.setPerson(person);

        Book foundBook2 = new Book();
        foundBook2.setId(2L);
        foundBook2.setPageCount(2000);
        foundBook2.setTitle("test title2");
        foundBook2.setAuthor("test author2");
        foundBook2.setPerson(person);

        List<Book> bookList = List.of(foundBook1, foundBook2);

        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setPageCount(1000);
        bookDto1.setTitle("test title");
        bookDto1.setAuthor("test author");
        bookDto1.setPerson(person);

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setPageCount(2000);
        bookDto2.setTitle("test title2");
        bookDto2.setAuthor("test author2");
        bookDto2.setPerson(person);

        //when

        when(bookRepository.findBooksByUserId(1L)).thenReturn(Optional.of(bookList));
        when(bookMapper.bookToBookDto(foundBook1)).thenReturn(bookDto1);
        when(bookMapper.bookToBookDto(foundBook2)).thenReturn(bookDto2);

        //then

        List<BookDto> bookDtoResult = bookService.getBooksByUserId(1L);
        assertEquals(1L, bookDtoResult.get(0).getId());
        assertEquals(2L, bookDtoResult.get(1).getId());
    }

    @Test
    @DisplayName("Удалить книгу. Должно пройти успешно.")
    void deleteBook_Test() {

        //given

        Person person = new Person();
        person.setId(1L);

        Book foundBook = new Book();
        foundBook.setId(1L);
        foundBook.setPageCount(1000);
        foundBook.setTitle("test title");
        foundBook.setAuthor("test author");
        foundBook.setPerson(person);


        //when

        when(bookRepository.findById(1L)).thenReturn(Optional.of(foundBook));

        //then

        bookService.deleteBookById(1L);
        verify(bookRepository, times(1)).delete(foundBook);
    }
}


