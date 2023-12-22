package com.natlex.geosectionservice.service;

import com.natlex.geosectionservice.model.AsyncJob;
import com.natlex.geosectionservice.model.GeologicalClass;
import com.natlex.geosectionservice.model.Section;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileService {

    private final AsyncJobService asyncJobService;
    private final SectionService sectionService;

    @Value("${app.export.file-path}")
    String exportFilePath;

    @Async
    public void importFile(MultipartFile file, AsyncJob job) {

        job.setStartTime(new Date());

        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<Section> sections = new ArrayList<>();
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // Skip header
                if (currentRow.getRowNum() == 0) continue;

                String sectionName = currentRow.getCell(0).getStringCellValue();
                int firstCellNum = currentRow.getFirstCellNum();
                int lastCellNum = currentRow.getLastCellNum();

                List<GeologicalClass> geologicalClassList = new ArrayList<>();
                for (int cellNum = firstCellNum + 1; cellNum < lastCellNum; cellNum += 2) {
                    Cell nameCell = currentRow.getCell(cellNum);
                    Cell codeCell = currentRow.getCell(cellNum + 1);

                    if (nameCell == null || nameCell.getStringCellValue().isBlank() ||
                            codeCell == null || codeCell.getStringCellValue().isBlank()) {
                        continue;
                    }

                    String geoClassName = nameCell.getStringCellValue();
                    String geoClassCode = codeCell.getStringCellValue();

                    GeologicalClass geologicalClass = new GeologicalClass(null, geoClassName, geoClassCode);
                    geologicalClassList.add(geologicalClass);
                }
                Section section = new Section(null, sectionName, geologicalClassList);
                sections.add(section);
            }
            sectionService.processSections(sections);

            job.setStatus(AsyncJob.JobStatus.DONE);
        } catch (Exception e) {
            job.setStatus(AsyncJob.JobStatus.ERROR);
        } finally {
            job.setEndTime(new Date());
            asyncJobService.updateAsyncJob(job);
        }
    }




    @Async
    public void exportFile(AsyncJob job) {
        job.setStartTime(new Date());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = dateFormat.format(new Date());
        String fileName = exportFilePath+"sections_export_" + timestamp + ".xlsx";

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Sections");

            List<Section> sections = sectionService.getAllSections();
            createHeaderRow(sheet);

            int rowNum = 1;
            for (Section section : sections) {
                Row row = sheet.createRow(rowNum++);

                int cellNum = 0;
                row.createCell(cellNum++).setCellValue(section.getName());

                for (GeologicalClass geoClass : section.getGeologicalClasses()) {
                    row.createCell(cellNum++).setCellValue(geoClass.getName());
                    row.createCell(cellNum++).setCellValue(geoClass.getCode());
                }
            }

            try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
                workbook.write(outputStream);
            }

            job.setFileName(fileName);
            job.setStatus(AsyncJob.JobStatus.DONE);
        } catch (IOException e) {
            job.setStatus(AsyncJob.JobStatus.ERROR);
        } finally {
            job.setEndTime(new Date());
            asyncJobService.updateAsyncJob(job);
        }
    }

    public Resource loadFileAsResource(Long jobId) throws MalformedURLException {
        AsyncJob job = asyncJobService.getAsyncJob(jobId);
        if (!job.getStatus().equals(AsyncJob.JobStatus.DONE)) {
            throw new RuntimeException("Exporting is still in process");
        }

        String fileName = job.getFileName();
        Path filePath = Paths.get(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new RuntimeException("File not found " + fileName);
        }

        return resource;
    }

    private void createHeaderRow(Sheet sheet) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Section Name");
        for(int geoClassNumber = 1, cellNumber=1; geoClassNumber <= sectionService.getMaxNumberOfGeologicalClasses(); geoClassNumber++,cellNumber=cellNumber+2) {
            headerRow.createCell(cellNumber).setCellValue("Class " + geoClassNumber + " Name");
            headerRow.createCell(cellNumber + 1).setCellValue("Class " + geoClassNumber + " Code");
        }
    }
}
