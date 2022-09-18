package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.User;
import com.edu.ulab.app.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
public class UserStorage implements Storage<User> {

    private final String PATH = "user_storage.json";

    @Override
    public boolean isExist(long id) {
        StringBuilder userIsPresent = new StringBuilder("0");
        JSONObject data = Optional.ofNullable(getDataFromFile())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        if (data.isEmpty())
            return false;

        JSONArray userArray = (JSONArray) data.get("users");
        userArray.forEach(jsonObject -> {
            JSONObject userObject = (JSONObject) jsonObject;
            if ((long) userObject.get("id") == id)
                userIsPresent.replace(0, 1, "1");
        });
        return userIsPresent.toString().equals("1");

    }

    @Override
    public User save(User user) {
        JSONObject data = Optional.ofNullable(getDataFromFile())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        try (FileWriter file = new FileWriter(PATH)) {
            if (data.isEmpty()) {
                data = new JSONObject();
                data.put("users", new JSONArray());
            }

            JSONArray userArray = (JSONArray) data.get("users");
            JSONObject newObject = new JSONObject();
            newObject.put("id", user.getId());
            newObject.put("fullname", user.getFullName());
            newObject.put("title", user.getTitle());
            newObject.put("age", user.getAge());
            newObject.put("books", Collections.EMPTY_LIST);
            userArray.add(newObject);

            file.write(String.valueOf(data));
        } catch (IOException e) {
            log.error(e.getMessage());
            return new User();
        }
        return user;
    }

    @Override
    public User update(User user) {
        if (!isExist(user.getId()))
            return null;

        JSONObject data = Optional.ofNullable(getDataFromFile())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        if (data.isEmpty())
            return null;

        try (FileWriter file = new FileWriter(PATH)) {
            JSONArray userArray = (JSONArray) data.get("users");
            userArray.forEach(jsonObject -> {
                JSONObject userObject = (JSONObject) jsonObject;
                if ((long) userObject.get("id") == user.getId()) {
                    JSONArray books = (JSONArray) userObject.get("books");
                    userObject.put("id", user.getId());
                    userObject.put("fullname", user.getFullName());
                    userObject.put("title", user.getTitle());
                    userObject.put("age", user.getAge());
                    userObject.put("books", books);
                }
            });

            file.write(String.valueOf(data));
        } catch (IOException e) {
            log.error(e.getMessage());
            return new User();
        }
        return user;
    }

    @Override
    public void delete(long userId) {
        JSONObject data = Optional.ofNullable(getDataFromFile())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        try (FileWriter file = new FileWriter(PATH)) {
            JSONArray newArray = new JSONArray();
            JSONArray userArray = (JSONArray) data.get("users");
            userArray.forEach(jsonObject -> {
                JSONObject userObject = (JSONObject) jsonObject;
                if ((long) userObject.get("id") != userId)
                    newArray.add(userObject);
            });
            data.put("users", newArray);

            file.write(String.valueOf(data));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public User find(long userId) {
        User foundUser = new User();
        JSONObject data = Optional.ofNullable(getDataFromFile())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        StringBuilder user = new StringBuilder();
        JSONArray userArray = (JSONArray) data.get("users");
        userArray.forEach(jsonObject -> {
            JSONObject userObject = (JSONObject) jsonObject;
            if ((long) userObject.get("id") == userId) {
                user.append(userObject.toJSONString());
            }
        });

        if (user.isEmpty())
            return null;

        try {
            JSONObject result = (JSONObject) new JSONParser().parse(user.toString());
            foundUser.setId((long) result.get("id"));
            foundUser.setFullName((String) result.get("fullname"));
            foundUser.setTitle((String) result.get("title"));
            foundUser.setAge(Math.toIntExact((long) result.get("age")));
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return foundUser;
    }

    private JSONObject getDataFromFile() {
        JSONObject data = null;

        try {
            StringBuilder sb = new StringBuilder();
            List<String> lines = Files.readAllLines(Paths.get(PATH));
            lines.forEach(sb::append);
            String stringUsers = sb.toString();
            if (stringUsers.isEmpty())
                return new JSONObject();

            JSONParser parser = new JSONParser();
            data = (JSONObject) parser.parse(stringUsers);
        } catch (ParseException | IOException e) {
            log.error("An error has occurred: {}", e.getMessage());
        }

        return data;
    }

}