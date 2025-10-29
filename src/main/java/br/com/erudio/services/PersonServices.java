package br.com.erudio.services;

import br.com.erudio.controllers.PersonController;
import br.com.erudio.data.dto.v1.PersonDTO;
import br.com.erudio.exception.RequiredObjectIsNullException;
import br.com.erudio.exception.ResourceNotFoundException;
import static br.com.erudio.mapper.ObjectMapper.parseListObjects;
import static br.com.erudio.mapper.ObjectMapper.parseObject;

import br.com.erudio.mapper.custom.PersonMapper;
import br.com.erudio.model.Person;
import br.com.erudio.repository.PersonRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonServices {
    private final Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    private PersonRepository repository;

    @Autowired
    private PersonMapper mapper;

    public List<PersonDTO> findAll() {
        logger.info("Returning all entities on data base");

        var persons = parseListObjects(repository.findAll(), PersonDTO.class);

        persons.forEach(PersonServices::addHateoasLinks);

        return persons;
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


    public PersonDTO create(PersonDTO person) {
        logger.info("Creating one entity on data base");

        if(person == null) throw new RequiredObjectIsNullException();

        var convertedPerson = parseObject(person, Person.class);

        var dto = parseObject(repository.save(convertedPerson), PersonDTO.class);
        addHateoasLinks(dto);

        return dto;
    }

    private static void addHateoasLinks(PersonDTO personDto) {
        personDto.add(linkTo(methodOn(PersonController.class).findById(personDto.getId())).withSelfRel().withType("GET"));
        personDto.add(linkTo(methodOn(PersonController.class).findAll()).withRel("findAll").withType("GET"));
        personDto.add(linkTo(methodOn(PersonController.class).create(personDto)).withRel("create").withType("POST"));
        personDto.add(linkTo(methodOn(PersonController.class).update(personDto)).withRel("update").withType("PUT"));
        personDto.add(linkTo(methodOn(PersonController.class).delete(personDto.getId())).withRel("delete").withType("DELETE"));
    }

}
