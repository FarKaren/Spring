package com.edu.ulab.app.repository;


import com.edu.ulab.app.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<Person, Long> {
}
