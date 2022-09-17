package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.storage.StorageUtils;
import com.edu.ulab.app.storage.repositories.BookRepo;
import com.edu.ulab.app.storage.repositories.impl.BookRepoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepo bookrepo;
    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepoImpl bookRepo, BookMapper bookMapper) {
        this.bookrepo = bookRepo;
        this.bookMapper = bookMapper;
    }


    @Override
    public BookDto createBook(BookDto bookDto) {

        Long id = StorageUtils.generateId();
        BookEntity bookEntity = bookMapper.bookDtoToBookEntity(bookDto);
        bookEntity.setId(id);

        BookEntity createdBookEntity = bookrepo.create(bookEntity);


        return bookMapper.bookEntityToBookDto(createdBookEntity);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        BookEntity bookEntity = bookMapper.bookDtoToBookEntity(bookDto);

        BookEntity updatedBookEntity = bookrepo.update(bookEntity);

        return bookMapper.bookEntityToBookDto(updatedBookEntity);
    }

    @Override
    public BookDto getBookById(Long id) {

        Optional<BookEntity> optionalBookEntity = bookrepo.getById(id);

        return optionalBookEntity.map(bookMapper::bookEntityToBookDto).orElse(null);
    }

    @Override
    public void deleteBookById(Long id) {
        bookrepo.deleteById(id);
    }
}
