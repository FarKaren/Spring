package com.edu.ulab.app.web;

import com.edu.ulab.app.facade.BookDataFacade;
import com.edu.ulab.app.web.constant.WebConstant;
import com.edu.ulab.app.web.request.BookRequest;
import com.edu.ulab.app.web.response.BookResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Pattern;
import java.util.List;

import static com.edu.ulab.app.web.constant.WebConstant.REQUEST_ID_PATTERN;
import static com.edu.ulab.app.web.constant.WebConstant.RQID;


@Slf4j
@RestController
@RequestMapping(value = WebConstant.VERSION_URL + "/book",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class BookController {
    private final BookDataFacade bookDataFacade;

    public BookController(BookDataFacade bookDataFacade) {
        this.bookDataFacade = bookDataFacade;
    }


    @PostMapping(value = "/create/{userId}")
    @Operation(summary = "Create book row.",
            responses = {
                    @ApiResponse(description = "Book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookResponse.class)))})
    public BookResponse createBook(@RequestBody BookRequest request,
                                   @RequestHeader(RQID) @Pattern(regexp = REQUEST_ID_PATTERN) final String requestId,
                                   @PathVariable Long userId) {
        BookResponse response = bookDataFacade.createBook(request, userId);
        log.info("Response with created book: {}", response);
        return response;
    }

    @PutMapping(value = "/update/{userId}/{bookId}")
    @Operation(summary = "Update book row.",
            responses = {
                    @ApiResponse(description = "Book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookResponse.class)))})
    public BookResponse updateBook(@RequestBody BookRequest request,
                                   @PathVariable long userId,
                                   @PathVariable long bookId) {
        BookResponse response = bookDataFacade.updateBook(request, userId, bookId);
        log.info("Response with updated  book: {}", response);
        return response;
    }

    @GetMapping(value = "/get/{id}")
    @Operation(summary = "Get book row.",
            responses = {
                    @ApiResponse(description = "Book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookResponse.class)))})
    public BookResponse getBook(@PathVariable Long id) {
        BookResponse response = bookDataFacade.getBook(id);
        log.info("Response with book: {}", response);
        return response;
    }

    @GetMapping(value = "/get/books/{userId}")
    @Operation(summary = "Get all books row.",
            responses = {
                    @ApiResponse(description = "Book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookResponse.class)))})
    public List<BookResponse> getBooksByUserId(@PathVariable Long userId) {
        List<BookResponse> response = bookDataFacade.getBooksByUserId(userId);
        log.info("Response with list of books: {}", response);
        return response;
    }

    @DeleteMapping(value = "/delete/{id}")
    @Operation(summary = "Delete book row.",
            responses = {
                    @ApiResponse(description = "Book",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookResponse.class)))})
    public void deleteBook(@PathVariable Long id) {
        log.info("Delete book:  id {}", id);
        bookDataFacade.deleteBook(id);
    }
}
