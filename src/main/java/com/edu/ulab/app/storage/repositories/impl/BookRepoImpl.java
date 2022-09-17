package com.edu.ulab.app.storage.repositories.impl;

import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.storage.repositories.BookRepo;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
public class BookRepoImpl implements BookRepo {

    private final Map<Long, BookEntity> books = new HashMap<>();

    @Override
    public BookEntity create(BookEntity entity) {
        books.put(entity.getId(), entity);

        return books.get(entity.getId());
    }

    @Override
    public BookEntity update(BookEntity entity) {

        return books.put(entity.getId(), entity);
    }

    @Override
    public Optional<BookEntity> getById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public void deleteById(Long id) {
        books.remove(id);
    }
}
