package com.vivero.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.AuthDTOs.*;
import com.vivero.entity.Role;
import com.vivero.entity.RoleName;
import com.vivero.entity.User;
import com.vivero.exception.BadRequestException;
import com.vivero.repository.RoleRepository;
import com.vivero.repository.UserRepository;
import com.vivero.security.JwtTokenProvider;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsernameOrEmail(request.getUsername(), request.getUsername())
                .orElseThrow(() -> new BadRequestException("Usuario no encontrado"));

        return AuthResponse.of(jwt, user.getUsername(), user.getFullName(), user.getRole().getName().name());
    }

    @Transactional
    public AuthResponse register(RegisterRequest request, String createdBy) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("El nombre de usuario ya existe");
        }

        RoleName roleName = RoleName.ROLE_VENDEDOR;
        if (request.getRoleName() != null) {
            try {
                roleName = RoleName.valueOf(request.getRoleName());
            } catch (Exception e) {
                // Default to VENDEDOR if invalid
            }
        }

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Rol no encontrado"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(role)
                .active(true)
                .createdBy(createdBy)
                .build();

        userRepository.save(user);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);
        return AuthResponse.of(jwt, user.getUsername(), user.getFullName(), user.getRole().getName().name());
    }
}
