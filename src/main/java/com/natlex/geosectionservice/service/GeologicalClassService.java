package com.natlex.geosectionservice.service;

import com.natlex.geosectionservice.model.GeologicalClass;
import com.natlex.geosectionservice.repository.GeologicalClassRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class GeologicalClassService {

    private final GeologicalClassRepository geologicalClassRepository;

    @Transactional
    public GeologicalClass saveGeologicalClass(GeologicalClass geologicalClass) {
        return geologicalClassRepository.save(geologicalClass);
    }

    @Transactional(readOnly = true)
    public Optional<GeologicalClass> getGeologicalClassById(Long id) {
        return geologicalClassRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<GeologicalClass> getAllGeologicalClasses() {
        return geologicalClassRepository.findAll();
    }

    @Transactional
    public void deleteGeologicalClass(Long id) {
        geologicalClassRepository.deleteById(id);
    }

    @Transactional
    public GeologicalClass updateGeologicalClass(Long id, GeologicalClass updatedGeologicalClass) {
        return geologicalClassRepository.findById(id)
                .map(geologicalClass -> {
                    geologicalClass.setName(updatedGeologicalClass.getName());
                    geologicalClass.setCode(updatedGeologicalClass.getCode());
                    return geologicalClassRepository.save(geologicalClass);
                })
                .orElseThrow(() -> new RuntimeException("GeologicalClass not found"));
    }

    @Transactional
    public GeologicalClass findOrCreateGeologicalClass(GeologicalClass geoClass) {
        Optional<GeologicalClass> existingGeoClassOpt = geologicalClassRepository
                .findByName(geoClass.getName());

        if (existingGeoClassOpt.isPresent()) {
            GeologicalClass existingGeoClass = existingGeoClassOpt.get();
            existingGeoClass.setCode(geoClass.getCode());
            return existingGeoClass;
        } else {
            return geoClass;
        }
    }
}
