package de.muenchen.ehrenamtjustiz.backend;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.io.Serial;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@SuppressWarnings("PMD.UnusedFormalParameter")
public class RestPageImpl<T> extends PageImpl<T> {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPageImpl(@JsonProperty("content") final List<T> content,
            @JsonProperty("number") final int number,
            @JsonProperty("size") final int size,
            @JsonProperty("totalElements") final Long totalElements,
            @JsonProperty("pageable") final JsonNode pageable,
            @JsonProperty("last") final boolean last,
            @JsonProperty("totalPages") final int totalPages,
            @JsonProperty("sort") final JsonNode sort,
            @JsonProperty("numberOfElements") final int numberOfElements) {
        super(content, PageRequest.of(number, numberOfElements), totalElements);
    }

}
