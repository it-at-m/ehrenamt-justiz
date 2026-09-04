package de.muenchen.ehrenamtjustiz.backend.rest;

import de.muenchen.ehrenamtjustiz.backend.domain.Konfiguration;
import de.muenchen.ehrenamtjustiz.backend.security.Authorities;
import java.util.Optional;
import java.util.UUID;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

/// Provides a Repository for [Konfiguration]. This Repository is exported as a REST resource.
///
/// The Repository handles CRUD Operations. Every Operation is secured and takes care of the tenancy.
/// For specific Documentation on how the generated REST point behaves, please consider the Spring
/// Data Rest Reference
/// <a href="http://docs.spring.io/spring-data/rest/docs/current/reference/html/">here</a>.
///
@RepositoryRestResource(collectionResourceRel = "konfigurationen", path = "konfigurationen")
public interface KonfigurationRepository extends PagingAndSortingRepository<Konfiguration, UUID> { //NOSONAR

    /// Name for the specific cache.
    String CACHE = "Konfiguration_CACHE";

    /// Get one specific [Konfiguration] by its unique id.
    ///
    /// @param id The identifier of the [Konfiguration].
    /// @return The [Konfiguration] with the requested id.
    @Cacheable(value = CACHE, key = "#p0")
    @PreAuthorize(Authorities.HAS_AUTHORITY_READ_KONFIGURATION)
    Optional<Konfiguration> findById(UUID id);

    /// Create or update a [Konfiguration].
    ///
    /// If the id already exists, the [Konfiguration] will be overridden, hence update. If the id does
    /// not already exist, a new [Konfiguration] will be created, hence create.
    ///
    ///
    /// @param konfiguration The [Konfiguration] that will be saved.
    /// @return the saved [Konfiguration].
    @CachePut(value = CACHE, key = "#p0.id")
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_KONFIGURATION)
    @Transactional
    <S extends Konfiguration> S save(S konfiguration);

    /// Create or update a collection of [Konfiguration].
    ///
    /// If the id already exists, the [Konfiguration]s will be overridden, hence update. If the id does
    /// not already exist, the new [Konfiguration]s will be created, hence create.
    ///
    ///
    /// @param entities The [Konfiguration] that will be saved.
    /// @return the collection saved [Konfiguration].
    @PreAuthorize(Authorities.HAS_AUTHORITY_WRITE_KONFIGURATION)
    @Transactional
    <S extends Konfiguration> Iterable<S> saveAll(Iterable<S> entities);

    /// Delete the [Konfiguration] by a specified id.
    ///
    /// @param id the unique id of the [Konfiguration] that will be deleted.
    @CacheEvict(value = CACHE, key = "#p0")
    @PreAuthorize(Authorities.HAS_AUTHORITY_DELETE_KONFIGURATION)
    @Transactional
    void deleteById(UUID id);

    /// Delete a [Konfiguration] by entity.
    ///
    /// @param entity The [Konfiguration] that will be deleted.
    @CacheEvict(value = CACHE, key = "#p0.id")
    @PreAuthorize(Authorities.HAS_AUTHORITY_DELETE_KONFIGURATION)
    @Transactional
    void delete(Konfiguration entity);

    /// Delete multiple [Konfiguration] entities by their id.
    ///
    /// @param entities The Iterable of [Konfiguration] that will be deleted.
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize(Authorities.HAS_AUTHORITY_DELETE_KONFIGURATION)
    @Transactional
    void deleteAll(Iterable<? extends Konfiguration> entities);

    /// Delete all [Konfiguration] entities.
    @CacheEvict(value = CACHE, allEntries = true)
    @PreAuthorize(Authorities.HAS_AUTHORITY_DELETE_KONFIGURATION)
    @Transactional
    void deleteAll();

    /// Get active configuration
    ///
    /// @param aktiv true/false
    /// @return Array of Configuration
    Konfiguration[] findByAktiv(@Param("aktiv") boolean aktiv);

}
