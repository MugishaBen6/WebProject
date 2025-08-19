package com.flight.management.repositories;

import com.flight.management.model.Airplane;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AirplaneRepository extends JpaRepository<Airplane, Long> {
    List<Airplane> findByModelContainingIgnoreCaseOrManufacturerContainingIgnoreCase(String model, String manufacturer);
    
    @Query("SELECT a FROM Airplane a WHERE " +
           "(:#{#params['model']} IS NULL OR LOWER(a.model) LIKE LOWER(CONCAT('%', :#{#params['model']}, '%'))) AND " +
           "(:#{#params['manufacturer']} IS NULL OR LOWER(a.manufacturer) LIKE LOWER(CONCAT('%', :#{#params['manufacturer']}, '%')))")
    Page<Airplane> findBySearchParams(@Param("params") Map<String, String> searchParams, Pageable pageable);
}