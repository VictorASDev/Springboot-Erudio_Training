package br.com.erudio.services;

import br.com.erudio.data.dto.v1.BookDTO;
import br.com.erudio.model.Book;
import br.com.erudio.model.Person;
import br.com.erudio.repository.BookRepository;
import br.com.erudio.unitetests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class BookServicesTest {

    @Mock
    private BookRepository repository;

    @InjectMocks
    private BookServices service;

    private MockBook input;

    @BeforeEach
    void setUp() {
        input = new MockBook();
    }

    @Test
    void findById() {
        var book = input.mockBook(1);
        book.setId(1L);
        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);

        when(repository.findById(1L)).thenReturn(Optional.of(book));

        // Act
        var result = service.findById(1L);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        // Verifica link "self"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/books/v1/1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "findAll"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "create"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("POST"))
        );

        // Verifica link "update"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("PUT"))
        );

        // Verifica link "delete"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/books/v1/1")
                        && link.getType().equals("DELETE"))
        );

        // Verifica os dados retornados
        assertEquals("Book 1", result.getTitle());
        assertEquals("Author 1", result.getAuthor());
        assertEquals(1.0, result.getPrice()); // se price for double
        assertEquals(1L, result.getId());
        assertEquals(fixedDate, result.getLaunchDate());
    }

    @Test
    void findAll() {
        var list = input.mockBookList();

        when(repository.findAll()).thenReturn(list);

        var bookList = service.findAll();

        assertNotNull(bookList);
        assertEquals(14, bookList.size());

        // Act
        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);
        var bookOne = bookList.get(1);

        // Assert
        assertNotNull(bookOne);
        assertNotNull(bookOne.getId());
        assertNotNull(bookOne.getLinks());

        // Verifica link "self"
        assertTrue(bookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/books/v1/1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "findAll"
        assertTrue(bookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "create"
        assertTrue(bookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("POST"))
        );

        // Verifica link "update"
        assertTrue(bookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("PUT"))
        );

        // Verifica link "delete"
        assertTrue(bookOne.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/books/v1/1")
                        && link.getType().equals("DELETE"))
        );

        // Verifica os dados retornados
        assertEquals("Book 1", bookOne.getTitle());
        assertEquals("Author 1", bookOne.getAuthor());
        assertEquals(1.0, bookOne.getPrice()); // se price for double
        assertEquals(1L, bookOne.getId());
        assertEquals(fixedDate, bookOne.getLaunchDate());

        var bookFive = bookList.get(5);

        // Assert
        assertNotNull(bookFive);
        assertNotNull(bookFive.getId());
        assertNotNull(bookFive.getLinks());

        // Verifica link "self"
        assertTrue(bookFive.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/books/v1/5")
                        && link.getType().equals("GET"))
        );

        // Verifica link "findAll"
        assertTrue(bookFive.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "create"
        assertTrue(bookFive.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("POST"))
        );

        // Verifica link "update"
        assertTrue(bookFive.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("PUT"))
        );

        // Verifica link "delete"
        assertTrue(bookFive.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/books/v1/5")
                        && link.getType().equals("DELETE"))
        );

        // Verifica os dados retornados
        assertEquals("Book 5", bookFive.getTitle());
        assertEquals("Author 5", bookFive.getAuthor());
        assertEquals(5.0, bookFive.getPrice()); // se price for double
        assertEquals(5L, bookFive.getId());
        assertEquals(fixedDate, bookFive.getLaunchDate());

        var bookTwelve = bookList.get(12);

        // Assert
        assertNotNull(bookTwelve);
        assertNotNull(bookTwelve.getId());
        assertNotNull(bookTwelve.getLinks());

        // Verifica link "self"
        assertTrue(bookTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/books/v1/12")
                        && link.getType().equals("GET"))
        );

        // Verifica link "findAll"
        assertTrue(bookTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "create"
        assertTrue(bookTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("POST"))
        );

        // Verifica link "update"
        assertTrue(bookTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("PUT"))
        );

        // Verifica link "delete"
        assertTrue(bookTwelve.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/books/v1/12")
                        && link.getType().equals("DELETE"))
        );

        // Verifica os dados retornados
        assertEquals("Book 12", bookTwelve.getTitle());
        assertEquals("Author 12", bookTwelve.getAuthor());
        assertEquals(12.0, bookTwelve.getPrice()); // se price for double
        assertEquals(12L, bookTwelve.getId());
        assertEquals(fixedDate, bookTwelve.getLaunchDate());
    }

    @Test
    void create() {
        var book = input.mockBook();
        var persistedBook = book;

        persistedBook.setId(1L);

        BookDTO dto = input.mockBookDto(1);

        when(repository.save(book)).thenReturn(persistedBook);

        var result = service.create(dto);

        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/books/v1/1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "findAll"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "create"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("POST"))
        );

        // Verifica link "update"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("PUT"))
        );

        // Verifica link "delete"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/books/v1/1")
                        && link.getType().equals("DELETE"))
        );

        // Verifica os dados retornados
        assertEquals("Book 1", result.getTitle());
        assertEquals("Author 1", result.getAuthor());
        assertEquals(1.0, result.getPrice()); // se price for double
        assertEquals(1L, result.getId());
        assertEquals(fixedDate, result.getLaunchDate());
    }

    @Test
    void update() {
        var book = input.mockBook();
        var persistedBook = book;
        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);
        persistedBook.setId(1L);

        BookDTO dto = input.mockBookDto(1);

        when(repository.findById(1L)).thenReturn(Optional.of(book));
        when(repository.save(book)).thenReturn(persistedBook);

        var result = service.update(dto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getLinks());

        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("self")
                        && link.getHref().endsWith("/api/books/v1/1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "findAll"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("findAll")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("GET"))
        );

        // Verifica link "create"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("create")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("POST"))
        );

        // Verifica link "update"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("update")
                        && link.getHref().endsWith("/api/books/v1")
                        && link.getType().equals("PUT"))
        );

        // Verifica link "delete"
        assertTrue(result.getLinks().stream()
                .anyMatch(link -> link.getRel().value().equals("delete")
                        && link.getHref().endsWith("/api/books/v1/1")
                        && link.getType().equals("DELETE"))
        );

        // Verifica os dados retornados
        assertEquals("Book 1", result.getTitle());
        assertEquals("Author 1", result.getAuthor());
        assertEquals(1.0, result.getPrice()); // se price for double
        assertEquals(1L, result.getId());
        assertEquals(fixedDate, result.getLaunchDate());
    }

    @Test
    void delete() {
        var book = input.mockBook(1);
        book.setId(1L);
        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);

        when(repository.findById(1L)).thenReturn(Optional.of(book));

        service.delete(1L);

        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);
    }
}