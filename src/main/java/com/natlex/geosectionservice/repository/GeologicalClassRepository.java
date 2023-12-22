package com.natlex.geosectionservice.repository;

import com.natlex.geosectionservice.model.GeologicalClass;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeologicalClassRepository extends JpaRepository<GeologicalClass, Long> {
    Optional<GeologicalClass> findByName(String name);

}