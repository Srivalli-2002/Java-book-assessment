package com.example.demo.service;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class BookServiceTests {

    @Autowired
    private BookService bookService;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void addBookFromGoogle_validId_savesBook() {

        String googleId = "12muzgEACAAJ";

        Book savedBook = bookService.addBookFromGoogle(googleId);

        assertThat(savedBook).isNotNull();
        assertThat(savedBook.getTitle()).isEqualTo("Effective Java");
        assertThat(savedBook.getAuthor()).isNotNull();

        // Verify saved in DB
        assertThat(bookRepository.findAll())
                .extracting(Book::getTitle)
                .contains("Effective Java");
    }

    @Test
    void addBookFromGoogle_invalidId_throwsException() {

        String invalidId = "invalid-id";

        assertThatThrownBy(() -> bookService.addBookFromGoogle(invalidId))
                .isInstanceOf(org.springframework.web.client.HttpServerErrorException.class);
    }
}