package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dto.StudentDTO;
import com.lechthemitch.sms.dto.StudentResponseDTO;

import java.util.List;

public interface StudentService {
    StudentResponseDTO create(StudentDTO dto);

    List<StudentResponseDTO> findAll();

    StudentResponseDTO findById(Integer id);

    List<StudentResponseDTO> findByParentId(Integer parentId);

    StudentResponseDTO update(Integer id, StudentDTO dto);

    void deleteById(Integer id);
}
