package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dao.ParentRepository;
import com.lechthemitch.sms.dao.StudentRepository;
import com.lechthemitch.sms.dao.UserRepository;
import com.lechthemitch.sms.dto.ParentDTO;
import com.lechthemitch.sms.dto.ParentResponseDTO;
import com.lechthemitch.sms.dto.StudentResponseDTO;
import com.lechthemitch.sms.entity.Parent;
import com.lechthemitch.sms.entity.Student;
import com.lechthemitch.sms.entity.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class ParentServiceImpl implements ParentService {
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    public ParentResponseDTO create(ParentDTO dto) {
        if (parentRepository.existsById(dto.userId())) {
            throw new IllegalArgumentException("Parent already exists for user: " + dto.userId());
        }
        Parent parent = new Parent();
        applyDto(parent, dto);
        return toResponse(parentRepository.save(parent));
    }

    @Override
    public List<ParentResponseDTO> findAll() {
        return parentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ParentResponseDTO findById(Integer id) {
        return toResponse(getParent(id));
    }

    @Override
    public List<StudentResponseDTO> findChildrenByParentId(Integer id) {
        getParent(id);
        return studentRepository.findByParentId(id).stream()
                .map(this::toStudentResponse)
                .toList();
    }

    @Override
    @Transactional
    public ParentResponseDTO update(Integer id, ParentDTO dto) {
        Parent parent = getParent(id);
        if (parent.getUser().getId() != dto.userId()) {
            throw new IllegalArgumentException("Parent userId cannot be changed: " + id);
        }
        applyDto(parent, dto);
        return toResponse(parentRepository.save(parent));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        Parent parent = getParent(id);
        List<Student> children = studentRepository.findByParentId(id);
        if (!children.isEmpty()) {
            throw new IllegalArgumentException("Cannot delete parent with assigned students: " + id);
        }
        parentRepository.delete(parent);
    }

    private Parent getParent(Integer id) {
        return parentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Parent not found: " + id));
    }

    private void applyDto(Parent parent, ParentDTO dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.userId()));
        parent.setUser(user);
    }

    private ParentResponseDTO toResponse(Parent parent) {
        return new ParentResponseDTO(
                parent.getId(),
                parent.getUser().getId(),
                parent.getUser().getPhoneNumber(),
                studentRepository.findByParentId(parent.getId()).stream()
                        .map(Student::getId)
                        .toList()
        );
    }

    private StudentResponseDTO toStudentResponse(Student student) {
        return new StudentResponseDTO(
                student.getId(),
                student.getUser().getId(),
                student.getParent().getId(),
                student.getParentPhoneNumber(),
                student.getQrCode()
        );
    }
}