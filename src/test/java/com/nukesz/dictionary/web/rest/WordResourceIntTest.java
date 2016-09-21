package com.nukesz.dictionary.web.rest;

import com.nukesz.dictionary.DictionaryApp;

import com.nukesz.dictionary.domain.Word;
import com.nukesz.dictionary.repository.WordRepository;
import com.nukesz.dictionary.service.WordService;

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
 * Test class for the WordResource REST controller.
 *
 * @see WordResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DictionaryApp.class)
public class WordResourceIntTest {

    private static final String DEFAULT_SWEDISH = "AAAAA";
    private static final String UPDATED_SWEDISH = "BBBBB";
    private static final String DEFAULT_HUNGARIAN = "AAAAA";
    private static final String UPDATED_HUNGARIAN = "BBBBB";
    private static final String DEFAULT_ENGLISH = "AAAAA";
    private static final String UPDATED_ENGLISH = "BBBBB";
    private static final String DEFAULT_COMMENT = "AAAAA";
    private static final String UPDATED_COMMENT = "BBBBB";

    @Inject
    private WordRepository wordRepository;

    @Inject
    private WordService wordService;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Inject
    private EntityManager em;

    private MockMvc restWordMockMvc;

    private Word word;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        WordResource wordResource = new WordResource();
        ReflectionTestUtils.setField(wordResource, "wordService", wordService);
        this.restWordMockMvc = MockMvcBuilders.standaloneSetup(wordResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Word createEntity(EntityManager em) {
        Word word = new Word()
                .swedish(DEFAULT_SWEDISH)
                .hungarian(DEFAULT_HUNGARIAN)
                .english(DEFAULT_ENGLISH)
                .comment(DEFAULT_COMMENT);
        return word;
    }

    @Before
    public void initTest() {
        word = createEntity(em);
    }

    @Test
    @Transactional
    public void createWord() throws Exception {
        int databaseSizeBeforeCreate = wordRepository.findAll().size();

        // Create the Word

        restWordMockMvc.perform(post("/api/words")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(word)))
                .andExpect(status().isCreated());

        // Validate the Word in the database
        List<Word> words = wordRepository.findAll();
        assertThat(words).hasSize(databaseSizeBeforeCreate + 1);
        Word testWord = words.get(words.size() - 1);
        assertThat(testWord.getSwedish()).isEqualTo(DEFAULT_SWEDISH);
        assertThat(testWord.getHungarian()).isEqualTo(DEFAULT_HUNGARIAN);
        assertThat(testWord.getEnglish()).isEqualTo(DEFAULT_ENGLISH);
        assertThat(testWord.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    @Transactional
    public void getAllWords() throws Exception {
        // Initialize the database
        wordRepository.saveAndFlush(word);

        // Get all the words
        restWordMockMvc.perform(get("/api/words?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(word.getId().intValue())))
                .andExpect(jsonPath("$.[*].swedish").value(hasItem(DEFAULT_SWEDISH.toString())))
                .andExpect(jsonPath("$.[*].hungarian").value(hasItem(DEFAULT_HUNGARIAN.toString())))
                .andExpect(jsonPath("$.[*].english").value(hasItem(DEFAULT_ENGLISH.toString())))
                .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())));
    }

    @Test
    @Transactional
    public void getWord() throws Exception {
        // Initialize the database
        wordRepository.saveAndFlush(word);

        // Get the word
        restWordMockMvc.perform(get("/api/words/{id}", word.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(word.getId().intValue()))
            .andExpect(jsonPath("$.swedish").value(DEFAULT_SWEDISH.toString()))
            .andExpect(jsonPath("$.hungarian").value(DEFAULT_HUNGARIAN.toString()))
            .andExpect(jsonPath("$.english").value(DEFAULT_ENGLISH.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingWord() throws Exception {
        // Get the word
        restWordMockMvc.perform(get("/api/words/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateWord() throws Exception {
        // Initialize the database
        wordService.save(word);

        int databaseSizeBeforeUpdate = wordRepository.findAll().size();

        // Update the word
        Word updatedWord = wordRepository.findOne(word.getId());
        updatedWord
                .swedish(UPDATED_SWEDISH)
                .hungarian(UPDATED_HUNGARIAN)
                .english(UPDATED_ENGLISH)
                .comment(UPDATED_COMMENT);

        restWordMockMvc.perform(put("/api/words")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(updatedWord)))
                .andExpect(status().isOk());

        // Validate the Word in the database
        List<Word> words = wordRepository.findAll();
        assertThat(words).hasSize(databaseSizeBeforeUpdate);
        Word testWord = words.get(words.size() - 1);
        assertThat(testWord.getSwedish()).isEqualTo(UPDATED_SWEDISH);
        assertThat(testWord.getHungarian()).isEqualTo(UPDATED_HUNGARIAN);
        assertThat(testWord.getEnglish()).isEqualTo(UPDATED_ENGLISH);
        assertThat(testWord.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    @Transactional
    public void deleteWord() throws Exception {
        // Initialize the database
        wordService.save(word);

        int databaseSizeBeforeDelete = wordRepository.findAll().size();

        // Get the word
        restWordMockMvc.perform(delete("/api/words/{id}", word.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Word> words = wordRepository.findAll();
        assertThat(words).hasSize(databaseSizeBeforeDelete - 1);
    }
}
