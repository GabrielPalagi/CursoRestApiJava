package com.palagi.demo_park_api.web.controller;

import com.palagi.demo_park_api.entity.Cliente;
import com.palagi.demo_park_api.jwt.JwtUserDetails;
import com.palagi.demo_park_api.repository.projection.ClienteProjection;
import com.palagi.demo_park_api.service.ClienteService;
import com.palagi.demo_park_api.service.UsuarioService;
import com.palagi.demo_park_api.web.dto.ClienteCreatedDto;
import com.palagi.demo_park_api.web.dto.ClienteResponseDto;
import com.palagi.demo_park_api.web.dto.PageableDto;
import com.palagi.demo_park_api.web.dto.UsuarioResponseDto;
import com.palagi.demo_park_api.web.dto.mapper.ClienteMapper;
import com.palagi.demo_park_api.web.dto.mapper.PageableMapper;
import com.palagi.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Clientes", description = "Contem todas as operacoes relativas aos recursos para cadastro,edicao e leitura de um cliente.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo cliente", description = "Recurso para criar um novo cliente vinculado a um usuario cadastrado. " +
            "Requisicao exige uso de um bearer token. Acesso restrito a Role='CLIENTE'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "409", description = "Cliente CPF ja possui cadastro no sistema",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "422", description = "Recurso nao processado por dados de entrada invalidados",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso nao permitido ao perfil de ADMIN",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ClienteResponseDto> created(@RequestBody @Valid ClienteCreatedDto dto, @AuthenticationPrincipal JwtUserDetails userDetails) {

        Cliente cliente = ClienteMapper.toCliente(dto);
        cliente.setUsuario(usuarioService.buscarPorId(userDetails.getId()));
        clienteService.salvar(cliente);

        return ResponseEntity.status(201).body(ClienteMapper.toDto(cliente));
    }


    @Operation(summary = "Localizar um cliente", description = "Recurso para localizar um cliente pelo ID " +
            "Requisicao exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cliente nao encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso nao permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClienteResponseDto> getById(@PathVariable Long id) {
        Cliente cliente = clienteService.buscarPorId(id);
        return ResponseEntity.ok().body(ClienteMapper.toDto(cliente));
    }

    @Operation(summary = "Recuperar lista de cliente", description = "Requisicao exige uso de um bearer token. Acesso restrito a Role='ADMIN'",
            security = @SecurityRequirement(name = "security"),
            parameters = {
                    @Parameter(in = ParameterIn.QUERY, name = "page",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "0")),
                            description = "Representa a pagina retornada"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "size",
                            content = @Content(schema = @Schema(type = "integer", defaultValue = "20")),
                            description = "Representa o total de elemento por pagina"
                    ),
                    @Parameter(in = ParameterIn.QUERY, name = "sort", hidden = true,
                            content = @Content(schema = @Schema(type = "string", defaultValue = "id,asc")),
                            description = "Representa a ordenacao dos resultados, aceita multiplos criterios de ordenacao"
                    )
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recurso localizado com sucesso",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "Cliente nao encontrado",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "403", description = "Recurso nao permitido ao perfil de CLIENTE",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class)))
            })
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageableDto> getAll(@Parameter(hidden = true) @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
        Page<ClienteProjection> clientes = clienteService.buscarTodos(pageable);
        return ResponseEntity.ok().body((PageableMapper.toDto(clientes)));
    }

}
