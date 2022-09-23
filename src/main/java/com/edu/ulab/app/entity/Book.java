package com.edu.ulab.app.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Book {

    private Long id;

    @JsonProperty("userid")
    private Long userId;

    private String title;

    private String author;

    @JsonProperty("pagecount")
    private long pageCount;
}
