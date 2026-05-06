package com.lechthemitch.sms.config;

import com.lechthemitch.sms.dao.ParentRepository;
import com.lechthemitch.sms.dao.PermissionRepository;
import com.lechthemitch.sms.dao.RoleRepository;
import com.lechthemitch.sms.dao.StudentRepository;
import com.lechthemitch.sms.dao.UserRepository;
import com.lechthemitch.sms.entity.Parent;
import com.lechthemitch.sms.entity.Permission;
import com.lechthemitch.sms.entity.PermissionType;
import com.lechthemitch.sms.entity.Role;
import com.lechthemitch.sms.entity.RoleType;
import com.lechthemitch.sms.entity.Student;
import com.lechthemitch.sms.entity.User;
import com.lechthemitch.sms.service.UserServiceImpl;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Set;

@Configuration
public class DataSeeder {

    private static final int QR_TOKEN_BYTES = 24;
    private static final SecureRandom QR_RANDOM = new SecureRandom();

    @Bean
    CommandLineRunner seedDatabase(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            ParentRepository parentRepository,
            StudentRepository studentRepository,
            UserServiceImpl userService
    ) {
        return ignored -> {
            Permission readUsers = ensurePermission(permissionRepository, PermissionType.READ_USERS);
            Permission deleteUsers = ensurePermission(permissionRepository, PermissionType.DELETE_USERS);
            Permission readStudents = ensurePermission(permissionRepository, PermissionType.READ_STUDENTS);
            Permission deleteStudents = ensurePermission(permissionRepository, PermissionType.DELETE_STUDENTS);
            Permission viewStudentData = ensurePermission(permissionRepository, PermissionType.VIEW_STUDENT_DATA);

            Role adminRole = ensureRole(roleRepository, RoleType.ADMIN,
                    Set.of(readUsers, deleteUsers, readStudents, deleteStudents, viewStudentData));
            Role parentRole = ensureRole(roleRepository, RoleType.PARENT,
                    Set.of(readStudents, viewStudentData));
            Role studentRole = ensureRole(roleRepository, RoleType.STUDENT,
                    Set.of(viewStudentData));

            ensureUser(userRepository, userService,
                    "admin@example.com", "Admin", "Account", "+15550000001", "admin1234", adminRole);
            User parentUser = ensureUser(userRepository, userService,
                    "parent@example.com", "Parent", "Account", "+15550000002", "parent1234", parentRole);
            User studentUser = ensureUser(userRepository, userService,
                    "student@example.com", "Student", "Account", "+15550000003", "student1234", studentRole);

            Parent parent = parentRepository.findById(parentUser.getId())
                    .orElseGet(() -> {
                        Parent newParent = new Parent();
                        newParent.setUser(parentUser);
                        return parentRepository.save(newParent);
                    });

            if (studentRepository.findById(studentUser.getId()).isEmpty()) {
                Student student = new Student();
                student.setUser(studentUser);
                student.setParent(parent);
                student.setParentPhoneNumber(parent.getUser().getPhoneNumber());
                student.setQrCode(generateQrCodeToken());
                studentRepository.save(student);
            }
        };
    }

    private String generateQrCodeToken() {
        byte[] randomBytes = new byte[QR_TOKEN_BYTES];
        QR_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    private Permission ensurePermission(PermissionRepository permissionRepository, PermissionType permissionType) {
        return permissionRepository.findByName(permissionType)
                .orElseGet(() -> {
                    Permission permission = new Permission();
                    permission.setName(permissionType);
                    return permissionRepository.save(permission);
                });
    }

    private Role ensureRole(RoleRepository roleRepository, RoleType roleType, Set<Permission> permissions) {
        return roleRepository.findByName(roleType)
                .map(role -> {
                    role.setPermissions(permissions);
                    return roleRepository.save(role);
                })
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName(roleType);
                    role.setPermissions(permissions);
                    return roleRepository.save(role);
                });
    }

    private User ensureUser(
            UserRepository userRepository,
            UserServiceImpl userService,
            String email,
            String firstName,
            String lastName,
            String phone,
            String password,
            Role role
    ) {
        return userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User user = new User();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setEmail(email);
                    user.setPhoneNumber(phone);
                    user.setPassword(password);
                    user.setRole(role);
                    user.setPermissions(Set.of());
                    return userService.save(user);
                });
    }
}