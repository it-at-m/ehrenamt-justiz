package de.muenchen.ehrenamtjustiz.backend.rest;

import de.muenchen.ehrenamtjustiz.backend.domain.Person;
import de.muenchen.ehrenamtjustiz.backend.domain.enums.Status;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides a Repository for {@link Person}. This Repository is exported as a REST
 * resource.
 * <p>
 * The Repository handles CRUD Operations. Every Operation is secured and takes care of the tenancy.
 * For specific Documentation on how the generated REST point behaves, please consider the Spring
 * Data Rest Reference
 * <a href="http://docs.spring.io/spring-data/rest/docs/current/reference/html/">here</a>.
 * </p>
 */
@RepositoryRestResource(collectionResourceRel = "personen", path = "personen")
@PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
public interface PersonRepository extends PagingAndSortingRepository<Person, UUID> { //NOSONAR

    /**
     * Name for the specific cache.
     */
    String CACHE = "Person_CACHE";

    /**
     * Get one specific {@link Person} by its unique id.
     *
     * @param id The identifier of the {@link Person}.
     * @return The {@link Person} with the requested id.
     */
    @Cacheable(value = CACHE, key = "#p0")
    Optional<Person> findById(UUID id);

    /**
     * Create or update a {@link Person}.
     * <p>
     * If the id already exists, the {@link Person} will be overridden, hence update.
     * If the id does not already exist, a new {@link Person} will be created, hence
     * create.
     * </p>
     *
     * @param person The {@link Person} that will be saved.
     * @return the saved {@link Person}.
     */
    @CachePut(value = CACHE, key = "#p0.id")
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    @Transactional
    <S extends Person> S save(S person);

    /**
     * Create or update a collection of {@link Person}.
     * <p>
     * If the id already exists, the {@link Person}s will be overridden, hence update.
     * If the id does not already exist, the new {@link Person}s will be created, hence
     * create.
     * </p>
     *
     * @param entities The {@link Person} that will be saved.
     * @return the collection saved {@link Person}.
     */
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    @Transactional
    <S extends Person> Iterable<S> saveAll(Iterable<S> entities);

    /**
     * Delete the {@link Person} by a specified id.
     *
     * @param id the unique id of the {@link Person} that will be deleted.
     */
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize(Authorities.HAS_AUTHORITY_DELETE_EHRENAMTJUSTIZDATEN)
    @Transactional
    void deleteById(UUID id);

    /**
     * Delete a {@link Person} by entity.
     *
     * @param entity The {@link Person} that will be deleted.
     */
    @CacheEvict(value = CACHE, key = "#p0.id")
    @PreAuthorize(Authorities.HAS_AUTHORITY_DELETE_EHRENAMTJUSTIZDATEN)
    @Transactional
    void delete(Person entity);

    @Modifying
    @Query("delete from Person p where p.id = :id and p.status='InErfassung'")
    @Transactional
    void deleteInErfassung(@Param("id") UUID id);

    /**
     * Delete multiple {@link Person} entities by their id.
     *
     * @param entities The Iterable of {@link Person} that will be deleted.
     */
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize(Authorities.HAS_AUTHORITY_DELETE_EHRENAMTJUSTIZDATEN)
    @Transactional
    void deleteAll(Iterable<? extends Person> entities);

    /**
     * Delete all {@link Person} entities.
     */
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize(Authorities.HAS_AUTHORITY_DELETE_EHRENAMTJUSTIZDATEN)
    @Transactional
    void deleteAll();

    @Query("SELECT r FROM Person r")
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    List<Person> retrieveAll();

    @Query("SELECT s FROM Person s WHERE s.ewoid = :om and s.konfigurationid = :aktiveKonfiguration")
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    Person findByOM(@Param("om") String om, @Param("aktiveKonfiguration") UUID aktiveKonfiguration);

    @Query(
        "SELECT p FROM Person p " +
                "WHERE ((p.familienname like CONCAT('%',:search,'%') and length(:search) > 0 ) " +
                " or (p.vorname like CONCAT('%',:search,'%') and length(:search) > 0)" +
                " or length(:search) = 0  or :search = '*') and p.status = :status and p.konfigurationid = :aktiveKonfiguration"
    )
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    Page<Person> findPersonBySearch(@Param("search") String search, @Param("status") Status status, @Param("aktiveKonfiguration") UUID aktiveKonfiguration,
            Pageable pageable);

    @Query(
        "SELECT p FROM Person p " +
                "WHERE p.status = :status and p.konfigurationid = :aktiveKonfiguration"
    )
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    List<Person> findPersonByStatus(@Param("status") Status status, @Param("aktiveKonfiguration") UUID aktiveKonfiguration);

    @Query("SELECT count(*) FROM Person p where p.status=?1 and p.konfigurationid=?2")
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    long countByStatus(Status status, UUID aktiveKonfiguration);

    @Query("SELECT count(*) FROM Person p where p.status=?1 and p.neuervorschlag=true and p.konfigurationid=?2")
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    long countByNeuerVorschlagStatus(Status status, UUID aktiveKonfiguration);

    @Query(
        "SELECT p FROM Person p " +
                "where p.status='Vorschlag' and p.neuervorschlag=true and p.konfigurationid=?1"
    )
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_EHRENAMTJUSTIZDATEN)
    Person[] getNeueVorschlaege(UUID aktiveKonfiguration);

    @Modifying
    @Transactional
    @Query("update Person p set p.neuervorschlag=false where p.status='Vorschlag' and p.neuervorschlag=true and p.konfigurationid=?1")
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_EHRENAMTJUSTIZDATEN)
    void alsBenachrichtigtMarkieren(UUID aktiveKonfiguration);

}
