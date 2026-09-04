package com.tuempresa.correspondencia.security;

import com.tuempresa.correspondencia.entity.Usuario;
import com.tuempresa.correspondencia.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UsuarioRepository repo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario u = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(), u.getPasswordHash(), u.getEstado(),
                true, true, true,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + u.getRol().getNombre()))
        );
    }
}