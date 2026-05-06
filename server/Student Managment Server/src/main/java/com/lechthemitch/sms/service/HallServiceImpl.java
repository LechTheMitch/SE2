package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dao.HallRepository;
import com.lechthemitch.sms.dao.SessionRepository;
import com.lechthemitch.sms.dto.HallDTO;
import com.lechthemitch.sms.dto.HallResponseDTO;
import com.lechthemitch.sms.entity.Hall;
import com.lechthemitch.sms.entity.Session;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class HallServiceImpl implements HallService {
    private final HallRepository hallRepository;
    private final SessionRepository sessionRepository;

    @Override
    public HallResponseDTO create(HallDTO dto) {
        Hall hall = new Hall();
        applyDto(hall, dto);
        return toResponse(hallRepository.save(hall));
    }

    @Override
    public List<HallResponseDTO> findAll() {
        return hallRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public HallResponseDTO findById(Integer id) {
        return toResponse(getHall(id));
    }

    @Override
    public HallResponseDTO update(Integer id, HallDTO dto) {
        Hall hall = getHall(id);
        applyDto(hall, dto);
        return toResponse(hallRepository.save(hall));
    }

    @Override
    public void deleteById(Integer id) {
        if (!hallRepository.existsById(id)) {
            throw new IllegalArgumentException("Hall not found: " + id);
        }
        hallRepository.deleteById(id);
    }

    private Hall getHall(Integer id) {
        return hallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hall not found: " + id));
    }

    private void applyDto(Hall hall, HallDTO dto) {
        hall.setName(dto.name());
        hall.setLocation(dto.location());
        hall.setSessionTime(dto.sessionTime());
        hall.setSession(resolveSession(dto.sessionId()));
    }

    private Session resolveSession(Integer sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
    }

    private HallResponseDTO toResponse(Hall hall) {
        return new HallResponseDTO(
                hall.getId(),
                hall.getName(),
                hall.getLocation(),
                hall.getSessionTime(),
                hall.getSession() == null ? null : hall.getSession().getId()
        );
    }
}
