package com.example.demo.service;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private GoogleBookService googleBookService;

    @InjectMocks
    private BookService bookService;

    @Test
    void addBookFromGoogle_validId_savesBook() {

        String googleId = "123";

        GoogleBook.VolumeInfo volumeInfo =
                new GoogleBook.VolumeInfo(
                        "Effective Java",
                        List.of("Joshua Bloch"),
                        "2018",
                        "Addison-Wesley",
                        400,
                        "BOOK",
                        "NOT_MATURE",
                        List.of("Programming"),
                        "en",
                        "previewLink",
                        "infoLink"
                );

        GoogleBook.Item item =
                new GoogleBook.Item(
                        googleId,
                        "selfLink",
                        volumeInfo,
                        null
                );

        when(googleBookService.getBookById(googleId)).thenReturn(item);

        when(bookRepository.save(any(Book.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Book result = bookService.addBookFromGoogle(googleId);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Effective Java");
        assertThat(result.getAuthor()).isEqualTo("Joshua Bloch");
        assertThat(result.getPageCount()).isEqualTo(400);

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void addBookFromGoogle_invalidId_throwsException() {

        String googleId = "invalid-id";

        when(googleBookService.getBookById(googleId)).thenReturn(null);

        assertThatThrownBy(() -> bookService.addBookFromGoogle(googleId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid Google Book ID");

        verify(bookRepository, never()).save(any());
    }
}