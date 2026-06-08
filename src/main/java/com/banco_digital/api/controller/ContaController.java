package com.banco_digital.api.controller;

import com.banco_digital.api.domain.conta.Conta;
import com.banco_digital.api.shared.dto.ContaResponseDTO;
import com.banco_digital.api.shared.dto.TransferenciaResponseDTO;
import com.banco_digital.api.shared.dto.TransferenciaRequestDTO;
import com.banco_digital.api.domain.conta.useCase.BuscarContaBancariaUseCase;
import com.banco_digital.api.domain.conta.useCase.ListarContasBancariasUseCase;
import com.banco_digital.api.domain.conta.useCase.ListarTransferenciasContaUseCase;
import com.banco_digital.api.domain.transferencia.useCase.TransferirDinheiroUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(
    name = "Contas",
    description = "Operações relacionadas a contas bancárias, consulta de saldo, histórico e transferências"
)
@RestController
@RequestMapping("/api/v1/contas")
@RequiredArgsConstructor
public class ContaController {

    private final BuscarContaBancariaUseCase buscarContaBancariaUseCase;
    private final ListarTransferenciasContaUseCase listarTransferenciasContaUseCase;
    private final ListarContasBancariasUseCase listarContasBancariasUseCase;
    private final TransferirDinheiroUseCase transferirDinheiroUseCase;

    @Operation(
            summary = "Lista todas as contas bancárias",
            description = "Retorna a lista completa de contas cadastradas no sistema"
    )
    @ApiResponse(responseCode = "200", description = "Lista de contas retornada com sucesso")
    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> listarContas(){
        List<ContaResponseDTO> contasEncontradas = listarContasBancariasUseCase.executar();
        return ResponseEntity.ok(contasEncontradas);
    }

    @Operation(
            summary = "Busca conta bancária por ID",
            description = "Retorna os dados de uma conta bancária específica"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Conta encontrada com sucesso"),
            @ApiResponse(responseCode = "422", description = "Conta não encontrada")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> buscarConta(
            @Parameter(description = "ID da conta bancária", example = "1")
            @PathVariable Long id
    ){
        Conta contaEncontrada =  buscarContaBancariaUseCase.executar(id);
        return ResponseEntity.ok(ContaResponseDTO.from(contaEncontrada));
    }

    @Operation(
        summary = "Histórico de movimentações da conta",
        description = "Retorna todas as transferências realizadas e recebidas pela conta"
    )
    @ApiResponse(responseCode = "200", description = "Histórico retornado com sucesso")
    @GetMapping("/{id}/historico")
    public ResponseEntity<List<TransferenciaResponseDTO>> historicoDeMovimentacoes(
            @Parameter(description = "ID da conta bancária", example = "1")
            @PathVariable Long id
    ){
        return ResponseEntity.ok(listarTransferenciasContaUseCase.executar(id));
    }

    @Operation(
        summary = "Realiza transferência entre contas",
        description = "Executa uma transferência de valor entre duas contas bancárias"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Transferência realizada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Requisição inválida"),
        @ApiResponse(responseCode = "409", description = "Transferência duplicada (idempotência)")
    })
    @PostMapping("/transferencia")
    public ResponseEntity<Void> transferir(
            @RequestHeader(name = "X-Idempotency-Key")
            @Parameter(
                description = "Chave única de idempotência para evitar duplicidade da transação",
                example = "550e8400-e29b-41d4-a716-446655440000"
            )
            String idempotencyKey,
            @Valid @RequestBody TransferenciaRequestDTO requestDTO
    ){
        transferirDinheiroUseCase.executar(idempotencyKey, requestDTO);
        return ResponseEntity.ok().build();
    }
}
