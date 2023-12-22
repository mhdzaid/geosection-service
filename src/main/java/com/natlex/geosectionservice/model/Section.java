package com.natlex.geosectionservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    @Column(unique = true)
    private String name;

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "section_geological_class",
            joinColumns = @JoinColumn(
                    name = "section_id", referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "geological_class_id", referencedColumnName = "id"
            )
    )
    private List<GeologicalClass> geologicalClasses;
}
