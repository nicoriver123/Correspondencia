package com.tuempresa.correspondencia.config;

import com.tuempresa.correspondencia.entity.*;
import com.tuempresa.correspondencia.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final RolRepository rolRepo;
    private final TipoDocumentoRepository tipoDocRepo;
    private final CategoriaPqrsRepository catRepo;
    private final UsuarioRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (rolRepo.count() == 0) {
            rolRepo.save(Rol.builder().nombre("ADMIN").descripcion("Administrador").build());
            rolRepo.save(Rol.builder().nombre("FUNCIONARIO").descripcion("Funcionario / Gestor").build());
            rolRepo.save(Rol.builder().nombre("JEFE_DEPENDENCIA").descripcion("Jefe de dependencia").build());
            rolRepo.save(Rol.builder().nombre("CIUDADANO").descripcion("Ciudadano / Usuario externo").build());
            log.info("✅ Roles creados");
        }
        if (tipoDocRepo.count() == 0) {
            for (String t : new String[]{"Oficio","Memorando","Factura","Circular","Resolución","PQRS","Otro"}) {
                tipoDocRepo.save(TipoDocumento.builder().nombre(t).build());
            }
            log.info("✅ Tipos de documento creados");
        }
        if (catRepo.count() == 0) {
            for (String c : new String[]{"Servicio al cliente","Facturación","Infraestructura","Talento humano","Tecnología","Otros"}) {
                catRepo.save(CategoriaPqrs.builder().nombre(c).build());
            }
            log.info("✅ Categorías PQRS creadas");
        }
        if (!userRepo.existsByEmail("admin@correo.com")) {
            Rol admin = rolRepo.findByNombre("ADMIN").orElseThrow();
            userRepo.save(Usuario.builder()
                    .nombre("Administrador")
                    .email("admin@correo.com")
                    .passwordHash(encoder.encode("admin123"))
                    .rol(admin).estado(true).build());
            log.info("✅ Usuario admin creado: admin@correo.com / admin123");
        }

        // 🆕 NUEVO: Usuario para radicaciones públicas sin login
        if (!userRepo.existsByEmail("publico@correo.com")) {
            Rol ciudadano = rolRepo.findByNombre("CIUDADANO").orElseThrow();
            userRepo.save(Usuario.builder()
                    .nombre("USUARIO PÚBLICO")
                    .email("publico@correo.com")
                    .passwordHash(encoder.encode("publico123"))
                    .rol(ciudadano).estado(true).build());
            log.info("✅ Usuario público creado para radicaciones anónima");
        }
    }
}