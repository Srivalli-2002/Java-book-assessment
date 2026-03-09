package com.example.demo.google;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import java.net.URI;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleBookServiceTests {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private GoogleBookService googleBookService;

    @BeforeEach
    void setup() {

        googleBookService = new GoogleBookService("http://test-url");

        // inject mock restClient using reflection
        try {
            var field = GoogleBookService.class.getDeclaredField("restClient");
            field.setAccessible(true);
            field.set(googleBookService, restClient);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void searchBooks_returnsGoogleBooks() {

        GoogleBook response = new GoogleBook("books", 1, null);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.util.function.Function.class)))
                .thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(GoogleBook.class)).thenReturn(response);

        GoogleBook result = googleBookService.searchBooks("java", 5, 0);

        assertThat(result).isNotNull();
        assertThat(result.totalItems()).isEqualTo(1);

        verify(restClient).get();
    }

    @Test
    void getBookById_returnsBookItem() {

        GoogleBook.Item item = mock(GoogleBook.Item.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri("/volumes/{id}", "123")).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(GoogleBook.Item.class)).thenReturn(item);

        GoogleBook.Item result = googleBookService.getBookById("123");

        assertThat(result).isNotNull();

        verify(restClient).get();
    }
}