package com.edu.ulab.app.web.request;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserBookRequest {

    @NotNull(message = "Person request can't be empty")
    private UserRequest userRequest;

    private List<BookRequest> bookRequests;
}
