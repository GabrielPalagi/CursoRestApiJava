package com.palagi.demo_park_api;

import com.palagi.demo_park_api.web.dto.UsuarioCreateDto;
import com.palagi.demo_park_api.web.dto.UsuarioResponseDto;
import com.palagi.demo_park_api.web.dto.UsuarioSenhaDto;
import com.palagi.demo_park_api.web.exception.ErrorMessage;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "/sql/usuarios/usuarios-insert.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@Sql(scripts = "/sql/usuarios/usuarios-delete.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class UsuarioIT {

    @Autowired
    WebTestClient testClient;

    @Test
    public void createUsuario_ComUsernameEPAssowordValidos_RetornarUsuarioCriadoComStatus201() {
        UsuarioResponseDto responsebody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.com", "123456"))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getId()).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getUsername()).isEqualTo("tody@email.com");
        org.assertj.core.api.Assertions.assertThat(responsebody.getRole()).isEqualTo("CLIENTE");
    }

    @Test
    public void createUsuario_ComUsernameInvalido_RetornarErrorMessageStatus422() {
        ErrorMessage responsebody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);

        responsebody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);

        responsebody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.", "123456"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_ComPasswordInvalido_RetornarErrorMessageStatus422() {
        ErrorMessage responsebody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.com", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);

        responsebody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.com", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);

        responsebody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("tody@email.com", "1234567"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);
    }

    @Test
    public void createUsuario_ComUsernameRepetido_RetornarErrorMessageComStatus409() {
        ErrorMessage responsebody = testClient
                .post()
                .uri("/api/v1/usuarios")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioCreateDto("ana@email.com", "123456"))
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(409);
    }

    @Test
    public void getUsuario_ComIdExistente_RetornarUsuarioComStatus200() {
        UsuarioResponseDto responsebody = testClient
                .get()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getId()).isEqualTo(100);
        org.assertj.core.api.Assertions.assertThat(responsebody.getUsername()).isEqualTo("ana@email.com");
        org.assertj.core.api.Assertions.assertThat(responsebody.getRole()).isEqualTo("ADMIN");

        responsebody = testClient
                .get()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responsebody.getUsername()).isEqualTo("bia@email.com");
        org.assertj.core.api.Assertions.assertThat(responsebody.getRole()).isEqualTo("CLIENTE");

        responsebody = testClient
                .get()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getId()).isEqualTo(101);
        org.assertj.core.api.Assertions.assertThat(responsebody.getUsername()).isEqualTo("bia@email.com");
        org.assertj.core.api.Assertions.assertThat(responsebody.getRole()).isEqualTo("CLIENTE");

    }

    @Test
    public void getUsuario_ComIdInexistente_RetornarErrorMessageStatus404() {
        ErrorMessage responsebody = testClient
                .get()
                .uri("/api/v1/usuarios/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(404);
    }

    @Test
    public void getUsuario_ComUsuarioClienteBuscandoOutroCliente_RetornarErrorMessageStatus403() {
        ErrorMessage responsebody = testClient
                .get()
                .uri("/api/v1/usuarios/102")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(403);
    }

    @Test
    public void updatePassword_ComDadosValidos_RetornarStatus204() {
        testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();

        testClient
                .patch()
                .uri("/api/v1/usuarios/101")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    public void updatePassword_ComUsuariosDiferentes_RetornarErrorMessageStatus403() {
        ErrorMessage responsebody = testClient
                .patch()
                .uri("/api/v1/usuarios/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(403);

        responsebody = testClient
                .patch()
                .uri("/api/v1/usuarios/0")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "123456", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(403);
    }

    @Test
    public void updatePassword_ComCampoInvalido_RetornarErrorMessageStatus422() {
        ErrorMessage responsebody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("", "", ""))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);

        responsebody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("12345", "12345", "12345"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);

        responsebody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("1234567", "1234567", "1234567"))
                .exchange()
                .expectStatus().isEqualTo(422)
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(422);
    }

    @Test
    public void updatePassword_ComSenhaNaoIgual_RetornarErrorMessageStatus400() {
        ErrorMessage responsebody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123456", "123123", "123456"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(400);

        responsebody = testClient
                .patch()
                .uri("/api/v1/usuarios/100")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new UsuarioSenhaDto("123123", "123456", "123456"))
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        org.assertj.core.api.Assertions.assertThat(responsebody).isNotNull();
        org.assertj.core.api.Assertions.assertThat(responsebody.getStatus()).isEqualTo(400);
    }

    @Test
    public void getAllUsuarios_SemParametro_ComUsuarioComPermissao_RetornarListaDeUsuariosComStatus200() {
        List<UsuarioResponseDto> responsebody = testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "ana@email.com", "123456"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UsuarioResponseDto.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responsebody).isNotNull();
        Assertions.assertThat(responsebody.size()).isEqualTo(3);
    }

    @Test
    public void getAllUsuarios_SemParametro_ComUsuarioSemPermissao_RetornarListaDeUsuariosComStatus403() {
        ErrorMessage responsebody = testClient
                .get()
                .uri("/api/v1/usuarios")
                .headers(JwtAuthentication.getHeaderAuthorization(testClient, "bia@email.com", "123456"))
                .exchange()
                .expectStatus().isForbidden()
                .expectBody(ErrorMessage.class)
                .returnResult().getResponseBody();

        Assertions.assertThat(responsebody).isNotNull();
        Assertions.assertThat(responsebody.getStatus()).isEqualTo(403);
    }

}