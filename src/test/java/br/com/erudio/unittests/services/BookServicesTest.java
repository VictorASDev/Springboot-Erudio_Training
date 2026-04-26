package br.com.erudio.unittests.services;

import br.com.erudio.data.dto.v1.BookDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;
import br.com.erudio.services.BookServices;
import br.com.erudio.unittests.mapper.mocks.MockBook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Mock
    private PagedResourcesAssembler<BookDTO> assembler;

    @InjectMocks
    private BookServices service;

    private MockBook input;

    @BeforeEach
    void setUp() {
        input = new MockBook();
        MockitoAnnotations.openMocks(this);
        this.service = new BookServices(repository);
        ReflectionTestUtils.setField(this.service, "assembler", assembler);
    }

    @Test
    void findById() {
        var book = input.mockBook(1);
        book.setId(1L);
        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);

        when(repository.findById(anyLong())).thenReturn(Optional.of(book));

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
                        && link.getHref().endsWith("/api/books/v1?page=0&size=12&direction=asc")
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
        List<Book> list = input.mockBookList();
        Pageable pageable = PageRequest.of(0, 12);

        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(list, pageable, list.size()));

        ArgumentCaptor<Page<BookDTO>> pageCaptor = ArgumentCaptor.forClass(Page.class);
        when(assembler.toModel(pageCaptor.capture(), any(Link.class)))
                .thenReturn(PagedModel.of(List.of(), new PagedModel.PageMetadata(0, 0, 0)));

        service.findAll(pageable);

        var captured = pageCaptor.getValue();
        assertNotNull(captured);

        List<BookDTO> bookList = new ArrayList<>(captured.getContent());
        assertNotNull(bookList);

        assertEquals(14, bookList.size());

        // Act
        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);
        BookDTO bookOne = bookList.get(1);

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
                        && link.getHref().endsWith("/api/books/v1?page=0&size=12&direction=asc")
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
                        && link.getHref().endsWith("/api/books/v1?page=0&size=12&direction=asc")
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
                        && link.getHref().endsWith("/api/books/v1?page=0&size=12&direction=asc")
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

        verify(repository, times(1)).findAll(pageable);
        verify(assembler, times(1)).toModel(any(Page.class), any(Link.class));
    }

    @Test
    void create() {
        var persistedBook = input.mockBook(1);
        persistedBook.setId(1L);

        BookDTO dto = input.mockBookDto(1);

        // Use argument matcher for save
        when(repository.save(any(Book.class))).thenReturn(persistedBook);

        var result = service.create(dto);

        // Optionally assert
        assertEquals(1L, result.getId());


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
                        && link.getHref().endsWith("/api/books/v1?page=0&size=12&direction=asc")
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
        var book = input.mockBook(1);
        var persistedBook = book;
        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);
        persistedBook.setId(1L);

        BookDTO dto = input.mockBookDto(1);

        when(repository.findById(anyLong())).thenReturn(Optional.of(book));
        when(repository.save(any(Book.class))).thenReturn(persistedBook);

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
                        && link.getHref().endsWith("/api/books/v1?page=0&size=12&direction=asc")
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

        when(repository.findById(anyLong())).thenReturn(Optional.of(book));

        service.delete(1L);

        verify(repository, times(1)).findById(anyLong());
        verify(repository, times(1)).delete(any(Book.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    void testCreateWithNull() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.create(null);
                });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testUpdateWithNull() {
        Exception exception = assertThrows(RequiredObjectIsNullException.class,
                () -> {
                    service.update(null);
                });

        String expectedMessage = "It is not allowed to persist a null object!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}