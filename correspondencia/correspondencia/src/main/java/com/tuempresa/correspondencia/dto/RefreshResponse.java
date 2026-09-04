package com.tuempresa.correspondencia.dto;
import lombok.*;
@Data @AllArgsConstructor @Builder
public class RefreshResponse {
    private String accessToken;
    private String refreshToken;
    private String tipo = "Bearer";
}