package com.banco_digital.api.domain.transferencia.useCase;

import com.banco_digital.api.controller.exception.RegraNegocioException;
import com.banco_digital.api.controller.exception.TransacaoDuplicadaException;
import com.banco_digital.api.domain.conta.Conta;
import com.banco_digital.api.domain.conta.ContaRepository;
import com.banco_digital.api.domain.conta.useCase.BuscarContasComLockUseCase;
import com.banco_digital.api.domain.transferencia.Transferencia;
import com.banco_digital.api.shared.dto.TransferenciaRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransferirDinheiroUseCase {
    private final ContaRepository contaRepository;
    private final EnviarNotificacaoUseCase enviarNotificacaoUseCase;
    private final BuscarTransferenciaUseCase buscarTransferenciaUseCase;
    private final RegistrarTransferenciasUseCase registrarTransferenciasUseCase;
    private final BuscarContasComLockUseCase buscarContasComLockUseCase;

    @Transactional(rollbackFor = Exception.class)
    public void executar(String chaveIdempotente, TransferenciaRequestDTO request){
        if (request.idContaOrigem().equals(request.idContaDestino())) {
            throw new RegraNegocioException(
                    "Conta de origem e destino devem ser diferentes"
            );
        }

        List<Conta> contas = buscarContasComLockUseCase.executar(request);
        Conta contaOrigem = getConta(contas, request.idContaOrigem());
        Conta contaDestino = getConta(contas, request.idContaDestino());

        BigDecimal valor = request.valor();

        List<Transferencia> transferencias = buscarTransferenciaUseCase.executar(chaveIdempotente, contaOrigem, contaDestino);

        if (!transferencias.isEmpty()) throw new TransacaoDuplicadaException("Transferência duplicada");

        transferencias = registrarTransferenciasUseCase.executar(chaveIdempotente, valor, contaOrigem, contaDestino);

        contaOrigem.debitar(valor);
        contaDestino.creditar(valor);

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        enviarNotificacaoUseCase.executar(contaOrigem, contaDestino, valor);
        transferencias.forEach(Transferencia::concluirTransferencia);
    }

    private Conta getConta(List<Conta> contas, Long id){
        return contas.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException(String.format("Conta com ID %s nao encontrada", id)));
    }
}
