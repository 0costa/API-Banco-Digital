package com.banco_digital.api.domain.conta.useCase;

import com.banco_digital.api.domain.conta.Conta;
import com.banco_digital.api.domain.conta.ContaRepository;
import com.banco_digital.api.shared.dto.TransferenciaRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarContasComLockUseCase {

    private final ContaRepository contaRepository;

    public List<Conta> executar(TransferenciaRequestDTO request){
        return contaRepository.buscarComLock(List.of(request.idContaOrigem(), request.idContaDestino()));
    }
}
