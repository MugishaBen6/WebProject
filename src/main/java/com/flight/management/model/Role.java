package com.flight.management.model;

import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 60, unique = true, nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;

    // Hierarchical relationship - a role can have parent roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_hierarchy",
        joinColumns = @JoinColumn(name = "child_role_id"),
        inverseJoinColumns = @JoinColumn(name = "parent_role_id")
    )
    private Set<Role> parentRoles = new HashSet<>();

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonIgnore
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Role> getParentRoles() {
        if (parentRoles == null) {
            parentRoles = new HashSet<>();
        }
        return parentRoles;
    }

    public void setParentRoles(Set<Role> parentRoles) {
        if (parentRoles == null) {
            this.parentRoles = new HashSet<>();
        } else {
            this.parentRoles = parentRoles;
        }
    }
} 