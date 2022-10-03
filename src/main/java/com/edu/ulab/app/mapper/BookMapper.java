package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.web.request.BookRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class BookMapper {
    public abstract BookDto bookRequestToBookDto(BookRequest bookRequest);

    public abstract BookRequest bookDtoToBookRequest(BookDto bookDto);

    public abstract Book bookDtoToBook(BookDto bookDto);

    public  BookDto bookToBookDto(Book book){
        BookDto bookDto = new BookDto();

        bookDto.setId(book.getId());
        bookDto.setUserId(book.getPerson().getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setPageCount(book.getPageCount());

        return bookDto;
    }
}
