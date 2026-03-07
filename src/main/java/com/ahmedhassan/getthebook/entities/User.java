package com.ahmedhassan.getthebook.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Principal;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "app_user")
@Entity
public class User extends BaseEntity implements Principal, UserDetails {
    @Column(nullable = false, length = 20)
    private String firstName;
    @Column(nullable = false, length = 20)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    private Boolean isAccountEnabled;
    private Boolean isAccountLocked;

    @Transient
    private String fullName;
    public String getFullName() {
        return this.firstName + " " + this.lastName;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Override
    public String getName() {
        return this.email;
    }

    @Override
    public @NonNull Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(
                new SimpleGrantedAuthority(this.role.getName())
        );
    }

    @Override
    public @NonNull String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isAccountLocked;
    }

    @Override
    public boolean isEnabled() {
        return this.isAccountEnabled;
    }
}