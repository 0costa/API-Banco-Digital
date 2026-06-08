package com.banco_digital.api.domain.transferencia.useCase;

import com.banco_digital.api.domain.conta.Conta;
import com.banco_digital.api.service.NotificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class EnviarNotificacaoUseCase {

    private final NotificacaoService notificacaoService;

    public void executar(Conta contaOrigem, Conta contaDestino, BigDecimal valor){
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String valorFormatado = formatoMoeda.format(valor);

        String mensagem = String.format(
                "Transferencia do valor de %s enviado com sucesso para %s",
                valorFormatado,
                contaDestino.getNome()
        );

        notificacaoService.enviar(contaOrigem, mensagem);
    }
}
