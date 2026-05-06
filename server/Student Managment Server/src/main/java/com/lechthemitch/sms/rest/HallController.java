package com.lechthemitch.sms.rest;

import com.lechthemitch.sms.dto.HallDTO;
import com.lechthemitch.sms.dto.HallResponseDTO;
import com.lechthemitch.sms.service.HallService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/halls")
public class HallController {
    private final HallService hallService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public HallResponseDTO create(@Valid @RequestBody HallDTO dto) {
        return hallService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public List<HallResponseDTO> findAll() {
        return hallService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public HallResponseDTO findById(@PathVariable Integer id) {
        return hallService.findById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public HallResponseDTO update(@PathVariable Integer id, @Valid @RequestBody HallDTO dto) {
        return hallService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        hallService.deleteById(id);
    }
}
