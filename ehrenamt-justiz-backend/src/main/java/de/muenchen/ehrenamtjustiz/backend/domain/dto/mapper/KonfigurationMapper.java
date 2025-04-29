package de.muenchen.ehrenamtjustiz.backend.domain.dto.mapper;

import de.muenchen.ehrenamtjustiz.backend.configuration.MapstructConfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.domain.dto.KonfigurationDto;
import org.mapstruct.Mapper;

@Mapper(config = MapstructConfiguration.class)
public interface KonfigurationMapper {
    KonfigurationDto entity2Model(Konfiguration entity);

    Konfiguration model2Entity(KonfigurationDto entity);
}
