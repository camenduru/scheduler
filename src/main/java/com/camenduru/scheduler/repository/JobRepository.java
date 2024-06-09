package com.camenduru.scheduler.repository;

import com.camenduru.scheduler.domain.Job;

import java.util.Date;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Job entity.
 */
@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    @Query("{status: {$ne: 'EXPIRED'}, date: {$lt: ?0}, login: {$ne: 'home'}}")
    List<Job> findAllNonExpiredJobsOlderThanTheDate(Date date);
}
