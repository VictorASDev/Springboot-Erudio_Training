package br.com.erudio.unittests.mapper.mocks;

import br.com.erudio.data.dto.v1.BookDTO;
import br.com.erudio.model.Book;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MockBook {

    public Book mockBook() {
        return mockBook(0);
    }

    public BookDTO mocBookDto() {
        return mockBookDto(0);
    }

    public List<Book> mockBookList() {
        List<Book> list = new ArrayList<Book>();

        for(int i = 0; i < 14; i++) {
            list.add(mockBook(i));
        }

        return list;
    }

    public List<BookDTO> mockDtoList() {
        List<BookDTO> list = new ArrayList<BookDTO>();

        for(int i = 0; i < 14; i++) {
            list.add(mockBookDto(i));
        }

        return list;
    }

    public Book mockBook(Integer id) {
        Book book = new Book();

        book.setId(id.longValue());
        book.setPrice(id.doubleValue());
        book.setTitle("Book " + id);
        book.setAuthor("Author " + id);

        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);
        book.setLaunchDate(fixedDate);

        return book;
    }

    public BookDTO mockBookDto(Integer id) {
        BookDTO book = new BookDTO();

        book.setId(id.longValue());
        book.setPrice(id.doubleValue());
        book.setTitle("Book " + id);
        book.setAuthor("Author " + id);

        LocalDateTime fixedDate = LocalDateTime.of(2025, 10, 23, 10, 0, 0);
        book.setLaunchDate(fixedDate);

        return book;
    }
}
