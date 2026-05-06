package com.lechthemitch.sms.rest;

import com.lechthemitch.sms.dto.AuthRequestDTO;
import com.lechthemitch.sms.dto.AuthResponseDTO;
import com.lechthemitch.sms.dto.ForcedAccountUpdateDTO;
import com.lechthemitch.sms.dto.RegisterDTO;
import com.lechthemitch.sms.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterDTO dto) {
            AuthResponseDTO registeredUser = authService.register(dto);
            return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO dto) {
        AuthResponseDTO registeredUser = authService.authenticate(dto);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/complete-forced-account-update")
    public ResponseEntity<AuthResponseDTO> completeForcedAccountUpdate(@Valid @RequestBody ForcedAccountUpdateDTO dto) {
        AuthResponseDTO updatedUser = authService.completeForcedAccountUpdate(dto);
        return ResponseEntity.ok(updatedUser);
    }

}
