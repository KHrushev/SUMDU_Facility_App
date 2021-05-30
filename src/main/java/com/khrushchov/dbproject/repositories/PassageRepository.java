package com.khrushchov.dbproject.repositories;

import com.khrushchov.dbproject.model.Passage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassageRepository extends CrudRepository<Passage, String> {
    Optional<Passage> findPassageByEmployeeID(int id);
    Optional<Passage> findPassageById(int id);
}
