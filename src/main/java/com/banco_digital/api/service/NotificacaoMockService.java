package com.banco_digital.api.service;

import com.banco_digital.api.domain.conta.Conta;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificacaoMockService implements NotificacaoService{

    @Async
    @Override
    public void enviar(Conta conta, String mensagem) {
        System.out.println("[NOTIFICAÇÃO] Para: " + conta.getEmail() + " - Mensagem: " + mensagem);
    }
}
