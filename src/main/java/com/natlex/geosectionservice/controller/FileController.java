package com.natlex.geosectionservice.controller;

import com.natlex.geosectionservice.model.AsyncJob;
import com.natlex.geosectionservice.repository.AsyncJobRepository;
import com.natlex.geosectionservice.service.AsyncJobService;
import com.natlex.geosectionservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/sections/")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    private final AsyncJobService asyncJobService;


    @PostMapping("/import")
    public ResponseEntity<Long> importFile(@RequestParam("file") MultipartFile file) {
        AsyncJob job = asyncJobService.createAsyncJob();
        fileService.importFile(file, job);
        return ResponseEntity.ok(job.getId());
    }

    @GetMapping("/import/{id}")
    public ResponseEntity<String> getImportJobStatus(@PathVariable Long id) {
        AsyncJob job = asyncJobService.getAsyncJob(id);
        return ResponseEntity.ok(job.getStatus().toString());
    }

    @GetMapping("/export")
    public ResponseEntity<Long> exportFile() {
        AsyncJob job = asyncJobService.createAsyncJob();
        fileService.exportFile(job);
        return ResponseEntity.ok(job.getId());
    }

    @GetMapping("/export/{id}")
    public ResponseEntity<String> getExportJobStatus(@PathVariable Long id) {
        AsyncJob job = asyncJobService.getAsyncJob(id);
        return ResponseEntity.ok(job.getStatus().toString());
    }

    @GetMapping(value = "/export/{id}/file", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) throws MalformedURLException {
        Resource resource = fileService.loadFileAsResource(id);
        return ResponseEntity.ok().body(resource);
    }
}
