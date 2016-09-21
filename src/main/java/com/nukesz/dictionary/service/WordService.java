package com.nukesz.dictionary.service;

import com.nukesz.dictionary.domain.Word;
import com.nukesz.dictionary.repository.WordRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Word.
 */
@Service
@Transactional
public class WordService {

    private final Logger log = LoggerFactory.getLogger(WordService.class);
    
    @Inject
    private WordRepository wordRepository;

    /**
     * Save a word.
     *
     * @param word the entity to save
     * @return the persisted entity
     */
    public Word save(Word word) {
        log.debug("Request to save Word : {}", word);
        Word result = wordRepository.save(word);
        return result;
    }

    /**
     *  Get all the words.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Word> findAll(Pageable pageable) {
        log.debug("Request to get all Words");
        Page<Word> result = wordRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one word by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Word findOne(Long id) {
        log.debug("Request to get Word : {}", id);
        Word word = wordRepository.findOne(id);
        return word;
    }

    /**
     *  Delete the  word by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Word : {}", id);
        wordRepository.delete(id);
    }
}
