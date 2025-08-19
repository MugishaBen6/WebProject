package com.flight.management.services;

import com.flight.management.model.Role;
import com.flight.management.model.User;
import com.flight.management.repositories.RoleRepository;
import com.flight.management.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Role createRole(String name, String description) {
        if (roleRepository.existsByName(name)) {
            throw new RuntimeException("Role already exists: " + name);
        }

        Role role = new Role(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long roleId, String name, String description) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found"));

        if (!role.getName().equals(name) && roleRepository.existsByName(name)) {
            throw new RuntimeException("Role name already exists: " + name);
        }

        role.setName(name);
        role.setDescription(description);
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found"));

        // Remove this role from all users that have it
        for (User user : role.getUsers()) {
            user.getRoles().remove(role);
            userRepository.save(user);
        }

        roleRepository.delete(role);
    }

    @Transactional
    public void addParentRole(Long childRoleId, Long parentRoleId) {
        if (childRoleId.equals(parentRoleId)) {
            throw new RuntimeException("A role cannot be its own parent");
        }

        Role childRole = roleRepository.findById(childRoleId)
            .orElseThrow(() -> new RuntimeException("Child role not found"));
        Role parentRole = roleRepository.findById(parentRoleId)
            .orElseThrow(() -> new RuntimeException("Parent role not found"));

        // Check for circular dependency
        if (isCircularDependency(parentRole, childRole)) {
            throw new RuntimeException("Adding this parent role would create a circular dependency");
        }

        childRole.getParentRoles().add(parentRole);
        roleRepository.save(childRole);
    }

    @Transactional
    public void removeParentRole(Long childRoleId, Long parentRoleId) {
        Role childRole = roleRepository.findById(childRoleId)
            .orElseThrow(() -> new RuntimeException("Child role not found"));
        Role parentRole = roleRepository.findById(parentRoleId)
            .orElseThrow(() -> new RuntimeException("Parent role not found"));

        childRole.getParentRoles().remove(parentRole);
        roleRepository.save(childRole);
    }

    @Transactional(readOnly = true)
    public Set<Role> getAllParentRoles(Long roleId) {
        Set<Role> allParentRoles = new HashSet<>();
        Queue<Role> toProcess = new LinkedList<>();
        
        Role initial = roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        toProcess.add(initial);

        while (!toProcess.isEmpty()) {
            Role current = toProcess.poll();
            Set<Role> parentRoles = roleRepository.findParentRoles(current.getId());
            if (parentRoles == null) parentRoles = new HashSet<>();
            for (Role parent : parentRoles) {
                if (allParentRoles.add(parent)) {
                    toProcess.add(parent);
                }
            }
        }

        return allParentRoles;
    }

    @Transactional(readOnly = true)
    private boolean isCircularDependency(Role potentialParent, Role child) {
        Set<Role> visited = new HashSet<>();
        Queue<Role> toCheck = new LinkedList<>();
        toCheck.add(potentialParent);

        while (!toCheck.isEmpty()) {
            Role current = toCheck.poll();
            if (!visited.add(current)) {
                continue;
            }

            if (current.getId().equals(child.getId())) {
                return true;
            }

            Set<Role> parentRoles = roleRepository.findParentRoles(current.getId());
            if (parentRoles == null) parentRoles = new HashSet<>();
            toCheck.addAll(parentRoles);
        }

        return false;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRoleById(Long roleId) {
        return roleRepository.findById(roleId)
            .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public Role getRoleByName(String name) {
        return roleRepository.findByName(name)
            .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }
} 