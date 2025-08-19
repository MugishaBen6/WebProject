package com.flight.management.repositories;

import com.flight.management.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
    boolean existsByName(String name);

    @Query("SELECT r FROM Role r JOIN r.parentRoles pr WHERE r.id = :roleId")
    Set<Role> findParentRoles(Long roleId);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Role r " +
           "WHERE r.id = :childRoleId AND :parentRoleId IN (SELECT pr.id FROM r.parentRoles pr)")
    boolean isParentRole(Long childRoleId, Long parentRoleId);
} 