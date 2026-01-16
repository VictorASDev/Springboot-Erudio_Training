package br.com.erudio.services;

import br.com.erudio.data.dto.v1.PersonDTO;
import br.com.erudio.data.dto.v2.PersonDTOV2;
import br.com.erudio.exception.ResourceNotFoundException;
import static br.com.erudio.mapper.ObjectMapper.parseListObjects;
import static br.com.erudio.mapper.ObjectMapper.parseObject;

import br.com.erudio.mapper.custom.PersonMapper;
import br.com.erudio.model.Person;
import br.com.erudio.repository.personRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class PersonServices {
    private final Logger logger = Logger.getLogger(PersonServices.class.getName());

    @Autowired
    private personRepository repository;

    @Autowired
    private PersonMapper mapper;

    public List<PersonDTO> findAll() {
        logger.info("Returning all entities on data base");

        return parseListObjects(repository.findAll(), PersonDTO.class);
    }

    public PersonDTO findById(Long id) {
        logger.info("Returning one entity on data base");

        var person = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found!"));

        var personDto = parseObject(person, PersonDTO.class);

        personDto.setPhoneNumber("(+55) (34) 9876-54321");
        personDto.setBirthday(new Date());
        personDto.setSalary("15.000");
        personDto.setPassword("123456789");

        return personDto;
    }

    public PersonDTO update(PersonDTO person) {
        logger.info("Updating one entity on data base");

        Person personOnData = repository.findById(person.getId())
               .orElseThrow(()
                       -> new ResourceNotFoundException("User with id " + person.getId() + "was´nt t found!"));

        personOnData.setFirstName(person.getFirstName());
        personOnData.setLastName(person.getLastName());
        personOnData.setAddress(person.getAddress());
        personOnData.setGender(person.getGender());

       return parseObject(repository.save(personOnData), PersonDTO.class);

    }

    public void delete(Long id) {
        logger.info("Deleting one entity on data base");

         Person entity = repository.findById(id)
                 .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

         repository.delete(entity);
    }


    public PersonDTO create(PersonDTO person) {
        logger.info("Creating one entity on data base");

        var convertedPerson = parseObject(person, Person.class);

        return parseObject(repository.save(convertedPerson), PersonDTO.class);
    }

    public PersonDTOV2 create(PersonDTOV2 person) {
        logger.info("Creating one entity on data base");

        var convertedPerson = mapper.convertDtoToEntity(person);

        return mapper.convertEntityToDto(repository.save(convertedPerson));
    }
}
