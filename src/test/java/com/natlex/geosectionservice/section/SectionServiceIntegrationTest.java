package com.natlex.geosectionservice.section;

import com.natlex.geosectionservice.config.TestSecurityConfig;
import com.natlex.geosectionservice.model.GeologicalClass;
import com.natlex.geosectionservice.model.Section;
import com.natlex.geosectionservice.repository.GeologicalClassRepository;
import com.natlex.geosectionservice.repository.SectionRepository;
import com.natlex.geosectionservice.service.GeologicalClassService;
import com.natlex.geosectionservice.service.SectionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TestSecurityConfig.class)
public class SectionServiceIntegrationTest {

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private GeologicalClassRepository geologicalClassRepository;

    private SectionService sectionService;

    private GeologicalClassService geologicalClassService;

    @BeforeEach
    void setUp() {
        geologicalClassService = new GeologicalClassService(geologicalClassRepository);
        sectionService = new SectionService(sectionRepository, geologicalClassService);
        // Initialize the database with test data if necessary
    }

    @Test
    void testUpdateSection() {
        // Arrange
        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        Section section1 = new Section(null, "Section 1", Arrays.asList(geoClass1));
        section1 = sectionRepository.save(section1);
        Long geoClass1Id = geoClass1.getId();

        Section section2 = new Section(null, "Updated Section 1", Arrays.asList(new GeologicalClass(null, "Geo Class 1", "GC1")));

        // Act
        Section result = sectionService.createSection(section2);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Section 1", result.getName());
        assertTrue(result.getGeologicalClasses().stream()
                .anyMatch(gc -> "Geo Class 1".equals(gc.getName()) && "GC1".equals(gc.getCode()) && gc.getId().equals(geoClass1Id)));


    }
}
