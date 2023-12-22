package com.natlex.geosectionservice.controller;

import com.natlex.geosectionservice.dto.GeologicalClassDTO;
import com.natlex.geosectionservice.mapper.GeologicalClassMapper;
import com.natlex.geosectionservice.model.GeologicalClass;
import com.natlex.geosectionservice.service.GeologicalClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/geological-classes")
@RequiredArgsConstructor
public class GeologicalClassController {

    private final GeologicalClassService geologicalClassService;


    @PostMapping
    public ResponseEntity<GeologicalClassDTO> createGeologicalClass(@RequestBody GeologicalClassDTO geologicalClassDTO) {
        GeologicalClass geologicalClass = GeologicalClassMapper.toGeologicalClass(geologicalClassDTO);
        GeologicalClass savedClass = geologicalClassService.saveGeologicalClass(geologicalClass);
        return ResponseEntity.ok(GeologicalClassMapper.toGeologicalClassDTO(savedClass));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeologicalClassDTO> getGeologicalClassById(@PathVariable Long id) {
        GeologicalClass geologicalClass = geologicalClassService.getGeologicalClassById(id)
                .orElseThrow(() -> new RuntimeException("GeologicalClass not found"));
        return ResponseEntity.ok(GeologicalClassMapper.toGeologicalClassDTO(geologicalClass));
    }

    @GetMapping
    public ResponseEntity<List<GeologicalClassDTO>> getAllGeologicalClasses() {
        List<GeologicalClassDTO> dtoList = geologicalClassService.getAllGeologicalClasses()
                .stream()
                .map(GeologicalClassMapper::toGeologicalClassDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGeologicalClass(@PathVariable Long id) {
        geologicalClassService.deleteGeologicalClass(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<GeologicalClassDTO> updateGeologicalClass(@PathVariable Long id, @RequestBody GeologicalClassDTO geologicalClassDTO) {
        GeologicalClass updatedGeologicalClass = GeologicalClassMapper.toGeologicalClass(geologicalClassDTO);
        GeologicalClass savedClass = geologicalClassService.updateGeologicalClass(id, updatedGeologicalClass);
        return ResponseEntity.ok(GeologicalClassMapper.toGeologicalClassDTO(savedClass));
    }
}

