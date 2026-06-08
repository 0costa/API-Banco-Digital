package com.banco_digital.api.domain.conta;

import com.banco_digital.api.controller.exception.RegraNegocioException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Table(name = "contas")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal saldo;

    public void debitar(BigDecimal valor){
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor deve ser maior que zero");
        }

        if (saldo.compareTo(valor) < 0) {
            throw new RegraNegocioException("Conta nao possui saldo suficiente");
        }

        saldo = saldo.subtract(valor);
    }

    public void creditar(BigDecimal valor){
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0){
            throw new RegraNegocioException("Nao e posssivel creditar valor negativo");
        }

        saldo = saldo.add(valor);
    }
}
