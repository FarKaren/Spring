package com.edu.ulab.app.web.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class BookRequest {

    @NotNull
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё]+", message = "Title don't match the format")
    private String title;

    @NotNull
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё]+", message = "Author don't match the format")
    private String author;

    @NotNull
    @Pattern(regexp = "[0-9]+", message = "Page count don't match the format")
    private long pageCount;
}
