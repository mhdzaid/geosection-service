package com.natlex.geosectionservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GeologicalClassDTO {

    private Long id;

    @NotBlank(message = "Geological Class name must not be blank")
    private String name;

    @NotBlank(message = "Geological Class code must not be blank")
    private String code;
}
