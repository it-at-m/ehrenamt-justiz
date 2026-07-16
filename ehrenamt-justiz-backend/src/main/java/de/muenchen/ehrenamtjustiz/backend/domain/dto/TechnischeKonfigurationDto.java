package de.muenchen.ehrenamtjustiz.backend.domain.dto;

import java.math.BigInteger;
import lombok.Data;

@Data
public class TechnischeKonfigurationDto {

    private BigInteger bestaetigungVerfassungstreueMaxSize;

    private String bestaetigungVerfassungstreueFileExtension;

}
