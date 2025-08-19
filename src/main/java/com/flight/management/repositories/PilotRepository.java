package com.flight.management.repositories;

import com.flight.management.model.Pilot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {
    List<Pilot> findByRankContainingIgnoreCase(String rank);
    
    @Query("SELECT p FROM Pilot p WHERE " +
           "(:#{#params['rank']} IS NULL OR LOWER(p.rank) LIKE LOWER(CONCAT('%', :#{#params['rank']}, '%')))")
    Page<Pilot> findBySearchParams(@Param("params") Map<String, String> searchParams, Pageable pageable);
}