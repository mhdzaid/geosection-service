package com.natlex.geosectionservice.section;

import com.natlex.geosectionservice.config.TestSecurityConfig;
import com.natlex.geosectionservice.model.GeologicalClass;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
public class GeologicalClassControllerTest {

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
    public void createGeologicalClassTest() throws Exception {
        String geologicalClassJson = "{\"name\": \"New Class\", \"code\": \"NC1\"}";

        mockMvc.perform(post("/api/geological-classes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(geologicalClassJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Class"))
                .andExpect(jsonPath("$.code").value("NC1"));
    }

    @Test
    public void getGeologicalClassTest() throws Exception {

        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        geoClass1 = geologicalClassRepository.save(geoClass1);

        mockMvc.perform(get("/api/geological-classes/{id}", geoClass1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Geo Class 1"))
                .andExpect(jsonPath("$.code").value("GC1"));
    }

    @Test
    public void updateGeologicalClassTest() throws Exception {

        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        geoClass1 = geologicalClassRepository.save(geoClass1);

        String updatedClassJson = "{\"name\": \"Updated Class\", \"code\": \"UC1\"}";

        mockMvc.perform(put("/api/geological-classes/{id}", geoClass1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedClassJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Class"))
                .andExpect(jsonPath("$.code").value("UC1"));
    }

    @Test
    public void deleteGeologicalClassTest() throws Exception {
        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 1", "GC1");
        geoClass1 = geologicalClassRepository.save(geoClass1);

        mockMvc.perform(delete("/api/geological-classes/{id}", geoClass1.getId()))
                .andExpect(status().isOk());
        assertTrue(geologicalClassRepository.findById(geoClass1.getId()).isEmpty());
    }



}
