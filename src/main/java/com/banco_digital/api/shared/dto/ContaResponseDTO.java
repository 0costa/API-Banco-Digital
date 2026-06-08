package com.banco_digital.api.shared.dto;

import com.banco_digital.api.domain.conta.Conta;
import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Conta bancária")
public record ContaResponseDTO(

        @Schema(description = "ID da conta", example = "1")
        Long id,

        @Schema(description = "Nome do titular", example = "João Silva")
        String nome,

        @Schema(description = "E-mail do titular", example = "joao@email.com")
        String email,

        @Schema(description = "Saldo atual", example = "1500.75")
        BigDecimal saldo

) {
    public static ContaResponseDTO from(Conta conta) {
        return new ContaResponseDTO(
                conta.getId(),
                conta.getNome(),
                conta.getEmail(),
                conta.getSaldo()
        );
    }
}