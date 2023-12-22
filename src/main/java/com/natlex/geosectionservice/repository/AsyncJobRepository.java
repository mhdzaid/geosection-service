package com.natlex.geosectionservice.repository;

import com.natlex.geosectionservice.model.AsyncJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsyncJobRepository extends JpaRepository<AsyncJob, Long> {
}
