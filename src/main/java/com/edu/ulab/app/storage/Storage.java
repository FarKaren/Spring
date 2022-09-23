package com.edu.ulab.app.storage;

import org.springframework.stereotype.Component;


@Component
public interface Storage<T> {

     boolean isExist(long id);

     T save(T t);

     T update(T t);

     void delete(long id);

      T find(long id);

}
