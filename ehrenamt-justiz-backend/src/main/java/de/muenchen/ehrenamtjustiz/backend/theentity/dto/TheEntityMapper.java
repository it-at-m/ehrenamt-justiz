package de.muenchen.ehrenamtjustiz.backend.theentity.dto;

import de.muenchen.ehrenamtjustiz.backend.theentity.TheEntity;
import org.mapstruct.Mapper;

@Mapper
public interface TheEntityMapper {

    TheEntityResponseDTO toDTO(TheEntity theEntity);

    TheEntity toEntity(TheEntityRequestDTO theEntityRequestDTO);
}
