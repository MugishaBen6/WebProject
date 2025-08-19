package com.flight.management.repositories;

import com.flight.management.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    List<Admin> findByUsername(String username); // If you need to find by username

    // Add any custom query methods here if needed
}