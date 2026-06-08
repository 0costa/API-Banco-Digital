package com.banco_digital.api.domain.transferencia.useCase;

import com.banco_digital.api.domain.conta.Conta;
import com.banco_digital.api.domain.transferencia.Transferencia;
import com.banco_digital.api.domain.transferencia.TransferenciaRepository;
import com.banco_digital.api.shared.enums.TipoTransferencia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RegistrarTransferenciasUseCase {

    private final TransferenciaRepository transferenciaRepository;

    public List<Transferencia> executar(String chave, BigDecimal valor, Conta contaOrigem, Conta contaDestino){
        Transferencia transferenciaOrigem = Transferencia.builder()
                .tipo(TipoTransferencia.ENVIADO)
                .chaveIdempotencia(chaveFormatada(chave, contaOrigem))
                .conta(contaOrigem)
                .valor(valor)
                .descricao(
                        String.format(
                                "Transferencia de %s enviado para %s",
                                valorFormatado(valor),
                                contaDestino.getNome()
                        )
                )
                .build();

        Transferencia transferenciaDestino = Transferencia.builder()
                .tipo(TipoTransferencia.RECEBIDO)
                .chaveIdempotencia(chaveFormatada(chave, contaDestino))
                .conta(contaDestino)
                .valor(valor)
                .descricao(
                        String.format(
                                "Transferencia de %s recebido de %s",
                                valorFormatado(valor),
                                contaOrigem.getNome()
                        )
                )
                .build();




        return List.of(
                transferenciaRepository.save(transferenciaOrigem),
                transferenciaRepository.save(transferenciaDestino)
        );
    }

    private String chaveFormatada(String chave, Conta conta){
        return String.format("conta-%s_%s", conta.getId(), chave);
    }

    private String valorFormatado(BigDecimal valor){
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        return formatoMoeda.format(valor);
    }
}
