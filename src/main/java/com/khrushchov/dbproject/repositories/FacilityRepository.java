package com.khrushchov.dbproject.repositories;

import com.khrushchov.dbproject.model.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {
    Optional<Facility> findFacilityById(int id);
}
