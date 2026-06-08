package com.banco_digital.api.domain.conta;

import com.banco_digital.api.controller.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ContaTest {

    private Conta criarContaComSaldo(String saldo) {
        return new Conta(
                1L,
                "João",
                "joao@email.com",
                new BigDecimal(saldo)
        );
    }

    @Test
    void deveDebitarValorComSucesso() {
        Conta conta = criarContaComSaldo("100.00");
        conta.debitar(new BigDecimal("30.00"));

        assertEquals(new BigDecimal("70.00"), conta.getSaldo());
    }

    @Test
    void deveLancarExcecaoQuandoValorForNulo() {
        Conta conta = criarContaComSaldo("100.00");

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> conta.debitar(null)
        );

        assertEquals("Valor deve ser maior que zero", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoValorForZero() {
        Conta conta = criarContaComSaldo("100.00");

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> conta.debitar(BigDecimal.ZERO)
        );

        assertEquals("Valor deve ser maior que zero", exception.getMessage());
    }

    @Test
    void deveLancarExcecaoQuandoSaldoForInsuficiente() {
        Conta conta = criarContaComSaldo("100.00");

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> conta.debitar(new BigDecimal("150.00"))
        );

        assertEquals("Conta nao possui saldo suficiente", exception.getMessage());
    }

    @Test
    void devePermitirDebitarTodoSaldo() {
        Conta conta = criarContaComSaldo("100.00");

        conta.debitar(new BigDecimal("100.00"));

        assertEquals(new BigDecimal("0.00"), conta.getSaldo());
    }

    @Test
    void deveCreditarValorComSucesso() {
        Conta conta = criarContaComSaldo("100.00");

        conta.creditar(new BigDecimal("50.00"));

        assertEquals(new BigDecimal("150.00"), conta.getSaldo());
    }

    @Test
    void deveLancarExcecaoQuandoCreditarValorNegativo() {
        Conta conta = criarContaComSaldo("100.00");

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> conta.creditar(new BigDecimal("-10.00"))
        );

        assertEquals(
                "Nao e posssivel creditar valor negativo",
                exception.getMessage()
        );
    }
}