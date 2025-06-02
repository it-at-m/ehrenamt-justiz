package de.muenchen.ehrenamtjustiz.backend;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serial;
import java.util.List;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@JsonIgnoreProperties(ignoreUnknown = true, value = { "pageable" })
@SuppressWarnings("PMD.UnusedFormalParameter")
public class RestPageImpl<T> extends PageImpl<T> {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public RestPageImpl(@JsonProperty("content") final List<T> content,
            @JsonProperty("number") final int number,
            @JsonProperty("totalElements") final Long totalElements,
            @JsonProperty("numberOfElements") final int numberOfElements) {
        super(content, PageRequest.of(number, numberOfElements), totalElements);
    }

}
