package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dto.SessionDTO;
import com.lechthemitch.sms.dto.SessionResponseDTO;

import java.util.List;

public interface SessionService {
    SessionResponseDTO create(SessionDTO dto);

    List<SessionResponseDTO> findAll();

    SessionResponseDTO findById(Integer id);

    SessionResponseDTO update(Integer id, SessionDTO dto);

    void deleteById(Integer id);
}
