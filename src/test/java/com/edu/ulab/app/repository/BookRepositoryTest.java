package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.NoSuchElementException;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число insert должно равняться 2")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void createUserWithBook_thenAssertDmlCount() {

        //Given

        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader2");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author");
        book.setTitle("test");
        book.setPageCount(1000);
        book.setPerson(savedPerson);

        //When

        Book result = bookRepository.save(book);

        //Then

        assertThat(result.getPageCount()).isEqualTo(1000);
        assertThat(result.getTitle()).isEqualTo("test");
        assertSelectCount(2);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Обновить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_thenAssertDmlCount() {

        //Given

        Book foundBook = bookRepository.findById(2002L)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        Book updatedBook = new Book();
        updatedBook.setId(foundBook.getId());
        updatedBook.setAuthor(foundBook.getAuthor());
        updatedBook.setTitle("Hobbit");
        updatedBook.setPageCount(1500);

        //When

        Book result = bookRepository.save(updatedBook);

        //Then

        assertThat(result.getPageCount()).isEqualTo(1500);
        assertThat(result.getTitle()).isEqualTo("Hobbit");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBook_thenAssertDmlCount() {

        //When

        Book foundBook = bookRepository.findById(2002L)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        //Then

        assertThat(foundBook.getPageCount()).isEqualTo(5500);
        assertThat(foundBook.getTitle()).isEqualTo("default book");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить книги. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getBooksByUserId_getBook_thenAssertDmlCount() {

        //when

        List<Book> allUserBook = bookRepository.findBooksByUserId(1001L)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        //Then

        assertThat(allUserBook.get(0).getPageCount()).isEqualTo(5500);
        assertThat(allUserBook.get(0).getTitle()).isEqualTo("default book");
        assertThat(allUserBook.get(1).getPageCount()).isEqualTo(6655);
        assertThat(allUserBook.get(1).getTitle()).isEqualTo("more default book");
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить книгу. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_thenAssertDmlCount() {

        //Given

        Book foundBook = bookRepository.findById(2002L)
                .orElseThrow(() -> new NoSuchElementException("Book not found"));

        //When

        bookRepository.delete(foundBook);
        Book getBookAfterDelete = bookRepository.findById(2002L)
                .orElse(null);

        //Then

        assertNull(getBookAfterDelete);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Поптыка сохарнить книгу с null атрибутом.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void trySaveBookWithNullProperty() {

        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader2");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book authorNull = new Book();
        authorNull.setTitle("test");
        authorNull.setPageCount(1000);
        authorNull.setPerson(savedPerson);

        Book titleNull = new Book();
        titleNull.setAuthor("Test Author");
        titleNull.setPageCount(1000);
        titleNull.setPerson(savedPerson);

        Book personNull = new Book();
        personNull.setAuthor("Test Author");
        authorNull.setPageCount(1000);
        personNull.setTitle("test");

        //When

        Throwable titleException =
                assertThrows(DataIntegrityViolationException.class, () -> bookRepository.save(titleNull));
        Throwable authorException =
                assertThrows(DataIntegrityViolationException.class, () -> bookRepository.save(authorNull));
        Throwable personException =
                assertThrows(DataIntegrityViolationException.class, () -> bookRepository.save(personNull));

        //Then

        assertAll(
                () -> assertEquals("not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Book.title; " +
                        "nested exception is org.hibernate.PropertyValueException: " +
                        "not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Book.title", titleException.getMessage()),
                () -> assertEquals("not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Book.author; " +
                        "nested exception is org.hibernate.PropertyValueException: " +
                        "not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Book.author", authorException.getMessage()),
                () -> assertEquals("not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Book.person; " +
                        "nested exception is org.hibernate.PropertyValueException: " +
                        "not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Book.person", personException.getMessage()));

    }
}
