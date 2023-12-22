package com.natlex.geosectionservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class SectionDTO {

    private Long id;
    @NotBlank(message = "Section name must not be blank")
    private String name;
    private List<GeologicalClassDTO> geologicalClasses;
}
