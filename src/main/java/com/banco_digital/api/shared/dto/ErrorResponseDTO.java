package com.banco_digital.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta padrão de erro da API")
public record ErrorResponseDTO(

        @Schema(description = "Código HTTP do erro", example = "422")
        int status,

        @Schema(description = "Mensagem descritiva do erro", example = "Conta com ID 1 nao encontrada")
        String message,

        @Schema(description = "Data e hora do erro", example = "2026-06-07T14:30:00Z")
        LocalDateTime timestamp,

        @Schema(description = "Lista de erros de validação de campos")
        List<ValidationError> errors

){
    @Schema(description = "Erro relacionado a um campo específico")
    public record ValidationError(

            @Schema(description = "Nome do campo com erro", example = "valor")
            String field,

            @Schema(description = "Mensagem de erro do campo", example = "deve ser maior que zero")
            String message

    ) {}
}