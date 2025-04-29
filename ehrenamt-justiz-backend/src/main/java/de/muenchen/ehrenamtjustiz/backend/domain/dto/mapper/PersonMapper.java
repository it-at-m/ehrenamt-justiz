package de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper;

import de.muenchen.ehrenamtjustiz.backend.configuration.MapstructConfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.PersonDto;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfiguration.class)
public interface PersonMapper {
    PersonDto entity2Model(Person entity);

    Person model2Entity(PersonDto entity);
}
