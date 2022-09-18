package com.edu.ulab.app.storage.repositories.impl;

import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.storage.repositories.BookRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Repository
@Slf4j
public class BookRepoImpl implements BookRepo {

    private final Map<Long, BookEntity> books = new HashMap<>();

    @Override
    public BookEntity create(BookEntity entity) {
        books.put(entity.getId(), entity);

        BookEntity bookEntity = books.get(entity.getId());

        log.info("Created bookEntity: {}", bookEntity);

        return bookEntity;
    }

    @Override
    public BookEntity update(BookEntity entity) {

        //TODO: check if map contains value beforehand?

        BookEntity updatedBookEntity = books.put(entity.getId(), entity);

        log.info("Updated bookEntity, old value is:  {}", updatedBookEntity);

        return updatedBookEntity;
    }

    @Override
    public Optional<BookEntity> getById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    public Iterable<BookEntity> findAll() {
        return books.values();
    }

    @Override
    public void deleteById(Long id) {
        BookEntity deletedBookEntity = books.remove(id);

        log.info("Deleted bookEntity: {}", deletedBookEntity);

    }
}
