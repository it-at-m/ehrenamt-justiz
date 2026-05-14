package de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper;

import de.muenchen.ehrenamtjustiz.backend.configuration.MapstructConfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Document;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.DocumentDto;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfiguration.class)
public interface DocumentMapper {
    DocumentDto entity2Model(Document entity);

}
