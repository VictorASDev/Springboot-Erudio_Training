package br.com.erudio.controllers;

import br.com.erudio.data.dto.v1.BookDTO;
import br.com.erudio.services.BookServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
@Tag(name = "Books", description = "Endpoint for Managing Books")
public class BookController implements br.com.erudio.controllers.docs.BookControllerDocs {
    private final BookServices services;

    public BookController(BookServices services) {
        this.services = services;
    }

    @GetMapping(
            value = "v1/{id}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public ResponseEntity<BookDTO> findById(@PathVariable("id") Long id) {
        var res = services.findById(id);

        return ResponseEntity.ok(res);
    }

    @GetMapping(
            value = "/v1",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public ResponseEntity<PagedModel<EntityModel<BookDTO>>> findAll (
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "12") Integer size,
            @RequestParam(value = "direction", defaultValue = "asc") String direction
    ) {
        var sortDirection = "desc".equalsIgnoreCase(direction) ? Direction.DESC : Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, "title"));

        return ResponseEntity.ok(services.findAll(pageable));
    }

    @PostMapping(
            value = "/v1",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public ResponseEntity<BookDTO> create(@RequestBody BookDTO book) {
        var res = services.create(book);

        return ResponseEntity.ok(res);
    }

    @PutMapping(
            value = "/v1",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            },
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,
                    MediaType.APPLICATION_YAML_VALUE
            }
    )
    @Override
    public ResponseEntity<BookDTO> update(@RequestBody BookDTO book) {
        var res = services.update(book);

        return ResponseEntity.ok(res);
    }

    @DeleteMapping(value = "v1/{id}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        services.delete(id);

        return ResponseEntity.noContent().build();
    }


}
