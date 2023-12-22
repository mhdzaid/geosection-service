package com.natlex.geosectionservice.controller;

import com.natlex.geosectionservice.dto.SectionDTO;
import com.natlex.geosectionservice.mapper.SectionMapper;
import com.natlex.geosectionservice.model.Section;
import com.natlex.geosectionservice.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionDTO> createSection(@RequestBody SectionDTO sectionDTO) {
        Section section = SectionMapper.toSection(sectionDTO);
        Section createdSection = sectionService.createSection(section);
        return ResponseEntity.ok(SectionMapper.toSectionDTO(createdSection));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionDTO> getSectionById(@PathVariable Long id) {
        return sectionService.getSectionById(id)
                .map(SectionMapper::toSectionDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<SectionDTO>> getAllSections() {
        List<SectionDTO> sections = sectionService.getAllSections().stream()
                .map(SectionMapper::toSectionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sections);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectionDTO> updateSection(@PathVariable Long id, @RequestBody SectionDTO sectionDTO) {
        Section updatedSection = sectionService.updateSection(id, SectionMapper.toSection(sectionDTO));
        return ResponseEntity.ok(SectionMapper.toSectionDTO(updatedSection));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/by-code")
    public ResponseEntity<List<SectionDTO>> getSectionsByCode(@RequestParam String code) {
        List<Section> sections = sectionService.getSectionsByGeologicalClassCode(code);
        List<SectionDTO> sectionDTOs = sections.stream()
                .map(SectionMapper::toSectionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sectionDTOs);
    }
}

