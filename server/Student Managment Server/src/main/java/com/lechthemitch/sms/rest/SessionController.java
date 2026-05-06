package com.lechthemitch.sms.rest;

import com.lechthemitch.sms.dto.SessionDTO;
import com.lechthemitch.sms.dto.SessionResponseDTO;
import com.lechthemitch.sms.service.SessionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/sessions")
public class SessionController {
    private final SessionService sessionService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponseDTO create(@Valid @RequestBody SessionDTO dto) {
        return sessionService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public List<SessionResponseDTO> findAll() {
        return sessionService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public SessionResponseDTO findById(@PathVariable Integer id) {
        return sessionService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public SessionResponseDTO update(@PathVariable Integer id, @Valid @RequestBody SessionDTO dto) {
        return sessionService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        sessionService.deleteById(id);
    }
}
