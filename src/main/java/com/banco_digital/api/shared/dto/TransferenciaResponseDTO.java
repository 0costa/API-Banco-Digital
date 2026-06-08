package com.banco_digital.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "Dados de uma transferência realizada entre contas bancárias")
public record TransferenciaResponseDTO(

        @Schema(description = "Descrição da transferência", example = "Transferência enviada para João Silva")
        String descricao,

        @Schema(description = "Valor transferido", example = "250.75")
        BigDecimal valor,

        @Schema(description = "Data e hora da transferência", example = "2026-06-07T14:30:00")
        LocalDateTime data

) {}
