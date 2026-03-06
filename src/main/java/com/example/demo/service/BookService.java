package com.example.demo.service;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;

    public BookService(BookRepository bookRepository, GoogleBookService googleBookService) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
    }

    public Book addBookFromGoogle(String googleId) {

        // Fetch book details from Google API
        GoogleBook.Item item = googleBookService.getBookById(googleId);

        // Validate response
        if (item == null || item.volumeInfo() == null) {
            throw new IllegalArgumentException("Invalid Google Book ID");
        }

        GoogleBook.VolumeInfo volumeInfo = item.volumeInfo();

        // Extract fields
        String title = volumeInfo.title();

        String author = null;
        if (volumeInfo.authors() != null && !volumeInfo.authors().isEmpty()) {
            author = volumeInfo.authors().get(0);
        }

        Integer pageCount = volumeInfo.pageCount();

        // Map Google response → Book entity
        Book book = new Book();
        book.setId(item.id());
        book.setTitle(title);
        book.setAuthor(author);
        book.setPageCount(pageCount);

        // Save to DB
        return bookRepository.save(book);
    }
}