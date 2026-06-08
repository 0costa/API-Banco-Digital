package com.banco_digital.api.service;

import com.banco_digital.api.domain.conta.Conta;

public interface NotificacaoService {
    void enviar(Conta conta, String mensagem);
}
