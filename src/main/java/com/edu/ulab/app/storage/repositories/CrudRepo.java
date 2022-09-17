package com.edu.ulab.app.storage.repositories;


import java.util.Optional;

public interface CrudRepo<T> {
    T create(T entity);

    T update(T entity);

    Optional<T> getById(Long id);

    void deleteById(Long id);
}
