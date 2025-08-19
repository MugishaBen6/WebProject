package com.flight.management.repositories;

import com.flight.management.model.BackupCode;
import com.flight.management.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BackupCodeRepository extends JpaRepository<BackupCode, Long> {
    List<BackupCode> findByUserAndUsedFalseAndExpiresAtAfter(User user, LocalDateTime now);
    
    Optional<BackupCode> findByUserAndCodeAndUsedFalseAndExpiresAtAfter(
        User user, String code, LocalDateTime now);
    
    @Query("SELECT COUNT(b) FROM BackupCode b WHERE b.user = :user AND b.used = false AND b.expiresAt > :now")
    long countValidBackupCodes(@Param("user") User user, @Param("now") LocalDateTime now);
    
    void deleteByUserAndExpiresAtBefore(User user, LocalDateTime dateTime);
} 