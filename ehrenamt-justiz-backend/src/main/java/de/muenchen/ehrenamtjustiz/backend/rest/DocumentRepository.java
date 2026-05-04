package de.muenchen.ehrenamtjustiz.backend.rest;

import de.muenchen.ehrenamtjustiz.backend.domain.Document;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource(collectionResourceRel = "document", path = "document")
@PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
public interface DocumentRepository extends CrudRepository<Document, UUID> {
    @Override
    @PreAuthorize("permitAll")
    @Transactional
    <S extends Document> S save(S document);

    @Query(
        "SELECT d FROM Document d " +
                "where d.personid=?1"
    )
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    Document[] getDocumentByPersonId(UUID person);

    @Modifying
    @Query("delete from Document d where d.personid = :personId")
    @Transactional
    void deleteByPersonId(@Param("personId") UUID personId);

    @Modifying
    @Transactional
    @Query("DELETE from Document d where d.personid IN ?1")
    void deleteByPersonIds(List<UUID> ids);
}
