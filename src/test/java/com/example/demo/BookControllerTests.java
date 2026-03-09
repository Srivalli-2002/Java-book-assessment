package com.example.demo;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import com.example.demo.service.BookService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GoogleBookService googleBookService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @Test
    void getAllBooks_returnsBooks() {

        Book book = new Book();
        book.setId("1");
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");
        book.setPageCount(400);

        when(bookRepository.findAll()).thenReturn(List.of(book));

        List<Book> result = bookController.getAllBooks();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Effective Java");

        verify(bookRepository).findAll();
    }

    @Test
    void searchGoogleBooks_returnsGoogleBooks() {

        GoogleBook googleBook =
                new GoogleBook(
                        "books#volumes",
                        1,
                        List.of()
                );

        when(googleBookService.searchBooks("java", null, null))
                .thenReturn(googleBook);

        GoogleBook result = bookController.searchGoogleBooks("java", null, null);

        assertThat(result.totalItems()).isEqualTo(1);

        verify(googleBookService).searchBooks("java", null, null);
    }

    @Test
    void addBook_returnsCreatedBook() {

        Book book = new Book();
        book.setId("123");
        book.setTitle("Effective Java");
        book.setAuthor("Joshua Bloch");

        when(bookService.addBookFromGoogle("123")).thenReturn(book);

        ResponseEntity<Book> response = bookController.addBook("123");

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody().getTitle()).isEqualTo("Effective Java");

        verify(bookService).addBookFromGoogle("123");
    }
}