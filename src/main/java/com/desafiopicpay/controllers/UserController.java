package com.desafiopicpay.controllers;

import com.desafiopicpay.dtos.PaginatedUsersResponseDTO;
import com.desafiopicpay.dtos.UserRequestDTO;
import com.desafiopicpay.dtos.UserResponseDTO;
import com.desafiopicpay.services.UserService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;

@RestController()
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<@NonNull UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO newUser = userService.createUser(userRequestDTO);
        return ResponseEntity.ok(newUser);
    }

    @GetMapping
    public ResponseEntity<@NonNull PaginatedUsersResponseDTO> findAllUsersPaginated(Pageable pageable) {
        PaginatedUsersResponseDTO usersPaginated = userService.findAllUsersPaginated(pageable);
        return ResponseEntity.ok(usersPaginated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<@NonNull UserResponseDTO> findUserById(@NonNull @PathVariable Long id) {
        UserResponseDTO user = userService.findUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/document/{document}")
    public ResponseEntity<@NonNull UserResponseDTO> findUserByDocument(@NonNull @PathVariable String document) {
        UserResponseDTO user = userService.findUserByDocument(document);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<@NonNull UserResponseDTO> updateUser(@NonNull @PathVariable Long id, @Valid @RequestBody UserRequestDTO user) {
        UserResponseDTO updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<@NonNull Void> deleteUserById(@NonNull @PathVariable Long id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
