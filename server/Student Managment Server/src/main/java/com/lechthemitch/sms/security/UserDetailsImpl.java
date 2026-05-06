package com.lechthemitch.sms.security;

import com.lechthemitch.sms.entity.Permission;
import com.lechthemitch.sms.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public record UserDetailsImpl(User user) implements UserDetails {
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
        if (user.getRole() != null && user.getRole().getName() != null) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));
            if (user.getRole().getPermissions() != null) {
                for (Permission permission : user.getRole().getPermissions()) {
                    grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName().name()));
                }
            }
        }
        if (user.getPermissions() != null) {
            for (Permission permission : user.getPermissions()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(permission.getName().name()));
            }
        }
        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
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

}