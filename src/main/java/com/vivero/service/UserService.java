package com.vivero.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vivero.dto.UserDTOs.*;
import com.vivero.entity.Role;
import com.vivero.entity.RoleName;
import com.vivero.entity.User;
import com.vivero.exception.BadRequestException;
import com.vivero.exception.ResourceNotFoundException;
import com.vivero.repository.RoleRepository;
import com.vivero.repository.UserRepository;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    @PostConstruct
    @Transactional
    public void initDefaultUsers() {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElse(null);
        if (adminRole == null) {
            adminRole = roleRepository.save(Role.builder().name(RoleName.ROLE_ADMIN).build());
        }

        Role vendedorRole = roleRepository.findByName(RoleName.ROLE_VENDEDOR).orElse(null);
        if (vendedorRole == null) {
            vendedorRole = roleRepository.save(Role.builder().name(RoleName.ROLE_VENDEDOR).build());
        }

        // Find all matching admin users (by email 'anthony.villalta@hotmail.com', username 'anthony.villalta@hotmail.com' or legacy 'admin')
        List<User> matchingAdmins = userRepository.findUsersByUsernameOrEmail("anthony.villalta@hotmail.com");
        if (matchingAdmins.isEmpty()) {
            matchingAdmins = userRepository.findUsersByUsernameOrEmail("admin");
        }

        if (matchingAdmins.isEmpty()) {
            // Create fresh admin user if missing
            userRepository.save(User.builder()
                    .username("anthony.villalta@hotmail.com")
                    .password(passwordEncoder.encode("060697"))
                    .fullName("Anthony Villalta")
                    .email("anthony.villalta@hotmail.com")
                    .phone("+51 987654321")
                    .role(adminRole)
                    .active(true)
                    .createdBy("system")
                    .build());
        } else {
            // Update primary matching admin user to ensure exact credentials and details
            User primary = matchingAdmins.get(0);
            primary.setUsername("anthony.villalta@hotmail.com");
            primary.setEmail("anthony.villalta@hotmail.com");
            primary.setFullName("Anthony Villalta");
            primary.setPassword(passwordEncoder.encode("060697"));
            primary.setRole(adminRole);
            primary.setActive(true);
            userRepository.save(primary);

            // Clean up any extra duplicate admin rows (ID > primary.getId()) to prevent future collisions
            if (matchingAdmins.size() > 1) {
                for (int i = 1; i < matchingAdmins.size(); i++) {
                    User duplicate = matchingAdmins.get(i);
                    try {
                        userRepository.delete(duplicate);
                    } catch (Exception ignored) {}
                }
            }
        }

        if (!userRepository.existsByUsername("vendedor")) {
            userRepository.save(User.builder()
                    .username("vendedor")
                    .password(passwordEncoder.encode("vendedor123"))
                    .fullName("Vendedor Demo")
                    .email("vendedor@vivero.pe")
                    .phone("+51 988888888")
                    .role(vendedorRole)
                    .active(true)
                    .createdBy("system")
                    .build());
        }

        // Create demo admin (admin@vivero.com / admin123) ONCE if missing
        boolean demoAdminExists = userRepository.existsByUsername("admin@vivero.com")
                               || userRepository.existsByEmail("admin@vivero.com");

        if (!demoAdminExists) {
            userRepository.save(User.builder()
                    .username("admin@vivero.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrador Demo")
                    .email("admin@vivero.com")
                    .phone("+51 999999999")
                    .role(adminRole)
                    .active(true)
                    .createdBy("system")
                    .build());
        }
    }

    public List<UserDTO> getAllUsers() {
        if (userRepository.count() == 0) {
            initDefaultUsers();
        }
        return userRepository.findAllByOrderByIdDesc().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return mapToDTO(user);
    }

    @Transactional
    public UserDTO createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("El nombre de usuario ya existe");
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()
                && userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("El correo electrónico ya está registrado");
        }

        RoleName roleName = RoleName.ROLE_VENDEDOR;
        if (request.getRoleName() != null && !request.getRoleName().trim().isEmpty()) {
            try {
                roleName = RoleName.valueOf(request.getRoleName());
            } catch (Exception e) {
                roleName = RoleName.ROLE_VENDEDOR;
            }
        }

        final RoleName finalRoleName = roleName;
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new BadRequestException("Rol no encontrado: " + finalRoleName.name()));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .role(role)
                .active(true)
                .build();

        return mapToDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getEmail() != null) {
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
                throw new BadRequestException("El correo electrónico ya está registrado");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getActive() != null) user.setActive(request.getActive());
        if (request.getRoleName() != null && !request.getRoleName().trim().isEmpty()) {
            try {
                RoleName roleName = RoleName.valueOf(request.getRoleName());
                Role role = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new BadRequestException("Rol no encontrado: " + roleName.name()));
                user.setRole(role);
            } catch (Exception e) {
                throw new BadRequestException("Rol inválido: " + request.getRoleName());
            }
        }
        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return mapToDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUserStatus(Long id, Boolean active) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        user.setActive(active);
        return mapToDTO(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .roleName(user.getRole() != null ? user.getRole().getName().name() : "ROLE_VENDEDOR")
                .active(user.getActive())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().format(DATE_FMT) : null)
                .updatedAt(user.getUpdatedAt() != null ? user.getUpdatedAt().format(DATE_FMT) : null)
                .build();
    }
}
