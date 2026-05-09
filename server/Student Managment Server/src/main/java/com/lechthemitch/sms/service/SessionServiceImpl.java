package com.lechthemitch.sms.service;

import com.lechthemitch.sms.dao.SessionRepository;
import com.lechthemitch.sms.dto.HallResponseDTO;
import com.lechthemitch.sms.dto.SessionDTO;
import com.lechthemitch.sms.dto.SessionResponseDTO;
import com.lechthemitch.sms.entity.Hall;
import com.lechthemitch.sms.entity.Session;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class SessionServiceImpl implements SessionService {
    private final SessionRepository sessionRepository;

    @Override
    public SessionResponseDTO create(SessionDTO dto) {
        Session session = new Session();
        session.setTitle(dto.title());
        session.setDescription(dto.description());
        return toResponse(sessionRepository.save(session));
    }

    @Override
    public List<SessionResponseDTO> findAll() {
        return sessionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public SessionResponseDTO findById(Integer id) {
        return toResponse(getSession(id));
    }

    @Transactional
    @Override
    public SessionResponseDTO update(Integer id, SessionDTO dto) {
        Session session = getSession(id);
        session.setTitle(dto.title());
        session.setDescription(dto.description());
        return toResponse(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        if (!sessionRepository.existsById(id)) {
            throw new IllegalArgumentException("Session not found: " + id);
        }
        sessionRepository.deleteById(id);
    }

    private Session getSession(Integer id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Session not found: " + id));
    }

    private SessionResponseDTO toResponse(Session session) {
        Set<HallResponseDTO> halls = session.getHalls() == null
                ? Collections.emptySet()
                : session.getHalls().stream()
                .filter(Objects::nonNull)
                .map(this::toHallResponse)
                .collect(Collectors.toSet());

        return new SessionResponseDTO(
                session.getId(),
                session.getTitle(),
                session.getDescription(),
                halls
        );
    }

    private HallResponseDTO toHallResponse(Hall hall) {
        return new HallResponseDTO(
                hall.getId(),
                hall.getName(),
                hall.getLocation(),
                hall.getSessionTime(),
                hall.getSession() == null ? null : hall.getSession().getId()
        );
    }
}
