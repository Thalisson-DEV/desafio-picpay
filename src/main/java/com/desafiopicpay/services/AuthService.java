package com.desafiopicpay.services;

import com.desafiopicpay.domain.role.Role;
import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.dtos.LoginRequestDTO;
import com.desafiopicpay.dtos.LoginResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtEncoder jwtEncoder;
    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        User user = this.userService.findUserEntityByEmail(loginRequestDTO.email());

        if (!bCryptPasswordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        Instant now = Instant.now();
        long expiresIn = 300L;
        var scopes = user.getRoles()
                .stream()
                .map(Role::getName)
                .collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder()
                .issuer("PicPay")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .claim("scope", scopes)
                .build();

        Jwt jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims));

        return new LoginResponseDTO(jwtValue.getTokenValue(), expiresIn);
    }

}
