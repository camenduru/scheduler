package com.camenduru.scheduler.repository;

import com.camenduru.scheduler.domain.Detail;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the Detail entity.
 */
@Repository
public interface DetailRepository extends MongoRepository<Detail, String> {
    @Query("{ 'membership' : 'FREE' }")
    List<Detail> findAllByMembershipIsFree();

    @Query("{ 'membership' : 'PAID' }")
    List<Detail> findAllByMembershipIsPaid();
 }
