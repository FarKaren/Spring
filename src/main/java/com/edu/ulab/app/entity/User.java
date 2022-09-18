package com.edu.ulab.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long id;

    @JsonProperty("fullname")
    private String fullName;

    private String title;

    private int age;

    private List<Book> books;


}
