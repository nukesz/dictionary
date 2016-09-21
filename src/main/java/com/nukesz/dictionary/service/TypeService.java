package com.nukesz.dictionary.service;

import com.nukesz.dictionary.domain.Type;
import com.nukesz.dictionary.repository.TypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

/**
 * Service Implementation for managing Type.
 */
@Service
@Transactional
public class TypeService {

    private final Logger log = LoggerFactory.getLogger(TypeService.class);
    
    @Inject
    private TypeRepository typeRepository;

    /**
     * Save a type.
     *
     * @param type the entity to save
     * @return the persisted entity
     */
    public Type save(Type type) {
        log.debug("Request to save Type : {}", type);
        Type result = typeRepository.save(type);
        return result;
    }

    /**
     *  Get all the types.
     *  
     *  @param pageable the pagination information
     *  @return the list of entities
     */
    @Transactional(readOnly = true) 
    public Page<Type> findAll(Pageable pageable) {
        log.debug("Request to get all Types");
        Page<Type> result = typeRepository.findAll(pageable);
        return result;
    }

    /**
     *  Get one type by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Transactional(readOnly = true) 
    public Type findOne(Long id) {
        log.debug("Request to get Type : {}", id);
        Type type = typeRepository.findOne(id);
        return type;
    }

    /**
     *  Delete the  type by id.
     *
     *  @param id the id of the entity
     */
    public void delete(Long id) {
        log.debug("Request to delete Type : {}", id);
        typeRepository.delete(id);
    }
}
