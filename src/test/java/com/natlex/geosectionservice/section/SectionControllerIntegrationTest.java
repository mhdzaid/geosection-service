package com.natlex.geosectionservice.section;

import com.natlex.geosectionservice.config.TestSecurityConfig;
import com.natlex.geosectionservice.model.GeologicalClass;
import com.natlex.geosectionservice.model.Section;
import com.natlex.geosectionservice.repository.GeologicalClassRepository;
import com.natlex.geosectionservice.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
public class SectionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private GeologicalClassRepository geologicalClassRepository;

    @BeforeEach
    void setUp() {
        sectionRepository.deleteAll();
        geologicalClassRepository.deleteAll();
        // Setup initial database state
    }

    @Test
    void testCreateSection() throws Exception {
        String jsonContent = "{\"name\": \"Section 1\", \"geologicalClasses\": [{\"name\": \"Geo Class 1\", \"code\": \"GC1\"}]}";

        mockMvc.perform(post("/api/sections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Section 1"))
                .andExpect(jsonPath("$.geologicalClasses", hasSize(1)))
                .andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 1"))
                .andExpect(jsonPath("$.geologicalClasses[0].code").value("GC1"));
    }

    @Test
    void testGetAllSections() throws Exception {
        // Pre-populate the database with sections and geological classes
        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        Section section1 = new Section(null, "Section 1", Arrays.asList(geoClass1));
        Section section2 = new Section(null, "Section 2", new ArrayList<>());
        sectionRepository.saveAll(Arrays.asList(section1, section2));

        mockMvc.perform(get("/api/sections"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Section 1"))
                .andExpect(jsonPath("$[0].geologicalClasses", hasSize(1)))
                .andExpect(jsonPath("$[0].geologicalClasses[0].name").value("Geo Class 1"))
                .andExpect(jsonPath("$[0].geologicalClasses[0].code").value("GC1"))
                .andExpect(jsonPath("$[1].name").value("Section 2"))
                .andExpect(jsonPath("$[1].geologicalClasses", hasSize(0)));
    }


    @Test
    void testGetSectionById() throws Exception {
        // Pre-populate the database
        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        Section section = new Section(null, "Section 1", Arrays.asList(geoClass1));
        section = sectionRepository.save(section);

        mockMvc.perform(get("/api/sections/{id}", section.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Section 1"))
                .andExpect(jsonPath("$.geologicalClasses", hasSize(1)))
                .andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 1"))
                .andExpect(jsonPath("$.geologicalClasses[0].code").value("GC1"));
    }

    @Test
    void testGetSectionByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/sections/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateSection() throws Exception {
        // Pre-populate the database
        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        GeologicalClass geoClass2 = new GeologicalClass(null, "Geo Class 2", "GC2");
        Section section = new Section(null, "Section 1", Arrays.asList(geoClass1, geoClass2));
        section = sectionRepository.save(section);

        String jsonContent = "{\n" +
                "    \"name\": \"Updated Section 1\",\n" +
                "    \"geologicalClasses\": [\n" +
                "        {\"name\": \"Geo Class 2\", \"code\": \"GC5\"},\n" +
                "        {\"name\": \"Geo Class 3\", \"code\": \"GC3\"}\n" +
                "    ]\n" +
                "}\n";

        mockMvc.perform(put("/api/sections/{id}", section.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Section 1"))
                .andExpect(jsonPath("$.geologicalClasses", hasSize(2)))
                .andExpect(jsonPath("$.geologicalClasses[0].name").value("Geo Class 2"))
                .andExpect(jsonPath("$.geologicalClasses[0].code").value("GC5"))
                .andExpect(jsonPath("$.geologicalClasses[1].name").value("Geo Class 3"))
                .andExpect(jsonPath("$.geologicalClasses[1].code").value("GC3"));
    }

    @Test
    void testDeleteSection() throws Exception {
        // Pre-populate the database
        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        Section section = new Section(null, "Section 1", Arrays.asList(geoClass1));
        section = sectionRepository.save(section);

        mockMvc.perform(delete("/api/sections/{id}", section.getId()))
                .andExpect(status().isOk());

        assertFalse(sectionRepository.findById(section.getId()).isPresent());
    }

    @Test
    void testGetSectionsByCode() throws Exception {

        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        GeologicalClass geoClass2 = new GeologicalClass(null, "Geo Class 2", "GC2");
        GeologicalClass geoClass3 = new GeologicalClass(null, "Geo Class 3", "GC3");
        Section section1 = new Section(null, "Section 1", Arrays.asList(geoClass1, geoClass2));
        Section section2 = new Section(null, "Section 2", Arrays.asList(geoClass1));
        Section section3 = new Section(null, "Section 3", Arrays.asList(geoClass3));
        sectionRepository.saveAll(Arrays.asList(section1, section2, section3));

        mockMvc.perform(get("/api/sections/by-code?code=GC1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Section 1"))
                .andExpect(jsonPath("$[1].name").value("Section 2"))
                .andExpect(jsonPath("$[0].geologicalClasses[0].code").value("GC1"))
                .andExpect(jsonPath("$[1].geologicalClasses[0].code").value("GC1"));
    }


}
