package com.natlex.geosectionservice.repository;

import com.natlex.geosectionservice.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SectionRepository extends JpaRepository<Section, Long> {
    List<Section> findByGeologicalClasses_Code(String code);

    Optional<Section> findByName(String name);

    @Query("SELECT MAX(size(s.geologicalClasses)) FROM Section s")
    Integer findMaxNumberOfGeologicalClasses();
}