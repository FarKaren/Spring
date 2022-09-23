package com.edu.ulab.app.dao.impl;


import com.edu.ulab.app.dao.UserRepository;
import com.edu.ulab.app.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class UserRepositoryImpl implements UserRepository {


    @Override
    public UserDto saveUser(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto findUserById(int userId) {
        return null;
    }

    @Override
    public void deleteUserById(int userId) {

    }
}
