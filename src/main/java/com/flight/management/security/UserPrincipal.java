package com.flight.management.security;

import com.flight.management.model.User;
import com.flight.management.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserPrincipal implements UserDetails {
    private Long id;
    private String fullName;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean twoFactorEnabled;

    public UserPrincipal(Long id, String fullName, String email, String password,
                        Collection<? extends GrantedAuthority> authorities,
                        boolean twoFactorEnabled) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public static UserPrincipal create(User user) {
        // Get direct role authorities
        List<GrantedAuthority> directAuthorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());

        // Get inherited role authorities
        List<GrantedAuthority> inheritedAuthorities = user.getRoles().stream()
            .flatMap(role -> {
                Set<Role> parents = role.getParentRoles();
                return (parents != null ? parents : java.util.Collections.emptySet()).stream();
            })
            .map(parentRole -> new SimpleGrantedAuthority(((Role) parentRole).getName()))
            .collect(Collectors.toList());

        // Combine and remove duplicates
        List<GrantedAuthority> allAuthorities = Stream.concat(
                directAuthorities.stream(),
                inheritedAuthorities.stream())
            .distinct()
            .collect(Collectors.toList());

        return new UserPrincipal(
            user.getId(),
            user.getFullName(),
            user.getEmail(),
            user.getPassword(),
            allAuthorities,
            user.isTwoFactorEnabled()
        );
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPrincipal that = (UserPrincipal) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 