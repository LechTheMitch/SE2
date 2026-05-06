package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dto.HallDTO;
import com.lechthemitch.sms.dto.HallResponseDTO;

import java.util.List;

public interface HallService {
    HallResponseDTO create(HallDTO dto);

    List<HallResponseDTO> findAll();

    HallResponseDTO findById(Integer id);

    HallResponseDTO update(Integer id, HallDTO dto);

    void deleteById(Integer id);
}
