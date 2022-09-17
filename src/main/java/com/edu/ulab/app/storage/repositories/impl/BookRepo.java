package com.edu.ulab.app.storage.repositories.impl;

import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.storage.repositories.CrudRepo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
public class BookRepo implements CrudRepo<BookEntity> {

    private Map<Long, BookEntity> books = new HashMap<>();

    @Override
    public BookEntity create(BookEntity entity) {
        return books.put(entity.getId(), entity);
    }

    @Override
    public BookEntity update(BookEntity entity) {
        return books.put(entity.getId(), entity);

    }

    @Override
    public Optional<BookEntity> getById(Long id) {
        return Optional.of(books.get(id));
    }

    @Override
    public void deleteById(Long id) {
        books.remove(id);
    }
}
