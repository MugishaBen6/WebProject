package com.flight.management.config;

import com.flight.management.model.Role;
import com.flight.management.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) {
        // Create default roles if they don't exist
        createRoleIfNotFound("ROLE_USER", "Default user role");
        createRoleIfNotFound("ROLE_STAFF", "Staff member role");
        createRoleIfNotFound("ROLE_MANAGER", "Manager role");
        createRoleIfNotFound("ROLE_ADMIN", "Administrator role");

        // Set up role hierarchy
        setupRoleHierarchy();
    }

    private void createRoleIfNotFound(String name, String description) {
        if (!roleRepository.existsByName(name)) {
            Role role = new Role(name);
            role.setDescription(description);
            roleRepository.save(role);
        }
    }

    private void setupRoleHierarchy() {
        try {
            // Get all roles
            Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("User role not found"));
            Role staffRole = roleRepository.findByName("ROLE_STAFF")
                .orElseThrow(() -> new RuntimeException("Staff role not found"));
            Role managerRole = roleRepository.findByName("ROLE_MANAGER")
                .orElseThrow(() -> new RuntimeException("Manager role not found"));
            Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Admin role not found"));

            // Staff inherits User permissions
            if (!staffRole.getParentRoles().contains(userRole)) {
                staffRole.getParentRoles().add(userRole);
                roleRepository.save(staffRole);
            }

            // Manager inherits Staff permissions
            if (!managerRole.getParentRoles().contains(staffRole)) {
                managerRole.getParentRoles().add(staffRole);
                roleRepository.save(managerRole);
            }

            // Admin inherits Manager permissions
            if (!adminRole.getParentRoles().contains(managerRole)) {
                adminRole.getParentRoles().add(managerRole);
                roleRepository.save(adminRole);
            }
        } catch (Exception e) {
            // Log the error but don't prevent application startup
            System.err.println("Error setting up role hierarchy: " + e.getMessage());
        }
    }
} 