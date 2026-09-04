package com.tuempresa.correspondencia.service;

import com.tuempresa.correspondencia.dto.*;
import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.exception.BusinessException;
import com.tuempresa.correspondencia.repository.UsuarioRepository;
import com.tuempresa.correspondencia.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    private final UsuarioRepository usuarioRepo;
    private final PasswordEncoder encoder;
    private final RefreshTokenService refreshTokenService;

    public LoginResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        Usuario u = usuarioRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new BusinessException("Usuario no existe"));

        if ("CIUDADANO".equals(u.getRol().getNombre())) {
            throw new BusinessException("Este acceso es solo para funcionarios. Use el portal público para radicar o consultar.");
        }

        String accessToken = tokenProvider.generate(auth);
        RefreshToken rt = refreshTokenService.crear(u);
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(rt.getToken())
                .tipo("Bearer")
                .usuarioId(u.getId())
                .nombre(u.getNombre())
                .rol(u.getRol().getNombre())
                .build();
    }

    public RefreshResponse refresh(String refreshTokenStr) {
        // Validar y rotar el token en una sola transacción atómica
        // (evita la condición de carrera entre validar y crear el nuevo token)
        RefreshTokenService.RotacionResultado resultado = refreshTokenService.rotar(refreshTokenStr);

        String newAccess = tokenProvider.generateFromEmail(resultado.usuario().getEmail());

        return RefreshResponse.builder()
                .accessToken(newAccess)
                .refreshToken(resultado.nuevoToken().getToken())
                .tipo("Bearer")
                .build();
    }

    public void logout(String refreshToken) {
        refreshTokenService.revocar(refreshToken);
    }
}