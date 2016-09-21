package com.nukesz.dictionary.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nukesz.dictionary.domain.Word;
import com.nukesz.dictionary.service.WordService;
import com.nukesz.dictionary.web.rest.util.HeaderUtil;
import com.nukesz.dictionary.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Word.
 */
@RestController
@RequestMapping("/api")
public class WordResource {

    private final Logger log = LoggerFactory.getLogger(WordResource.class);
        
    @Inject
    private WordService wordService;

    /**
     * POST  /words : Create a new word.
     *
     * @param word the word to create
     * @return the ResponseEntity with status 201 (Created) and with body the new word, or with status 400 (Bad Request) if the word has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/words",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Word> createWord(@RequestBody Word word) throws URISyntaxException {
        log.debug("REST request to save Word : {}", word);
        if (word.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("word", "idexists", "A new word cannot already have an ID")).body(null);
        }
        Word result = wordService.save(word);
        return ResponseEntity.created(new URI("/api/words/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("word", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /words : Updates an existing word.
     *
     * @param word the word to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated word,
     * or with status 400 (Bad Request) if the word is not valid,
     * or with status 500 (Internal Server Error) if the word couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/words",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Word> updateWord(@RequestBody Word word) throws URISyntaxException {
        log.debug("REST request to update Word : {}", word);
        if (word.getId() == null) {
            return createWord(word);
        }
        Word result = wordService.save(word);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("word", word.getId().toString()))
            .body(result);
    }

    /**
     * GET  /words : get all the words.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of words in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/words",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Word>> getAllWords(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Words");
        Page<Word> page = wordService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/words");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /words/:id : get the "id" word.
     *
     * @param id the id of the word to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the word, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/words/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Word> getWord(@PathVariable Long id) {
        log.debug("REST request to get Word : {}", id);
        Word word = wordService.findOne(id);
        return Optional.ofNullable(word)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /words/:id : delete the "id" word.
     *
     * @param id the id of the word to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/words/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteWord(@PathVariable Long id) {
        log.debug("REST request to delete Word : {}", id);
        wordService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("word", id.toString())).build();
    }

}
