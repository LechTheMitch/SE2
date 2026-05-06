package com.lechthemitch.sms.rest;

import com.lechthemitch.sms.dto.ParentDTO;
import com.lechthemitch.sms.dto.ParentResponseDTO;
import com.lechthemitch.sms.dto.StudentResponseDTO;
import com.lechthemitch.sms.service.ParentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/parents")
public class ParentController {
    private final ParentService parentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    @ResponseStatus(HttpStatus.CREATED)
    public ParentResponseDTO create(@Valid @RequestBody ParentDTO dto) {
        return parentService.create(dto);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    public List<ParentResponseDTO> findAll() {
        return parentService.findAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARENT') and #id == principal.user.id)")
    public ParentResponseDTO findById(@PathVariable Integer id) {
        return parentService.findById(id);
    }

    @GetMapping("/{id}/children")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('PARENT') and #id == principal.user.id)")
    public List<StudentResponseDTO> findChildrenByParentId(@PathVariable Integer id) {
        return parentService.findChildrenByParentId(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT') or (hasRole('PARENT') and #id == principal.user.id)")
    public ParentResponseDTO update(@PathVariable Integer id, @Valid @RequestBody ParentDTO dto) {
        return parentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHING_ASSISTANT')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        parentService.deleteById(id);
    }
}

