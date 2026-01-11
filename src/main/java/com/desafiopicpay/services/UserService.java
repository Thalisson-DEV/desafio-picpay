package com.desafiopicpay.services;

import com.desafiopicpay.domain.user.User;
import com.desafiopicpay.domain.user.UserType;
import com.desafiopicpay.dtos.PaginatedUsersResponseDTO;
import com.desafiopicpay.dtos.UserRequestDTO;
import com.desafiopicpay.dtos.UserResponseDTO;
import com.desafiopicpay.exceptions.InvalidOperationException;
import com.desafiopicpay.mappers.UserMapper;
import com.desafiopicpay.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import com.desafiopicpay.domain.role.Role;
import com.desafiopicpay.repositories.RoleRepository;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;



    @Transactional
    public UserResponseDTO createUser(UserRequestDTO user) {
        User newUser = this.userMapper.toUserEntity(user);

        validateUserRoles(newUser);

        this.userRepository.save(newUser);
        return userMapper.toUserResponseDTO(newUser);
    }

    public UserResponseDTO findUserById(Long id) {
        User user = this.userRepository.findUserById(id).
                orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toUserResponseDTO(user);
    }

    public UserResponseDTO findUserByDocument(String document) {
        User user = this.userRepository.findUserByDocument(document).
                orElseThrow(() -> new EntityNotFoundException("User not found"));

        return userMapper.toUserResponseDTO(user);
    }

    public User findUserEntityById(Long id) {
        return this.userRepository.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public PaginatedUsersResponseDTO findAllUsersPaginated(Pageable pageable) {
        Page<@NonNull User> users = this.userRepository.findAll(pageable);

        if (users.isEmpty()) {
            throw new EntityNotFoundException("Users not found");
        }

        return userMapper.toPaginatedResponseDTO(users);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, UserRequestDTO user) {
        User userToUpdate = this.userRepository.findUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userMapper.updateUserFromDTO(user, userToUpdate);

        validateUserRoles(userToUpdate);

        return userMapper.toUserResponseDTO(userToUpdate);
    }

    @Transactional
    public void deleteUserById(Long id) {
        if(!this.userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found");
        }

        this.userRepository.deleteById(id);
    }

    @Transactional
    public void saveUser(User user) {
        this.userRepository.save(user);
    }

    private void validateUserRoles(User newUser) {
        if (newUser.getRoles() != null && !newUser.getRoles().isEmpty()) {
            Set<Role> managedRoles = newUser.getRoles().stream()
                    .map(role -> roleRepository.findById(role.getId())
                            .orElseThrow(() -> new EntityNotFoundException("Role not found with id: " + role.getId())))
                    .collect(Collectors.toSet());
            newUser.setRoles(managedRoles);
        }
    }
}
