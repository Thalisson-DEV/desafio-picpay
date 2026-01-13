package com.desafiopicpay.controllers;

import com.desafiopicpay.dtos.LoginRequestDTO;
import com.desafiopicpay.dtos.LoginResponseDTO;
import com.desafiopicpay.services.AuthService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping
    public ResponseEntity<@NonNull LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        LoginResponseDTO loginResponseDTO = authService.login(loginRequestDTO);
        return ResponseEntity.ok(loginResponseDTO);
    }
}
