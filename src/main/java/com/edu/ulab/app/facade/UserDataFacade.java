package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserDataFacade(UserService userService,
                          BookService bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
    }

    public UserBookResponse createUserWithBooks(@NonNull UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);
        List<Long> bookIdList = new ArrayList<>();
        if(userBookRequest.getBookRequests() != null) {
            bookIdList = userBookRequest.getBookRequests()
                    .stream()
                    .filter(Objects::nonNull)
                    .map(bookMapper::bookRequestToBookDto)
                    .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                    .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                    .map(bookService::createBook)
                    .peek(createdBook -> log.info("Created book: {}", createdBook))
                    .map(BookDto::getId)
                    .toList();
            log.info("Collected book ids: {}", bookIdList);
        }

        return buildBookResponse(createdUser, bookIdList);
    }

    public UserBookResponse updateUserWithBooks(@NonNull UserBookRequest userBookRequest, @NonNull long userId) {
        log.info("Got user update request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        userDto.setId(userId);
        log.info("Mapped user request: {}", userDto);

        UserDto updatedUser = userService.updateUser(userDto);
        log.info("Created user: {}", updatedUser);
        List<Long> bookIdList = bookService.getBooksIdByUserId(updatedUser.getId());
        log.info("Collected book ids: {}", bookIdList);

        return buildBookResponse(updatedUser, bookIdList);
    }

    public UserBookResponse getUserWithBooks(@NonNull Long userId) {
        log.info("Got user get request: {}", userId);
        UserDto getUser = userService.getUserById(userId);
        log.info("Get user: {}", getUser);
        List<Long> bookIdList = bookService.getBooksIdByUserId(userId);
        log.info("Collected book ids: {}", bookIdList);

        return buildBookResponse(getUser, bookIdList);
    }

    public void deleteUserWithBooks(@NonNull Long userId) {
        log.info("Got user delete request: {}", userId);
        userService.deleteUserById(userId);
        log.info("User deleted");
    }

    private UserBookResponse buildBookResponse(UserDto userDto, List<Long> bookIdList){
       return UserBookResponse.builder()
                .userId(userDto.getId())
                .fullName(userDto.getFullName())
                .title(userDto.getTitle())
                .age(userDto.getAge())
                .booksIdList(bookIdList)
                .build();
    }

}
