package com.edu.ulab.app.web.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserRequest {

    @NotNull
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё]+", message = "Full name don't match the format")
    private String fullName;

    @NotNull
    @Pattern(regexp = "[A-Za-zА-Яа-яЁё]+", message = "Title don't match the format")
    private String title;

    @NotNull
    @Pattern(regexp = "[0-9]+", message = "Age don't match the format")
    private int age;
}
