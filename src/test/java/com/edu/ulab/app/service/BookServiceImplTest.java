package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    BookMapper bookMapper;

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);

        //when

        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(userRepository.findById(bookDto.getUserId())).thenReturn(Optional.of(person));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);


        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        assertEquals(1L, bookDtoResult.getId());
    }


    // failed save
    @Test
    @DisplayName("Обновленипе книги c null. Должно выбросить ошибку")
    void saveBookWithNull_Test(){

        assertThatThrownBy(() -> bookService.updateBook(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Обновление книги.")
    void updateBook_Test() {
        // given
        Person person = new Person();
        person.setId(1L);

        BookDto bookDto = new BookDto();
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);


        Book book = new Book();
        book.setId(1L);
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        Book savedBook = new Book();
        savedBook.setId(1L);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);


        // when
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.findByIdForUpdate(book.getId())).thenReturn(Optional.of(book));
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        // then
        BookDto bookDtoResult = bookService.updateBook(bookDto);
        assertThat(bookDtoResult.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Получение книги.")
    void getBookById_Test() {
        // given
        Person person = new Person();
        person.setId(1L);

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        BookDto result = new BookDto();
        result.setId(1L);
        result.setUserId(1L);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        // when
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookMapper.bookToBookDto(book)).thenReturn(result);

        // then
        BookDto bookById = bookService.getBookById(1L);
        assertThat(bookById.getId()).isEqualTo(1L);
    }

    // get all
    @Test
    @DisplayName("Получение всех книг.")
    void getAllBooks_Test() {
        // given
        Person person = new Person();
        person.setId(1L);

        List<Book> books = new ArrayList<>();
        List<BookDto> booksDto = new ArrayList<>();

        Book book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setUserId(1L);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        books.add(book);
        booksDto.add(bookDto);

        // when
        when(bookRepository.findAll()).thenReturn(books);
        when(bookMapper.bookToBookDto(book)).thenReturn(bookDto);

        // then
        assertThat(bookService.getBooks().size()).isEqualTo(1);
    }

    // failed getAll
    @Test
    @DisplayName("Получение всех книги c ошибкой. Должно выбросить ошибку")
    void getBooksWithThrow_Test(){
        // when
        when(bookRepository.findAll()).thenThrow(NullPointerException.class);

        // then
        assertThatThrownBy(() -> bookService.getBooks())
                .isInstanceOf(NullPointerException.class);
    }

    // delete
    @Test
    @DisplayName("Удаление книги.")
    void deleteBook_Test() {
        // given

        // when
        bookService.deleteBookById(1L);


        // then
        verify(bookRepository).deleteById(1L);
    }
}
