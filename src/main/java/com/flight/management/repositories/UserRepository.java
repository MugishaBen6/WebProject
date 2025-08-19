package com.flight.management.repositories;

import com.flight.management.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByResetPasswordToken(String token);
    boolean existsByEmail(String email);

    List<User> findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(String email, String fullName);
    
    @Query("SELECT u FROM User u WHERE " +
           "(:#{#params['email']} IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :#{#params['email']}, '%'))) AND " +
           "(:#{#params['fullName']} IS NULL OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :#{#params['fullName']}, '%')))")
    Page<User> findBySearchParams(@Param("params") Map<String, String> searchParams, Pageable pageable);
    // Add any custom query methods here if needed
}