package com.natlex.geosectionservice.mapper;

import com.natlex.geosectionservice.dto.GeologicalClassDTO;
import com.natlex.geosectionservice.model.GeologicalClass;

public class GeologicalClassMapper {

    public static GeologicalClassDTO toGeologicalClassDTO(GeologicalClass geologicalClass) {
        if (geologicalClass == null) {
            return null;
        }

        return new GeologicalClassDTO(geologicalClass.getId(), geologicalClass.getName(), geologicalClass.getCode());
    }

    public static GeologicalClass toGeologicalClass(GeologicalClassDTO geologicalClassDTO) {
        if (geologicalClassDTO == null) {
            return null;
        }

        return new GeologicalClass(geologicalClassDTO.getId(), geologicalClassDTO.getName(), geologicalClassDTO.getCode());
    }
}

