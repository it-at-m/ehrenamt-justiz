package de.muenchen.ehrenamtjustiz.backend.domain;

import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_NO_SECURITY_PROFILE;
import static de.muenchen.ehrenamtjustiz.backend.TestConstants.SPRING_TEST_PROFILE;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.muenchen.ehrenamtjustiz.backend.TestConstants;
import de.muenchen.ehrenamtjustiz.backend.rest.KonfigurationRepository;
import de.muenchen.ehrenamtjustiz.backend.testdata.KonfigurationTestDataBuilder;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles(profiles = { SPRING_TEST_PROFILE, SPRING_NO_SECURITY_PROFILE })
class KonfigurationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Container
    @ServiceConnection
    @SuppressWarnings("unused")
    private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(
            DockerImageName.parse(TestConstants.TESTCONTAINERS_POSTGRES_IMAGE));

    private UUID testEntityId;

    @Autowired
    private KonfigurationRepository konfigurationRepository;

    @BeforeEach
    public void setUp() {
        testEntityId = konfigurationRepository.save(new KonfigurationTestDataBuilder().build()).getId();
    }

    @AfterEach
    public void tearDown() {
        konfigurationRepository.deleteById(testEntityId);
    }

    @Nested
    class GetEntity {
        @Test
        void givenEntityId_thenReturnEntity() throws Exception {
            mockMvc.perform(get("/konfigurationen/{theEntityID}", testEntityId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(new MediaType("application", "hal+json")))
                    .andExpect(jsonPath("$.id", is(testEntityId.toString())));
        }
    }

    @Nested
    class GetEntitiesPage {
        @Test
        void givenPageNumberAndPageSize_thenReturnPageOfEntities() throws Exception {
            mockMvc.perform(get("/konfigurationen")
                    .param("pageNumber", "0")
                    .param("pageSize", "10")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(new MediaType("application", "hal+json")))
                    .andExpect(jsonPath("$._embedded.konfigurationen", hasSize(greaterThanOrEqualTo(0))));
        }
    }

    @Nested
    class SaveEntity {
        @Test
        void givenEntity_thenEntityIsSaved() throws Exception {
            final Konfiguration requestDTO = new KonfigurationTestDataBuilder().build();
            final String requestBody = objectMapper.writeValueAsString(requestDTO);

            mockMvc.perform(post("/konfigurationen")
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated());
        }
    }

    @Nested
    class UpdateEntity {
        @Test
        void givenEntity_thenEntityIsUpdated() throws Exception {
            final Konfiguration requestDTO = new KonfigurationTestDataBuilder().build();
            final String requestBody = objectMapper.writeValueAsString(requestDTO);

            mockMvc.perform(put("/konfigurationen/{theEntityId}", testEntityId)
                    .content(requestBody)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

    @Nested
    class DeleteEntity {
        @Test
        void givenEntityId_thenEntityIsDeleted() throws Exception {
            mockMvc.perform(delete("/konfigurationen/{theEntityId}", testEntityId)
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }

}
