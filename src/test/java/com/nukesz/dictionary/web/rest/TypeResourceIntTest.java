package com.nukesz.dictionary.web.rest;

import com.nukesz.dictionary.DictionaryApp;

import com.nukesz.dictionary.domain.Type;
import com.nukesz.dictionary.repository.TypeRepository;
import com.nukesz.dictionary.service.TypeService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TypeResource REST controller.
 *
 * @see TypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DictionaryApp.class)
public class TypeResourceIntTest {

    private static final String DEFAULT_NAME = "A";
    private static final String UPDATED_NAME = "B";

    @Inject
    private TypeRepository typeRepository;

    @Inject
    private TypeService typeService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restTypeMockMvc;

    private Type type;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TypeResource typeResource = new TypeResource();
        ReflectionTestUtils.setField(typeResource, "typeService", typeService);
        this.restTypeMockMvc = MockMvcBuilders.standaloneSetup(typeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Type createEntity(EntityManager em) {
        Type type = new Type()
                .name(DEFAULT_NAME);
        return type;
    }

    @Before
    public void initTest() {
        type = createEntity(em);
    }

    @Test
    @Transactional
    public void createType() throws Exception {
        int databaseSizeBeforeCreate = typeRepository.findAll().size();

        // Create the Type

        restTypeMockMvc.perform(post("/api/types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(type)))
                .andExpect(status().isCreated());

        // Validate the Type in the database
        List<Type> types = typeRepository.findAll();
        assertThat(types).hasSize(databaseSizeBeforeCreate + 1);
        Type testType = types.get(types.size() - 1);
        assertThat(testType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = typeRepository.findAll().size();
        // set the field null
        type.setName(null);

        // Create the Type, which fails.

        restTypeMockMvc.perform(post("/api/types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(type)))
                .andExpect(status().isBadRequest());

        List<Type> types = typeRepository.findAll();
        assertThat(types).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTypes() throws Exception {
        // Initialize the database
        typeRepository.saveAndFlush(type);

        // Get all the types
        restTypeMockMvc.perform(get("/api/types?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(type.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getType() throws Exception {
        // Initialize the database
        typeRepository.saveAndFlush(type);

        // Get the type
        restTypeMockMvc.perform(get("/api/types/{id}", type.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(type.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingType() throws Exception {
        // Get the type
        restTypeMockMvc.perform(get("/api/types/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateType() throws Exception {
        // Initialize the database
        typeService.save(type);

        int databaseSizeBeforeUpdate = typeRepository.findAll().size();

        // Update the type
        Type updatedType = typeRepository.findOne(type.getId());
        updatedType
                .name(UPDATED_NAME);

        restTypeMockMvc.perform(put("/api/types")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedType)))
                .andExpect(status().isOk());

        // Validate the Type in the database
        List<Type> types = typeRepository.findAll();
        assertThat(types).hasSize(databaseSizeBeforeUpdate);
        Type testType = types.get(types.size() - 1);
        assertThat(testType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void deleteType() throws Exception {
        // Initialize the database
        typeService.save(type);

        int databaseSizeBeforeDelete = typeRepository.findAll().size();

        // Get the type
        restTypeMockMvc.perform(delete("/api/types/{id}", type.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Type> types = typeRepository.findAll();
        assertThat(types).hasSize(databaseSizeBeforeDelete - 1);
    }
}
