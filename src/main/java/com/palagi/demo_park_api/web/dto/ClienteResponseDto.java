package com.palagi.demo_park_api.web.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ClienteResponseDto {

    private Long Id;
    private String nome;
    private String cpf;

}
