package com.natlex.geosectionservice.file;

import com.natlex.geosectionservice.config.TestSecurityConfig;
import com.natlex.geosectionservice.model.AsyncJob;
import com.natlex.geosectionservice.model.GeologicalClass;
import com.natlex.geosectionservice.model.Section;
import com.natlex.geosectionservice.repository.GeologicalClassRepository;
import com.natlex.geosectionservice.repository.SectionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
public class FileImportExportIntegrationTest {

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
    public void testImportRequestAndJobCreation() throws Exception {
        MockMultipartFile file = createTestExcelFile();

        MvcResult result = mockMvc.perform(multipart("/api/sections/import").file(file))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Long jobId = Long.parseLong(content);

        assertNotNull(jobId);
    }

    @Test
    public void testJobCompletionAndDatabaseState() throws Exception {
        Long jobId = initiateImportProcessAndGetJobId();

        awaitJobCompletion(jobId);

        Optional<Section> optionalSection = sectionRepository.findByName("Section 1");
        assertTrue(optionalSection.isPresent());

        Section section = optionalSection.get();
        assertFalse(section.getGeologicalClasses().isEmpty());

        // Verify that the geological classes data is as expected
        assertEquals(2, section.getGeologicalClasses().size());
        assertTrue(section.getGeologicalClasses().stream()
                .anyMatch(geoClass -> "Geo Class 11".equals(geoClass.getName()) && "GC11".equals(geoClass.getCode())));
        assertTrue(section.getGeologicalClasses().stream()
                .anyMatch(geoClass -> "Geo Class 13".equals(geoClass.getName()) && "GC13".equals(geoClass.getCode())));
    }

    @Test
    public void testExportRequestAndJobCreation() throws Exception {
        populateDatabaseWithTestData();
        MvcResult result = mockMvc.perform(get("/api/sections/export"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Long jobId = Long.parseLong(content);

        assertNotNull(jobId); // Verify that a job ID is returned
    }

    @Test
    public void testExportJobCompletion() throws Exception {
        populateDatabaseWithTestData();
        Long jobId = initiateExportProcessAndGetJobId(); // Utility method to initiate export process

        awaitExportJobCompletion(jobId); // Wait for the job to complete

        // Verify the job status is 'DONE'
        MvcResult statusResult = mockMvc.perform(get("/api/sections/export/" + jobId))
                .andExpect(status().isOk())
                .andReturn();
        String statusContent = statusResult.getResponse().getContentAsString();
        assertEquals(AsyncJob.JobStatus.DONE.toString(), statusContent);
    }

    @Test
    public void testExportFileRetrieval() throws Exception {
        populateDatabaseWithTestData();
        Long jobId = initiateExportProcessAndGetJobId(); // Assumes job is initiated and completed
        awaitExportJobCompletion(jobId); // Ensure job completion

        MvcResult result = mockMvc.perform(get("/api/sections/export/" + jobId + "/file"))
                .andExpect(status().isOk())
                .andReturn();

        byte[] fileContent = result.getResponse().getContentAsByteArray();
        try (InputStream is = new ByteArrayInputStream(fileContent);
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);

            Row firstDataRow = sheet.getRow(1); // Assuming row 0 is header
            assertEquals("Section 1", firstDataRow.getCell(0).getStringCellValue());
            assertEquals("Geo Class 11", firstDataRow.getCell(1).getStringCellValue());
            assertEquals("GC11", firstDataRow.getCell(2).getStringCellValue());

            Row secondDataRow = sheet.getRow(2);
            assertEquals("Section 2", secondDataRow.getCell(0).getStringCellValue());
            assertEquals("Geo Class 21", secondDataRow.getCell(1).getStringCellValue());
            assertEquals("GC21", secondDataRow.getCell(2).getStringCellValue());

        }
    }


    private MockMultipartFile createTestExcelFile() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Test Sheet");

        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Section Name");
        headerRow.createCell(1).setCellValue("Class 1 Name");
        headerRow.createCell(2).setCellValue("Class 1 Code");

        headerRow.createCell(3).setCellValue("Class 2 Name");
        headerRow.createCell(4).setCellValue("Class 2 Code");

        headerRow.createCell(5).setCellValue("Class 3 Name");
        headerRow.createCell(6).setCellValue("Class 3 Code");

        Row dataRow = sheet.createRow(1);
        dataRow.createCell(0).setCellValue("Section 1");

        dataRow.createCell(1).setCellValue("Geo Class 11");
        dataRow.createCell(2).setCellValue("GC11");

        dataRow.createCell(3).setCellValue("");
        dataRow.createCell(4).setCellValue("");

        dataRow.createCell(5).setCellValue("Geo Class 13");
        dataRow.createCell(6).setCellValue("GC13");


        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return new MockMultipartFile(
                "file", "test.xlsx", MediaType.APPLICATION_OCTET_STREAM_VALUE, baos.toByteArray());
    }

    private Long initiateImportProcessAndGetJobId() throws Exception {
        MockMultipartFile file = createTestExcelFile();

        MvcResult result = mockMvc.perform(multipart("/api/sections/import").file(file))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return Long.parseLong(content);
    }

    private void awaitExportJobCompletion(Long jobId) throws Exception {
        boolean isDone = false;
        int attempts = 0;
        while (!isDone && attempts < 10) {
            Thread.sleep(1000); // Wait before checking the status again
            MvcResult statusResult = mockMvc.perform(get("/api/sections/export/" + jobId))
                    .andExpect(status().isOk())
                    .andReturn();
            String statusContent = statusResult.getResponse().getContentAsString();
            isDone = AsyncJob.JobStatus.DONE.toString().equals(statusContent);
            attempts++;
        }
        if (!isDone) {
            throw new AssertionError("Export job did not complete within the expected time frame.");
        }
    }

    private Long initiateExportProcessAndGetJobId() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/sections/export"))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        return Long.parseLong(content); // Extract and return the job ID from the response
    }

    public void populateDatabaseWithTestData() {
        // Create geological classes
        GeologicalClass geoClass1 = new GeologicalClass(null, "Geo Class 11", "GC11");
        GeologicalClass geoClass2 = new GeologicalClass(null, "Geo Class 21", "GC21");

        // Create sections with associated geological classes
        Section section1 = new Section(null, "Section 1", Arrays.asList(geoClass1));
        Section section2 = new Section(null, "Section 2", Arrays.asList(geoClass2));
        sectionRepository.saveAll(Arrays.asList(section1, section2));
    }

    private void awaitJobCompletion(Long jobId) throws Exception {
        boolean isDone = false;
        int attempts = 0;
        while (!isDone) {
            if (attempts >= 10) {
                throw new AssertionError("Job did not complete within the expected time frame.");
            }
            Thread.sleep(1000); // Wait before checking the status again
            MvcResult statusResult = mockMvc.perform(get("/api/sections/import/" + jobId))
                    .andExpect(status().isOk())
                    .andReturn();
            String statusContent = statusResult.getResponse().getContentAsString();
            isDone = AsyncJob.JobStatus.DONE.toString().equals(statusContent);

            attempts++;
        }
    }

}

