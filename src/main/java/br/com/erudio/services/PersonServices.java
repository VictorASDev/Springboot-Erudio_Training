package br.com.erudio.services;

import br.com.erudio.controllers.PersonController;
import br.com.erudio.data.dto.v1.PersonDTO;
import br.com.erudio.exception.BadRequestException;
import br.com.erudio.exception.FileStorageException;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.exception.ResourceNotFoundException;
import static br.com.erudio.mapper.ObjectMapper.parseObject;

import br.com.erudio.file.exporter.contract.PersonExporter;
import br.com.erudio.file.exporter.factory.FileExporterFactory;
import br.com.erudio.file.importer.contract.FileImporter;
import br.com.erudio.file.importer.factory.FileImporterFactory;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class PersonServices {
    private final Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PagedResourcesAssembler<PersonDTO> assembler;

    @Autowired
    private FileImporterFactory importer;

    @Autowired
    private FileExporterFactory exporter;

    public PagedModel<EntityModel<PersonDTO>>  findAll(Pageable pageable) {
        logger.info("Returning all entities on data base");

        var people = repository.findAll(pageable);

        return buildPagedModel(pageable, people);
    }

    public Resource exportPage(Pageable pageable, String acceptHeader) {
        logger.info("Exporting a People page!");

        var people = repository.findAll(pageable)
                .map(person -> parseObject(person, PersonDTO.class))
                .getContent();

        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);
            return exporter.exportPeople(people);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export!",e);
        }
    }

    public Resource exportPerson(Long id, String acceptHeader) {
        logger.info("Exporting data of one person");

        var person = repository.findById(id)
                .map(entity -> parseObject(entity, PersonDTO.class))
                .orElseThrow(() -> new EntityNotFoundException("Entity not found!"));

        try {
            PersonExporter exporter = this.exporter.getExporter(acceptHeader);

            return exporter.exportPerson(person);
        } catch (Exception e) {
            throw new RuntimeException("Error during file export!",e);
        }
    }

    public PersonDTO findById(Long id) {
        logger.info("Returning one entity on data base");

        var person = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found!"));

        var personDto = parseObject(person, PersonDTO.class);

        addHateoasLinks(personDto);

        return personDto;
    }

    public PersonDTO update(PersonDTO person) {
        logger.info("Updating one entity on data base");

        if(person == null) throw new RequiredObjectIsNullException();

        Person personOnData = repository.findById(person.getId())
               .orElseThrow(()
                       -> new ResourceNotFoundException("User with id " + person.getId() + "was´nt t found!"));

        personOnData.setFirstName(person.getFirstName());
        personOnData.setLastName(person.getLastName());
        personOnData.setAddress(person.getAddress());
        personOnData.setGender(person.getGender());


        var dto = parseObject(repository.save(personOnData), PersonDTO.class);
        addHateoasLinks(person);
        return dto;
    }

    public void delete(Long id) {
        logger.info("Deleting one entity on data base");

         Person entity = repository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

         repository.delete(entity);
    }

    public PagedModel<EntityModel<PersonDTO>> findByName(String firstName, Pageable pageable) {
        logger.info("Returning all entities on data base by name");

        var people = repository.findPeopleByName(firstName, pageable);

        return buildPagedModel(pageable, people);
    }

    @Transactional
    public PersonDTO disablePerson(Long id) {
        logger.info("Disabling one Person");

        repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.disablePerson(id);

        var entity = repository.findById(id).get();

        var dto = parseObject(entity, PersonDTO.class);
        addHateoasLinks(dto);

        return dto;
    }


    public PersonDTO create(PersonDTO person) {
        logger.info("Creating one entity on data base");

        if(person == null) throw new RequiredObjectIsNullException();

        var convertedPerson = parseObject(person, Person.class);

        var dto = parseObject(repository.save(convertedPerson), PersonDTO.class);
        addHateoasLinks(dto);

        return dto;
    }

    public List<PersonDTO>  massCreation(MultipartFile file)  {
        logger.info("Importing people from file!");

        if (file.isEmpty()) throw new BadRequestException("Invalid file!");

        try (InputStream inputStream = file.getInputStream()) {
            String fileName = Optional.ofNullable(file.getOriginalFilename())
                    .orElseThrow(() -> new BadRequestException("Invalid file name!"));

            FileImporter importer = this.importer.getImporter(fileName);

            List<Person> entities = importer.importFile(inputStream).stream()
                    .map(dto -> repository.save(parseObject(dto, Person.class)))
                    .toList();

            return entities.stream().map(entity -> {
                var dto = parseObject(repository.save(entity), PersonDTO.class);
                addHateoasLinks(dto);
                return dto;
            }).toList();
        } catch (Exception e) {
            throw new FileStorageException("Error processing the file!");
        }
    }

    private PagedModel<EntityModel<PersonDTO>> buildPagedModel(Pageable pageable, Page<Person> people) {
        var peopleWithLinks = people.map(person -> {
            var dto = parseObject(person, PersonDTO.class);
            addHateoasLinks(dto);
            return dto;
        });

        Link findAllLink = linkTo(
                methodOn(PersonController.class)
                        .findAll(
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                String.valueOf(pageable.getSort())
                        )
        ).withSelfRel();

        return assembler.toModel(peopleWithLinks, findAllLink);
    }

    private static void addHateoasLinks(PersonDTO personDto) {
        personDto.add(linkTo(methodOn(PersonController.class).findById(personDto.getId())).withSelfRel().withType("GET"));
        personDto.add(linkTo(methodOn(PersonController.class).findAll(1, 12, "asc")).withRel("findAll").withType("GET"));
        personDto.add(linkTo(methodOn(PersonController.class).findByName("",1, 12, "asc")).withRel("findByName").withType("GET"));
        personDto.add(linkTo(methodOn(PersonController.class).create(personDto)).withRel("create").withType("POST"));
        personDto.add(linkTo(methodOn(PersonController.class).update(personDto)).withRel("update").withType("PUT"));
        personDto.add(linkTo(methodOn(PersonController.class).disablePerson(personDto.getId())).withRel("disable").withType("PATCH"));
        personDto.add(linkTo(methodOn(PersonController.class).delete(personDto.getId())).withRel("delete").withType("DELETE"));
        personDto.add(linkTo(methodOn(PersonController.class).exportPage(
                1, 12, "asc", null))
                .withRel("exportPage")
                .withType("GET")
                .withTitle("Export People"));
    }

}
