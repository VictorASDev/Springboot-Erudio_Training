package br.com.erudio.services;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.dto.v1.BookDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.model.Book;
import br.com.erudio.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class BookServices {
    private final BookRepository repository;

    @Autowired
    PagedResourcesAssembler<BookDTO> assembler;

    public BookServices(BookRepository repository) {
        this.repository = repository;
    }

    public BookDTO findById(Long id) {
        var book = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found on data!"));

        var bookDto = convertToDTO(book);
        addHateoas(bookDto);

        return bookDto;
    }

    public PagedModel<EntityModel<BookDTO>> findAll(Pageable pageable) {
        var books = repository.findAll(pageable);

        var booksWithLink = books.map(book -> {
            var dto = convertToDTO(book);
            addHateoas(dto);
            return dto;
        });

        Link findAllLink = linkTo(
                methodOn(BookController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                pageable.getSort().toString()
                        )
        ).withSelfRel();

        return assembler.toModel(booksWithLink, findAllLink);
    }

    public BookDTO create(BookDTO bookDTO) {
        if (bookDTO == null) {
            throw new RequiredObjectIsNullException("It is not allowed to persist a null object!");
        }

        var book = convertToEntity(bookDTO);
        var savedBook = repository.save(book);

        var returnObject = convertToDTO(savedBook);
        addHateoas(returnObject);

        return returnObject;
    }

    public BookDTO update(BookDTO bookDTO) {
        if (bookDTO == null) {
            throw new RequiredObjectIsNullException("It is not allowed to persist a null object!");
        }

        var bookOnData = repository.findById(bookDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found on data!"));

        bookOnData.setAuthor(bookDTO.getAuthor());
        bookOnData.setLaunchDate(bookDTO.getLaunchDate());
        bookOnData.setTitle(bookDTO.getTitle());
        bookOnData.setPrice(bookDTO.getPrice());

        var savedBook = repository.save(bookOnData);

        var dto = convertToDTO(savedBook);
        addHateoas(dto);

        return dto;
    }

    public void delete(Long id) {
        var book = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book not found on data!"));
        repository.delete(book);
    }

    // 🔥 MÉTODOS DE MAPEAMENTO MANUAL
    private BookDTO convertToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setAuthor(book.getAuthor());
        dto.setTitle(book.getTitle());
        dto.setLaunchDate(book.getLaunchDate());
        dto.setPrice(book.getPrice());
        return dto;
    }

    private Book convertToEntity(BookDTO dto) {
        Book book = new Book();
        book.setId(dto.getId());
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book.setLaunchDate(dto.getLaunchDate());
        book.setPrice(dto.getPrice());
        return book;
    }

    private static void addHateoas(BookDTO book) {
        try {
            book.add(linkTo(methodOn(BookController.class).findById(book.getId())).withSelfRel().withType("GET"));
            book.add(linkTo(methodOn(BookController.class).findAll(0, 12, "asc")).withRel("findAll").withType("GET"));
            book.add(linkTo(methodOn(BookController.class).create(book)).withRel("create").withType("POST"));
            book.add(linkTo(methodOn(BookController.class).update(book)).withRel("update").withType("PUT"));
            book.add(linkTo(methodOn(BookController.class).delete(book.getId())).withRel("delete").withType("DELETE"));
        } catch (Exception e) {
        }
    }
}