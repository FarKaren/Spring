package com.edu.ulab.app.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String fullName;

    @NotNull
    private String title;

    @NotNull
    private int age;

    @NotNull
    @ToString.Exclude
    @OneToMany(mappedBy = "person")
    private List<Book> books = new ArrayList<>();

    public void addBook(Book book) {
        books.add(book);
    }

}
