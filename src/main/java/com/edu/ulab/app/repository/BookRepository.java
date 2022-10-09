package com.edu.ulab.app.repository;


import com.edu.ulab.app.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    @Query(value = "select * from ulab_edu.book where person_id = ?", nativeQuery = true)
    Optional<List<Book>> findBooksByUserId(long id);
}
