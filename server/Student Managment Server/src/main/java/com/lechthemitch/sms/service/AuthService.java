package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dao.PermissionRepository;
import com.lechthemitch.sms.dao.RoleRepository;
import com.lechthemitch.sms.dao.UserRepository;
import com.lechthemitch.sms.dto.AuthRequestDTO;
import com.lechthemitch.sms.dto.AuthResponseDTO;
import com.lechthemitch.sms.dto.ForcedAccountUpdateDTO;
import com.lechthemitch.sms.dto.RegisterDTO;
import com.lechthemitch.sms.entity.Role;
import com.lechthemitch.sms.entity.RoleType;
import com.lechthemitch.sms.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PermissionRepository permissionRepository;
    private final UserDetailsService userDetailsService;

    public AuthResponseDTO register(RegisterDTO dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("An account already exists for " + dto.email());
        }

        var request = User.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .password(passwordEncoder.encode(dto.password()))
                .email(dto.email())
                .role(findManagedRoleByName(dto.role()))
                .permissions(dto.permissions() != null ? dto.permissions().stream()
                        .map(p -> permissionRepository.findByName(p)
                                .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + p)))
                        .collect(Collectors.toSet()) : Collections.emptySet())
                .build();

        User savedUser = userRepository.save(request);
        return new AuthResponseDTO(jwtService.generateToken(toUserDetails(savedUser)));
    }

    private static UserDetails toUserDetails(User user) {
        var authorities = new HashSet<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().name()));

        if (user.getPermissions() != null) {
            user.getPermissions().forEach(permission ->
                    authorities.add(new SimpleGrantedAuthority(permission.getName().name())));
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }

    public Role findManagedRoleByName(RoleType roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
    }

    public AuthResponseDTO authenticate(AuthRequestDTO request) {
        String identifier = request.emailOrPhone();
        String email = resolveEmailFromIdentifier(identifier);
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.password()
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        if (requiresForcedAccountUpdate(user)) {
            throw new IllegalArgumentException("Account requires password and email change. Use /complete-forced-account-update first.");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(jwtToken);
    }

    private String resolveEmailFromIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return identifier;
        }
        User userByPhone = userRepository.findByPhoneNumber(identifier)
                .orElseThrow(() -> new IllegalArgumentException("User not found with phone number: " + identifier));
        return userByPhone.getEmail();
    }

    public AuthResponseDTO completeForcedAccountUpdate(ForcedAccountUpdateDTO request) {
        String email = resolveEmailFromIdentifier(request.emailOrPhone());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        email,
                        request.currentPassword()
                )
        );

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));

        if (!requiresForcedAccountUpdate(user)) {
            throw new IllegalArgumentException("Account does not require forced updates.");
        }

        if (request.newEmail().startsWith("parent_")) {
            throw new IllegalArgumentException("New email cannot start with parent_.");
        }

        userRepository.findByEmail(request.newEmail())
                .filter(existing -> existing.getId() != user.getId())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("An account already exists for " + request.newEmail());
                });

        user.setEmail(request.newEmail());
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        user.setForcePasswordChange(false);
        user.setForceEmailChange(false);
        User saved = userRepository.save(user);

        String jwtToken = jwtService.generateToken(toUserDetails(saved));
        return new AuthResponseDTO(jwtToken);
    }

    private boolean requiresForcedAccountUpdate(User user) {
        return user.isForcePasswordChange()
                || user.isForceEmailChange()
                || user.getEmail().startsWith("parent_");
    }
}
