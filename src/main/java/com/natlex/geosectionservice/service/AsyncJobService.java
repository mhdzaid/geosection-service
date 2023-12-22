package com.natlex.geosectionservice.service;

import com.natlex.geosectionservice.model.AsyncJob;
import com.natlex.geosectionservice.repository.AsyncJobRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsyncJobService {

    private final AsyncJobRepository asyncJobRepository;

    @Autowired
    public AsyncJobService(AsyncJobRepository asyncJobRepository) {
        this.asyncJobRepository = asyncJobRepository;
    }

    public AsyncJob createAsyncJob() {
        AsyncJob job = new AsyncJob();
        job.setStatus(AsyncJob.JobStatus.IN_PROGRESS);
        return asyncJobRepository.save(job);
    }

    public AsyncJob updateAsyncJob(AsyncJob asyncJob) {
        return asyncJobRepository.save(asyncJob);
    }

    public AsyncJob getAsyncJob(Long jobId) {
        return asyncJobRepository.findById(jobId)
                .orElseThrow(() -> new EntityNotFoundException("AsyncJob not found"));
    }
}
