package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dao.AttendanceRecordRepository;
import com.lechthemitch.sms.dao.ParentRepository;
import com.lechthemitch.sms.dao.RoleRepository;
import com.lechthemitch.sms.dao.StudentRepository;
import com.lechthemitch.sms.dao.UserRepository;
import com.lechthemitch.sms.dto.StudentDTO;
import com.lechthemitch.sms.dto.StudentResponseDTO;
import com.lechthemitch.sms.entity.Parent;
import com.lechthemitch.sms.entity.Role;
import com.lechthemitch.sms.entity.RoleType;
import com.lechthemitch.sms.entity.Student;
import com.lechthemitch.sms.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final AttendanceRecordRepository attendanceRecordRepository;

    private static final int QR_TOKEN_BYTES = 24;
    private static final SecureRandom QR_RANDOM = new SecureRandom();

    @Override
    public StudentResponseDTO create(StudentDTO dto) {
        if (studentRepository.existsById(dto.userId())) {
            throw new IllegalArgumentException("Student already exists for user: " + dto.userId());
        }
        Student student = new Student();
        applyDto(student, dto);
        return toResponse(studentRepository.save(student));
    }

    @Override
    public List<StudentResponseDTO> findAll() {
        return studentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public StudentResponseDTO findById(Integer id) {
        return toResponse(getStudent(id));
    }

    @Override
    public List<StudentResponseDTO> findByParentId(Integer parentId) {
        parentRepository.findById(parentId)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found: " + parentId));
        return studentRepository.findByParentId(parentId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public StudentResponseDTO update(Integer id, StudentDTO dto) {
        Student student = getStudent(id);
        if (student.getUser().getId() != dto.userId()) {
            throw new IllegalArgumentException("Student userId cannot be changed: " + id);
        }
        applyDto(student, dto);
        return toResponse(studentRepository.save(student));
    }

    @Override
    public void deleteById(Integer id) {
        Student student = getStudent(id);
        if (!attendanceRecordRepository.findByStudentId(id).isEmpty()) {
            throw new IllegalArgumentException("Cannot delete student with attendance records: " + id);
        }
        studentRepository.delete(student);
    }

    private Student getStudent(Integer id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + id));
    }

    private void applyDto(Student student, StudentDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.userId()));
        student.setUser(user);
        student.setParentPhoneNumber(dto.parentPhoneNumber());

        if (student.getQrCode() == null || student.getQrCode().isBlank()) {
            student.setQrCode(generateUniqueQrCode());
        }

        // If parentId is provided, use it. Otherwise create/find a parent based on the provided phone number
        if (dto.parentId() != null) {
            Parent parent = parentRepository.findById(dto.parentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent not found: " + dto.parentId()));
            student.setParent(parent);
        } else {
            // Try to find an existing user by phone number
            var parentUserOpt = userRepository.findByPhoneNumber(dto.parentPhoneNumber());
            User parentUser;
            if (parentUserOpt.isPresent()) {
                parentUser = parentUserOpt.get();
            } else {
                // Create a new parent user using student's email prefixed with parent_
                parentUser = new User();
                parentUser.setFirstName(user.getFirstName());
                parentUser.setLastName(user.getLastName());
                parentUser.setEmail("parent_" + user.getEmail());
                parentUser.setPhoneNumber(dto.parentPhoneNumber());
                // Password should be the phone number (will be encoded by userService.save)
                parentUser.setPassword(dto.parentPhoneNumber());
                parentUser.setForcePasswordChange(true);
                parentUser.setForceEmailChange(true);
                // Assign PARENT role if available
                Role parentRole = roleRepository.findByName(RoleType.PARENT).orElse(null);
                parentUser.setRole(parentRole);
                parentUser.setPermissions(Set.of());
                parentUser = userService.save(parentUser);
            }

            // Ensure Parent entity exists for the user
            final User finalParentUser = parentUser;
            Parent parent = parentRepository.findById(finalParentUser.getId())
                    .orElseGet(() -> {
                        Parent newParent = new Parent();
                        newParent.setUser(finalParentUser);
                        return parentRepository.save(newParent);
                    });

            student.setParent(parent);
        }
    }

    private StudentResponseDTO toResponse(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getUser().getId(),
                student.getParent().getId(),
                student.getParentPhoneNumber(),
                student.getQrCode()
        );
    }

    private String generateUniqueQrCode() {
        String token;
        do {
            byte[] randomBytes = new byte[QR_TOKEN_BYTES];
            QR_RANDOM.nextBytes(randomBytes);
            token = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        } while (studentRepository.existsByQrCode(token));
        return token;
    }
}
