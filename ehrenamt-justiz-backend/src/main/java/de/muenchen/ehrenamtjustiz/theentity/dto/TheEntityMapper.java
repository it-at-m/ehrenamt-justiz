package de.muenchen.ehrenamtjustiz.theentity.dto;

import de.muenchen.ehrenamtjustiz.theentity.TheEntity;
import org.mapstruct.Mapper;

@Mapper
public interface TheEntityMapper {

    TheEntityResponseDTO toDTO(TheEntity theEntity);

    TheEntity toEntity(TheEntityRequestDTO theEntityRequestDTO);
}
