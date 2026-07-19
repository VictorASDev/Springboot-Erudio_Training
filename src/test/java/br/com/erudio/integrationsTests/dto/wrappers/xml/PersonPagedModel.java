package br.com.erudio.integrationsTests.dto.wrappers.xml;

import br.com.erudio.integrationsTests.dto.PersonDTO;
import jakarta.xml.bind.annotation.XmlElement;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PersonPagedModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "content")
    public List<PersonDTO> content;

    public PersonPagedModel() {}

    public List<PersonDTO> getContent() {
        return content;
    }

    public void setContent(List<PersonDTO> content) {
        this.content = content;
    }
}
