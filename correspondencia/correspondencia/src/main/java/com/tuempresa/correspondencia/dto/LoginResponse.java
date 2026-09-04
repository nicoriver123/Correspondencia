package com.tuempresa.correspondencia.dto;
import lombok.*;
@Data @AllArgsConstructor @Builder
public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    private String tipo = "Bearer";
    private Long usuarioId;
    private String nombre;
    private String rol;
}