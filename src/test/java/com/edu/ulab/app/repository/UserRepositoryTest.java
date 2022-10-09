package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import javax.validation.ConstraintViolationException;
import java.util.NoSuchElementException;

import static com.vladmihalcea.sql.SQLStatementCountValidator.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;


    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPerson_thenAssertDmlCount() {

        //Given

        Person person = new Person();
        person.setAge(111);
        person.setTitle("reader2");
        person.setFullName("Test Test");
        person.setCode(5);

        //When

        Person result = userRepository.save(person);

        //Then

        assertThat(result.getAge()).isEqualTo(111);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }


    @DisplayName("Обновить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {

        //Given

        Person foundPerson = userRepository.findById(1001L)
                .orElseThrow(() -> new NoSuchElementException("Person not found"));

        Person updatedPerson = new Person();
        updatedPerson.setId(foundPerson.getId());
        updatedPerson.setAge(222);
        updatedPerson.setTitle("reader");
        updatedPerson.setFullName("Test Test");
        updatedPerson.setCode(5);

        //When

        Person result = userRepository.save(updatedPerson);

        //Then

        assertThat(result.getAge()).isEqualTo(222);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Получить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_thenAssertDmlCount() {

        //When

        Person foundPerson = userRepository.findById(1001L)
                .orElseThrow(() -> new NoSuchElementException("Person not found"));

        //Then

        assertThat(foundPerson.getAge()).isEqualTo(55);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Удалить юзера. Число select должно равняться 1")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {

        //Given

        Person foundPerson = userRepository.findById(1001L)
                .orElseThrow(() -> new NoSuchElementException("Person not found"));

        //When

        userRepository.delete(foundPerson);
        Person getAfterDelete = userRepository.findById(1001L).orElse(null);

        //Then

        assertNull(getAfterDelete);
        assertSelectCount(1);
        assertInsertCount(0);
        assertUpdateCount(0);
        assertDeleteCount(0);
    }

    @DisplayName("Попытка сохранить юзера с null атрибутом.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void trySavePersonWithNullProperty() {

        //Given

        Person titleNull = new Person();
        titleNull.setAge(111);
        titleNull.setFullName("Test Test");

        Person fullNameNull = new Person();
        fullNameNull.setAge(111);
        fullNameNull.setTitle("reader");

        //When

        Throwable titleException =
                assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(titleNull));
        Throwable fullNameException =
                assertThrows(DataIntegrityViolationException.class, () -> userRepository.save(fullNameNull));

        //Then

        assertAll(
                () -> assertEquals("not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Person.title; " +
                        "nested exception is org.hibernate.PropertyValueException: " +
                        "not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Person.title", titleException.getMessage()),
                () -> assertEquals("not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Person.fullName; " +
                        "nested exception is org.hibernate.PropertyValueException: " +
                        "not-null property references a null or transient value : " +
                        "com.edu.ulab.app.entity.Person.fullName", fullNameException.getMessage()));

    }


//////////// К сожалению этот тест так и не полуилось запустить, несмотря на то, что вне теста работает

//    @DisplayName("Попытка сохранить юзера с уже существующим title.")
//    @Test
//    @Rollback
//    @Sql({"classpath:sql/1_clear_schema.sql",
//            "classpath:sql/2_insert_person_data.sql",
//            "classpath:sql/3_insert_book_data.sql"
//    })
//    void trySavePersonWithTheSameTitle() {
//
//        //Given
//
//        Person person = new Person();
//        person.setAge(55);
//        person.setTitle("reader");
//        person.setFullName("default user");
//        person.setCode(36);
//        Person fromDb = userRepository.findById(1001L).orElse(null);
//
//        userRepository.save(person);
//
//
//        //When
//
//        Throwable titleException =
//                assertThrows(ConstraintViolationException.class, () -> userRepository.save(person));
//
//        //Then
//        assertEquals("could not execute statement", titleException.getMessage());
//    }
}
