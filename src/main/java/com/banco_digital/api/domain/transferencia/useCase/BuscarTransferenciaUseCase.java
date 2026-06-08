package com.banco_digital.api.domain.transferencia.useCase;

import com.banco_digital.api.domain.conta.Conta;
import com.banco_digital.api.domain.transferencia.Transferencia;
import com.banco_digital.api.domain.transferencia.TransferenciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarTransferenciaUseCase {

    private final TransferenciaRepository transferenciaRepository;

    public List<Transferencia> executar(String chave, Conta contaOrigem, Conta contaDestino){
        String chaveContaOrigem = chaveFormatada(chave, contaOrigem);
        String chaveContaDestino = chaveFormatada(chave, contaDestino);

        List<String> chaves = List.of(chaveContaOrigem, chaveContaDestino);

        return transferenciaRepository.findAllByChavesIdempotencia(chaves);
    }

    private String chaveFormatada(String chave, Conta conta){
        return String.format("conta-%s_%s", conta.getId(), chave);
    }

}
