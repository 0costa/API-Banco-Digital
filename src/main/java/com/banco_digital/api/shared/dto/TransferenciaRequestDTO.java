package com.banco_digital.api.shared.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Requisição para transferência entre contas")
public record TransferenciaRequestDTO(

        @Schema(description = "ID da conta de origem", example = "1", requiredMode = REQUIRED)
        @NotNull
        Long idContaOrigem,

        @Schema(description = "ID da conta de destino", example = "2", requiredMode = REQUIRED)
        @NotNull
        Long idContaDestino,

        @Schema(description = "Valor da transferência", example = "100.50", requiredMode = REQUIRED)
        BigDecimal valor
) {
}
