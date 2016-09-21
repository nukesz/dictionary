package com.nukesz.dictionary.repository;

import com.nukesz.dictionary.domain.Word;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Word entity.
 */
@SuppressWarnings("unused")
public interface WordRepository extends JpaRepository<Word,Long> {

}
