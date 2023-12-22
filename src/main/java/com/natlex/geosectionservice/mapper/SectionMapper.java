package com.natlex.geosectionservice.mapper;

import com.natlex.geosectionservice.dto.GeologicalClassDTO;
import com.natlex.geosectionservice.dto.SectionDTO;
import com.natlex.geosectionservice.model.GeologicalClass;
import com.natlex.geosectionservice.model.Section;

import java.util.List;
import java.util.stream.Collectors;

public class SectionMapper {

    public static SectionDTO toSectionDTO(Section section) {
        if (section == null) {
            return null;
        }

        List<GeologicalClassDTO> geoClassDTOs = section.getGeologicalClasses().stream()
                .map(GeologicalClassMapper::toGeologicalClassDTO)
                .collect(Collectors.toList());

        return new SectionDTO(section.getId(), section.getName(), geoClassDTOs);
    }

    public static Section toSection(SectionDTO sectionDTO) {
        if (sectionDTO == null) {
            return null;
        }

        List<GeologicalClass> geologicalClasses = sectionDTO.getGeologicalClasses().stream()
                .map(GeologicalClassMapper::toGeologicalClass)
                .collect(Collectors.toList());

        return new Section(sectionDTO.getId(), sectionDTO.getName(), geologicalClasses);
    }
}

