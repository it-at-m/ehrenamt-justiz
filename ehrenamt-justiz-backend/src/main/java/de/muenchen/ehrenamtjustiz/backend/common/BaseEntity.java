package de.muenchen.ehrenamtjustiz.backend.common;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
public abstract class BaseEntity implements Cloneable, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "id")
    @Id
    // @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private UUID id;

}
