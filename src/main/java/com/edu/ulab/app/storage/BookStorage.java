package com.edu.ulab.app.storage;

import com.edu.ulab.app.entity.Book;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
@Component
public class BookStorage implements Storage<Book> {

    private final String PATH = "user_storage.json";

    @Override
    public boolean isExist(long userId) {
        StringBuilder bookIsPresent = new StringBuilder("0");

        JSONObject data = Optional.ofNullable(getDataFromStorage())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        if (data.isEmpty())
            return false;

        JSONArray userArray = (JSONArray) data.get("users");
        userArray.forEach(jsonObject -> {
            JSONObject userObject = (JSONObject) jsonObject;
            if ((long) userObject.get("id") == userId)
                bookIsPresent.replace(0, 1, "1");

        });
        return bookIsPresent.toString().equals("1");
    }

    @Override
    public Book save(Book book) {
        JSONObject data = Optional.ofNullable(getDataFromStorage())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        try (FileWriter file = new FileWriter(PATH)) {
            JSONArray userArray = (JSONArray) data.get("users");
            userArray.forEach(jsonObject -> {
                JSONObject userObject = (JSONObject) jsonObject;
                if ((long) userObject.get("id") == book.getUserId()) {
                    JSONArray bookArray = (JSONArray) userObject.get("books");
                    JSONObject newObject = new JSONObject();
                    newObject.put("id", book.getId());
                    newObject.put("userId", book.getUserId());
                    newObject.put("title", book.getTitle());
                    newObject.put("author", book.getAuthor());
                    newObject.put("pageCount", book.getPageCount());
                    bookArray.add(newObject);
                }
            });

            file.write(String.valueOf(data));
        } catch (IOException e) {
            log.error(e.getMessage());
            return new Book();
        }
        return book;
    }

    @Override
    public Book update(Book book) {
        JSONObject data = Optional.ofNullable(getDataFromStorage())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        StringBuilder bookIsPresent = new StringBuilder("0");
        try (FileWriter file = new FileWriter(PATH)) {
            JSONArray userArray = (JSONArray) data.get("users");
            userArray.forEach(jsonObject -> {
                JSONObject userObject = (JSONObject) jsonObject;
                if ((long) userObject.get("id") == book.getUserId()) {
                    JSONArray bookArray = (JSONArray) userObject.get("books");
                    bookArray.forEach(obj -> {
                        JSONObject bookObject = (JSONObject) obj;
                        if (bookObject.get("id") == book.getId()) {
                            bookIsPresent.replace(0, 1, "1");
                            bookObject.put("id", book.getId());
                            bookObject.put("userId", book.getUserId());
                            bookObject.put("title", book.getTitle());
                            bookObject.put("author", book.getAuthor());
                            bookObject.put("pageCount", book.getPageCount());
                        }
                    });
                }
            });

            if ("0".equals(bookIsPresent.toString())) {
                file.write(String.valueOf(data));
                return null;
            }

            file.write(String.valueOf(data));
        } catch (IOException e) {
            log.error(e.getMessage());
            return new Book();
        }
        return book;
    }

    @Override
    public void delete(long bookId) {
        JSONObject data = Optional.ofNullable(getDataFromStorage())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        try (FileWriter file = new FileWriter(PATH)) {
            JSONArray newArray = new JSONArray();
            JSONArray userArray = (JSONArray) data.get("users");
            userArray.forEach(jsonObject -> {
                JSONObject userObject = (JSONObject) jsonObject;
                JSONArray bookArray = (JSONArray) userObject.get("books");
                bookArray.forEach(obj -> {
                    JSONObject bookObject = (JSONObject) obj;
                    if ((long) bookObject.get("id") != bookId)
                        newArray.add(bookObject);
                });
                userObject.put("books", newArray);
            });

            file.write(String.valueOf(data));
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Book find(long bookId) {
        StringBuilder book = new StringBuilder();
        JSONObject data = Optional.ofNullable(getDataFromStorage())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        JSONArray userArray = (JSONArray) data.get("users");
        userArray.forEach(jsonObject -> {
            JSONObject userObject = (JSONObject) jsonObject;
            JSONArray bookArray = (JSONArray) userObject.get("books");
            bookArray.forEach(obj -> {
                JSONObject bookObject = (JSONObject) obj;
                if ((long) bookObject.get("id") == bookId) {
                    book.append(bookObject.toJSONString());
                }
            });
        });

        if (book.isEmpty())
            return null;

        return convertJSONtoBook(new Book(), book.toString());
    }

    public List<Book> findBooksByUserId(long userId) {
        List<Book> foundedBooks = new ArrayList<>();
        JSONObject data = Optional.ofNullable(getDataFromStorage())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));
        
        JSONArray userArray = (JSONArray) data.get("users");
        userArray.forEach(jsonObject -> {
            JSONObject userObject = (JSONObject) jsonObject;
            if ((long) userObject.get("id") == userId) {
                JSONArray bookArray = (JSONArray) userObject.get("books");
                bookArray.forEach(obj -> {
                    JSONObject bookObject = (JSONObject) obj;
                    String jsonString = bookObject.toJSONString();
                    Book book = convertJSONtoBook(new Book(), jsonString);
                    foundedBooks.add(book);
                });
            }
        });

        return foundedBooks;
    }

    public List<Long> findBooksIdByUserId(long userId) {
        List<Long> booksId = new ArrayList<>();
        JSONObject data = Optional.ofNullable(getDataFromStorage())
                .orElseThrow(() -> new StorageException("Incorrect data from storage"));

        JSONArray userArray = (JSONArray) data.get("users");
        userArray.forEach(jsonObject -> {
            JSONObject userObject = (JSONObject) jsonObject;
            if ((long) userObject.get("id") == userId) {
                JSONArray bookArray = (JSONArray) userObject.get("books");
                bookArray.forEach(obj -> {
                    JSONObject bookObject = (JSONObject) obj;
                    booksId.add((long) bookObject.get("id"));
                });
            }
        });

        return booksId;
    }

    private JSONObject getDataFromStorage() {
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
            log.error(e.getMessage());
        }
        return data;
    }
    
    private Book convertJSONtoBook(Book book, String jsonString){
        try {
            JSONObject result = (JSONObject) new JSONParser().parse(jsonString);
            book.setId((long) result.get("id"));
            book.setUserId((long) result.get("userId"));
            book.setTitle((String) result.get("title"));
            book.setAuthor((String) result.get("author"));
            book.setPageCount((long) result.get("pageCount"));
        } catch (ParseException e) {
            log.error(e.getMessage());
        }
        return book;
    }
}
