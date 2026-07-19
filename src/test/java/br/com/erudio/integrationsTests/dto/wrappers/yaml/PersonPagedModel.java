package br.com.erudio.integrationsTests.dto.wrappers.yaml;

import br.com.erudio.integrationsTests.dto.PersonDTO;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

public class PersonPagedModel implements Serializable {

    @JsonProperty("content")
    private List<PersonDTO> content;

    public List<PersonDTO> getContent() {
        return content;
    }

    public void setContent(List<PersonDTO> content) {
        this.content = content;
    }
}