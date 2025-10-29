package br.com.erudio.services;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.dto.v1.BookDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.mapper.ObjectMapper;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookServices {
    private final BookRepository repository;

    public BookServices(BookRepository repository) {
        this.repository = repository;
    }

    public BookDTO findById(Long id) {
        var book = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found on data!"));


        var bookDto = ObjectMapper.parseObject(book, BookDTO.class);
        addHateoas(bookDto);

        return bookDto;
    }

    public List<BookDTO> findAll() {
        var books = ObjectMapper.parseListObjects(repository.findAll(), BookDTO.class);

        books.forEach(BookServices::addHateoas);

        return books;
    }

    public BookDTO create(BookDTO bookDTO) {
        if(bookDTO == null) throw new RequiredObjectIsNullException();

        var book = ObjectMapper.parseObject(bookDTO, Book.class);

        var returnObject = ObjectMapper.parseObject(repository.save(book), BookDTO.class);

        addHateoas(returnObject);

        return returnObject;
    }

    public BookDTO update(BookDTO book) {
        var bookOnData = repository.findById(book.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found on data!"));

        bookOnData.setAuthor(book.getAuthor());
        //bookOnData.setLaunchDate(book.getLaunchDate());
        bookOnData.setTitle(book.getTitle());
        bookOnData.setPrice(book.getPrice());

        var dto = ObjectMapper.parseObject(repository.save(bookOnData), BookDTO.class);
        addHateoas(dto);

        return dto;
    }

    public void delete(Long id) {
        var book = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found on data!"));

        repository.delete(book);
    }

    private static void addHateoas(BookDTO book) {
        book.add(linkTo(methodOn(BookController.class).findById(book.getId())).withSelfRel().withType("GET"));
        book.add(linkTo(methodOn(BookController.class).findAll()).withRel("findAll").withType("GET"));
        book.add(linkTo(methodOn(BookController.class).create(book)).withRel("create").withType("POST"));
        book.add(linkTo(methodOn(BookController.class).update(book)).withRel("update").withType("PUT"));
        book.add(linkTo(methodOn(BookController.class).delete(book.getId())).withRel("delete").withType("DELETE"));
    }
}
