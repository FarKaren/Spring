package com.edu.ulab.app.dao;


import com.edu.ulab.app.dto.UserDto;

public interface UserRepository {
 UserDto saveUser(UserDto userDto);

 UserDto findUserById(int userId);

 void deleteUserById(int userId);


}
