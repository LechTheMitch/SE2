package com.lechthemitch.sms.rest;

import com.lechthemitch.sms.dao.ParentRepository;
import com.lechthemitch.sms.dao.StudentRepository;
import com.lechthemitch.sms.dao.UserRepository;
import com.lechthemitch.sms.dto.CurrentUserResponseDTO;
import com.lechthemitch.sms.entity.Permission;
import com.lechthemitch.sms.entity.User;
import com.lechthemitch.sms.security.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<CurrentUserResponseDTO> findAll() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public CurrentUserResponseDTO getCurrentUser(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof UserDetailsImpl userDetails)) {
            throw new IllegalArgumentException("Invalid authentication principal");
        }

        User user = userRepository.findById(userDetails.user().getId())
                .orElseThrow(() -> new IllegalArgumentException("Authenticated user not found"));

        return toResponse(user);
    }

    private CurrentUserResponseDTO toResponse(User user) {
        Set<String> permissions = new HashSet<>();
        if (user.getRole() != null && user.getRole().getPermissions() != null) {
            permissions.addAll(user.getRole().getPermissions().stream().map(Permission::getName).map(Enum::name).toList());
        }
        if (user.getPermissions() != null) {
            permissions.addAll(user.getPermissions().stream().map(Permission::getName).map(Enum::name).toList());
        }

        Integer parentId = parentRepository.findByUserId(user.getId()).map(p -> p.getId()).orElse(null);
        Integer studentId = studentRepository.findByUserId(user.getId()).map(s -> s.getId()).orElse(null);

        return new CurrentUserResponseDTO(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole() == null ? null : user.getRole().getName(),
                Collections.unmodifiableSet(permissions),
                user.isForcePasswordChange(),
                user.isForceEmailChange(),
                parentId,
                studentId
        );
    }
}
