package br.com.erudio.services;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.dto.v1.BookDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import static br.com.erudio.mapper.ObjectMapper.parseObject;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Service;

@Service
public class BookServices {
    private final BookRepository repository;

    public BookServices(BookRepository repository) {
        this.repository = repository;
    }

    @Autowired
    PagedResourcesAssembler<BookDTO> assembler;

    public BookDTO findById(Long id) {
        var book = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found on data!"));


        var bookDto = parseObject(book, BookDTO.class);
        addHateoas(bookDto);

        return bookDto;
    }

    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {
        var books = repository.findAll(pageable);

        var booksWithLink = books.map(book -> {
            var dto = parseObject(book, BookDTO.class);
            addHateoas(dto);
            return dto;
        });

        Link findAllLink = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(BookController.class)
                    .findAll(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        String.valueOf(pageable.getSort())
                    )
        ).withSelfRel();

        return assembler.toModel(booksWithLink, findAllLink);
    }

    public BookDTO create(BookDTO bookDTO) {
        if(bookDTO == null) throw new RequiredObjectIsNullException();

        var book = parseObject(bookDTO, Book.class);

        var returnObject = parseObject(repository.save(book), BookDTO.class);

        addHateoas(returnObject);

        return returnObject;
    }

    public BookDTO update(BookDTO book) {
        if(book == null) throw new RequiredObjectIsNullException();

        var bookOnData = repository.findById(book.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found on data!"));

        bookOnData.setAuthor(book.getAuthor());
        bookOnData.setLaunchDate(book.getLaunchDate());
        bookOnData.setTitle(book.getTitle());
        bookOnData.setPrice(book.getPrice());

        var dto = parseObject(repository.save(bookOnData), BookDTO.class);
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
        book.add(linkTo(methodOn(BookController.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
        book.add(linkTo(methodOn(BookController.class).create(book)).withRel("create").withType("POST"));
        book.add(linkTo(methodOn(BookController.class).update(book)).withRel("update").withType("PUT"));
        book.add(linkTo(methodOn(BookController.class).delete(book.getId())).withRel("delete").withType("DELETE"));
    }
}
