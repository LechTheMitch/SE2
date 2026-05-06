package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dto.ParentDTO;
import com.lechthemitch.sms.dto.ParentResponseDTO;
import com.lechthemitch.sms.dto.StudentResponseDTO;

import java.util.List;

public interface ParentService {
    ParentResponseDTO create(ParentDTO dto);

    List<ParentResponseDTO> findAll();

    ParentResponseDTO findById(Integer id);

    List<StudentResponseDTO> findChildrenByParentId(Integer id);

    ParentResponseDTO update(Integer id, ParentDTO dto);

    void deleteById(Integer id);
}


