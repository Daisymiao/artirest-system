package com.raysmond.artirest.web.rest;

import com.raysmond.artirest.ArtirestApp;

import com.raysmond.artirest.domain.Coordinator;
import com.raysmond.artirest.repository.CoordinatorRepository;
import com.raysmond.artirest.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.raysmond.artirest.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CoordinatorResource REST controller.
 *
 * @see CoordinatorResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ArtirestApp.class)
public class CoordinatorResourceIntTest {

    private static final String DEFAULT_FIRST_PROCESS_ID = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_PROCESS_ID = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_PROCESS_ATTR = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_PROCESS_ATTR = "BBBBBBBBBB";

    private static final String DEFAULT_SECOND_PROCESS_ID = "AAAAAAAAAA";
    private static final String UPDATED_SECOND_PROCESS_ID = "BBBBBBBBBB";

    private static final String DEFAULT_SECOND_PROCESS_ATTR = "AAAAAAAAAA";
    private static final String UPDATED_SECOND_PROCESS_ATTR = "BBBBBBBBBB";

    @Autowired
    private CoordinatorRepository coordinatorRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    private MockMvc restCoordinatorMockMvc;

    private Coordinator coordinator;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final CoordinatorResource coordinatorResource = new CoordinatorResource(coordinatorRepository);
        this.restCoordinatorMockMvc = MockMvcBuilders.standaloneSetup(coordinatorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Coordinator createEntity() {
        Coordinator coordinator = new Coordinator()
            .firstProcessId(DEFAULT_FIRST_PROCESS_ID)
            .firstProcessAttr(DEFAULT_FIRST_PROCESS_ATTR)
            .secondProcessId(DEFAULT_SECOND_PROCESS_ID)
            .secondProcessAttr(DEFAULT_SECOND_PROCESS_ATTR);
        return coordinator;
    }

    @Before
    public void initTest() {
        coordinatorRepository.deleteAll();
        coordinator = createEntity();
    }

    @Test
    public void createCoordinator() throws Exception {
        int databaseSizeBeforeCreate = coordinatorRepository.findAll().size();

        // Create the Coordinator
        restCoordinatorMockMvc.perform(post("/api/coordinators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coordinator)))
            .andExpect(status().isCreated());

        // Validate the Coordinator in the database
        List<Coordinator> coordinatorList = coordinatorRepository.findAll();
        assertThat(coordinatorList).hasSize(databaseSizeBeforeCreate + 1);
        Coordinator testCoordinator = coordinatorList.get(coordinatorList.size() - 1);
        assertThat(testCoordinator.getFirstProcessId()).isEqualTo(DEFAULT_FIRST_PROCESS_ID);
        assertThat(testCoordinator.getFirstProcessAttr()).isEqualTo(DEFAULT_FIRST_PROCESS_ATTR);
        assertThat(testCoordinator.getSecondProcessId()).isEqualTo(DEFAULT_SECOND_PROCESS_ID);
        assertThat(testCoordinator.getSecondProcessAttr()).isEqualTo(DEFAULT_SECOND_PROCESS_ATTR);
    }

    @Test
    public void createCoordinatorWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = coordinatorRepository.findAll().size();

        // Create the Coordinator with an existing ID
        coordinator.setId("existing_id");

        // An entity with an existing ID cannot be created, so this API call must fail
        restCoordinatorMockMvc.perform(post("/api/coordinators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coordinator)))
            .andExpect(status().isBadRequest());

        // Validate the Coordinator in the database
        List<Coordinator> coordinatorList = coordinatorRepository.findAll();
        assertThat(coordinatorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    public void getAllCoordinators() throws Exception {
        // Initialize the database
        coordinatorRepository.save(coordinator);

        // Get all the coordinatorList
        restCoordinatorMockMvc.perform(get("/api/coordinators?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(coordinator.getId())))
            .andExpect(jsonPath("$.[*].firstProcessId").value(hasItem(DEFAULT_FIRST_PROCESS_ID.toString())))
            .andExpect(jsonPath("$.[*].firstProcessAttr").value(hasItem(DEFAULT_FIRST_PROCESS_ATTR.toString())))
            .andExpect(jsonPath("$.[*].secondProcessId").value(hasItem(DEFAULT_SECOND_PROCESS_ID.toString())))
            .andExpect(jsonPath("$.[*].secondProcessAttr").value(hasItem(DEFAULT_SECOND_PROCESS_ATTR.toString())));
    }

    @Test
    public void getCoordinator() throws Exception {
        // Initialize the database
        coordinatorRepository.save(coordinator);

        // Get the coordinator
        restCoordinatorMockMvc.perform(get("/api/coordinators/{id}", coordinator.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(coordinator.getId()))
            .andExpect(jsonPath("$.firstProcessId").value(DEFAULT_FIRST_PROCESS_ID.toString()))
            .andExpect(jsonPath("$.firstProcessAttr").value(DEFAULT_FIRST_PROCESS_ATTR.toString()))
            .andExpect(jsonPath("$.secondProcessId").value(DEFAULT_SECOND_PROCESS_ID.toString()))
            .andExpect(jsonPath("$.secondProcessAttr").value(DEFAULT_SECOND_PROCESS_ATTR.toString()));
    }

    @Test
    public void getNonExistingCoordinator() throws Exception {
        // Get the coordinator
        restCoordinatorMockMvc.perform(get("/api/coordinators/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    public void updateCoordinator() throws Exception {
        // Initialize the database
        coordinatorRepository.save(coordinator);
        int databaseSizeBeforeUpdate = coordinatorRepository.findAll().size();

        // Update the coordinator
        Coordinator updatedCoordinator = coordinatorRepository.findOne(coordinator.getId());
        updatedCoordinator
            .firstProcessId(UPDATED_FIRST_PROCESS_ID)
            .firstProcessAttr(UPDATED_FIRST_PROCESS_ATTR)
            .secondProcessId(UPDATED_SECOND_PROCESS_ID)
            .secondProcessAttr(UPDATED_SECOND_PROCESS_ATTR);

        restCoordinatorMockMvc.perform(put("/api/coordinators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCoordinator)))
            .andExpect(status().isOk());

        // Validate the Coordinator in the database
        List<Coordinator> coordinatorList = coordinatorRepository.findAll();
        assertThat(coordinatorList).hasSize(databaseSizeBeforeUpdate);
        Coordinator testCoordinator = coordinatorList.get(coordinatorList.size() - 1);
        assertThat(testCoordinator.getFirstProcessId()).isEqualTo(UPDATED_FIRST_PROCESS_ID);
        assertThat(testCoordinator.getFirstProcessAttr()).isEqualTo(UPDATED_FIRST_PROCESS_ATTR);
        assertThat(testCoordinator.getSecondProcessId()).isEqualTo(UPDATED_SECOND_PROCESS_ID);
        assertThat(testCoordinator.getSecondProcessAttr()).isEqualTo(UPDATED_SECOND_PROCESS_ATTR);
    }

    @Test
    public void updateNonExistingCoordinator() throws Exception {
        int databaseSizeBeforeUpdate = coordinatorRepository.findAll().size();

        // Create the Coordinator

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCoordinatorMockMvc.perform(put("/api/coordinators")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(coordinator)))
            .andExpect(status().isCreated());

        // Validate the Coordinator in the database
        List<Coordinator> coordinatorList = coordinatorRepository.findAll();
        assertThat(coordinatorList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    public void deleteCoordinator() throws Exception {
        // Initialize the database
        coordinatorRepository.save(coordinator);
        int databaseSizeBeforeDelete = coordinatorRepository.findAll().size();

        // Get the coordinator
        restCoordinatorMockMvc.perform(delete("/api/coordinators/{id}", coordinator.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Coordinator> coordinatorList = coordinatorRepository.findAll();
        assertThat(coordinatorList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Coordinator.class);
        Coordinator coordinator1 = new Coordinator();
        coordinator1.setId("id1");
        Coordinator coordinator2 = new Coordinator();
        coordinator2.setId(coordinator1.getId());
        assertThat(coordinator1).isEqualTo(coordinator2);
        coordinator2.setId("id2");
        assertThat(coordinator1).isNotEqualTo(coordinator2);
        coordinator1.setId(null);
        assertThat(coordinator1).isNotEqualTo(coordinator2);
    }
}
