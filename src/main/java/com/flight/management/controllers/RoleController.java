package com.flight.management.controllers;

import com.flight.management.model.Role;
import com.flight.management.payload.RoleRequest;
import com.flight.management.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<Role> createRole(@Valid @RequestBody RoleRequest roleRequest) {
        Role role = roleService.createRole(roleRequest.getName(), roleRequest.getDescription());
        return ResponseEntity.ok(role);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<Role> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleRequest roleRequest) {
        Role role = roleService.updateRole(roleId, roleRequest.getName(), roleRequest.getDescription());
        return ResponseEntity.ok(role);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<?> deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{childRoleId}/parents/{parentRoleId}")
    public ResponseEntity<?> addParentRole(
            @PathVariable Long childRoleId,
            @PathVariable Long parentRoleId) {
        roleService.addParentRole(childRoleId, parentRoleId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{childRoleId}/parents/{parentRoleId}")
    public ResponseEntity<?> removeParentRole(
            @PathVariable Long childRoleId,
            @PathVariable Long parentRoleId) {
        roleService.removeParentRole(childRoleId, parentRoleId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{roleId}/parents")
    public ResponseEntity<Set<Role>> getParentRoles(@PathVariable Long roleId) {
        Set<Role> parentRoles = roleService.getAllParentRoles(roleId);
        return ResponseEntity.ok(parentRoles);
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRoleById(@PathVariable Long roleId) {
        Role role = roleService.getRoleById(roleId);
        return ResponseEntity.ok(role);
    }
} 