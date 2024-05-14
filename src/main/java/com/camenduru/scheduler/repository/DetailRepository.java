package com.camenduru.scheduler.repository;

import com.camenduru.scheduler.domain.Detail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Detail entity.
 */
@Repository
public interface DetailRepository extends MongoRepository<Detail, String> { }
