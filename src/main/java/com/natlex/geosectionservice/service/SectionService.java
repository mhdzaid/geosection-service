package com.natlex.geosectionservice.service;

import com.natlex.geosectionservice.model.GeologicalClass;
import com.natlex.geosectionservice.model.Section;
import com.natlex.geosectionservice.repository.GeologicalClassRepository;
import com.natlex.geosectionservice.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;
    private final GeologicalClassService geologicalClassService;

    @Transactional
    public Section createSection(Section section) {
        List<GeologicalClass> managedGeoClasses = section.getGeologicalClasses().stream()
                .map(geologicalClassService::findOrCreateGeologicalClass)
                .collect(Collectors.toList());
        return sectionRepository.findByName(section.getName())
                .map(s -> {
                    s.setGeologicalClasses(managedGeoClasses);
                    return sectionRepository.save(s);
                })
                .orElseGet(() -> {
                    section.setGeologicalClasses(managedGeoClasses);
                    return sectionRepository.save(section);
                });

    }

    @Transactional
    public void processSections(List<Section> sections) {
        for (Section section : sections) {
            createSection(section);
        }
    }

    public Optional<Section> getSectionById(Long id) {
        return sectionRepository.findById(id);
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    @Transactional
    public Section updateSection(Long id, Section updatedSection) {
        return sectionRepository.findById(id)
                .map(section -> {
                    section.setName(updatedSection.getName());
                    List<GeologicalClass> managedGeoClasses = updatedSection.getGeologicalClasses().stream()
                            .map(geologicalClassService::findOrCreateGeologicalClass)
                            .collect(Collectors.toList());
                    section.setGeologicalClasses(managedGeoClasses);
                    return sectionRepository.save(section);
                })
                .orElseGet(() -> {
                    updatedSection.setId(id);
                    return sectionRepository.save(updatedSection);
                });
    }

    @Transactional
    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Section> getSectionsByGeologicalClassCode(String code) {
        return sectionRepository.findByGeologicalClasses_Code(code);
    }

    @Transactional(readOnly = true)
    public int getMaxNumberOfGeologicalClasses() {
        Integer maxCount = sectionRepository.findMaxNumberOfGeologicalClasses();
        return maxCount != null ? maxCount : 0;
    }
}
