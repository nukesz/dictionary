package com.nukesz.dictionary.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.nukesz.dictionary.domain.Type;
import com.nukesz.dictionary.service.TypeService;
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
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Type.
 */
@RestController
@RequestMapping("/api")
public class TypeResource {

    private final Logger log = LoggerFactory.getLogger(TypeResource.class);
        
    @Inject
    private TypeService typeService;

    /**
     * POST  /types : Create a new type.
     *
     * @param type the type to create
     * @return the ResponseEntity with status 201 (Created) and with body the new type, or with status 400 (Bad Request) if the type has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/types",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Type> createType(@Valid @RequestBody Type type) throws URISyntaxException {
        log.debug("REST request to save Type : {}", type);
        if (type.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("type", "idexists", "A new type cannot already have an ID")).body(null);
        }
        Type result = typeService.save(type);
        return ResponseEntity.created(new URI("/api/types/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("type", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /types : Updates an existing type.
     *
     * @param type the type to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated type,
     * or with status 400 (Bad Request) if the type is not valid,
     * or with status 500 (Internal Server Error) if the type couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @RequestMapping(value = "/types",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Type> updateType(@Valid @RequestBody Type type) throws URISyntaxException {
        log.debug("REST request to update Type : {}", type);
        if (type.getId() == null) {
            return createType(type);
        }
        Type result = typeService.save(type);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("type", type.getId().toString()))
            .body(result);
    }

    /**
     * GET  /types : get all the types.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of types in body
     * @throws URISyntaxException if there is an error to generate the pagination HTTP headers
     */
    @RequestMapping(value = "/types",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Type>> getAllTypes(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Types");
        Page<Type> page = typeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/types");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /types/:id : get the "id" type.
     *
     * @param id the id of the type to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the type, or with status 404 (Not Found)
     */
    @RequestMapping(value = "/types/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Type> getType(@PathVariable Long id) {
        log.debug("REST request to get Type : {}", id);
        Type type = typeService.findOne(id);
        return Optional.ofNullable(type)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /types/:id : delete the "id" type.
     *
     * @param id the id of the type to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @RequestMapping(value = "/types/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteType(@PathVariable Long id) {
        log.debug("REST request to delete Type : {}", id);
        typeService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("type", id.toString())).build();
    }

}
