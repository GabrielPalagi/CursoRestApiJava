package com.palagi.demo_park_api.web.dto.mapper;

import com.palagi.demo_park_api.entity.Cliente;
import com.palagi.demo_park_api.web.dto.ClienteCreatedDto;
import com.palagi.demo_park_api.web.dto.ClienteResponseDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClienteMapper {

    public static Cliente toCliente(ClienteCreatedDto dto) {
        return new ModelMapper().map(dto, Cliente.class);
    }

    public static ClienteResponseDto toDto(Cliente cliente) {
        return new ModelMapper().map(cliente, ClienteResponseDto.class);
    }

}
