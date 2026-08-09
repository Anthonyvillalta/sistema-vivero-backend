package com.vivero.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vivero.entity.User;
import com.vivero.repository.UserRepository;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        List<User> users = userRepository.findUsersByUsernameOrEmail(usernameOrEmail);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con usuario/correo: " + usernameOrEmail);
        }

        User user = users.get(0);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getActive() != null ? user.getActive() : true,
                true,
                true,
                true,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole() != null && user.getRole().getName() != null ? user.getRole().getName().name() : "ROLE_ADMIN"))
        );
    }
}
