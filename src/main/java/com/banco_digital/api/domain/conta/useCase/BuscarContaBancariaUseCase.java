package com.banco_digital.api.domain.conta.useCase;

import com.banco_digital.api.controller.exception.RegraNegocioException;
import com.banco_digital.api.domain.conta.Conta;
import com.banco_digital.api.domain.conta.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuscarContaBancariaUseCase {

    private final ContaRepository contaRepository;

    public Conta executar(Long id){
        return contaRepository.findById(id)
                .orElseThrow(() -> new RegraNegocioException(String.format("Conta com ID %s nao encontrada", id)));
    }
}
