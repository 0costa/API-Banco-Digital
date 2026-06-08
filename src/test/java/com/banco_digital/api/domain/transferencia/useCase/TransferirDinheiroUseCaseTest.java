package com.banco_digital.api.domain.transferencia.useCase;

import com.banco_digital.api.controller.exception.RegraNegocioException;
import com.banco_digital.api.controller.exception.TransacaoDuplicadaException;
import com.banco_digital.api.domain.conta.Conta;
import com.banco_digital.api.domain.conta.ContaRepository;
import com.banco_digital.api.domain.conta.useCase.BuscarContasComLockUseCase;
import com.banco_digital.api.domain.transferencia.Transferencia;
import com.banco_digital.api.shared.dto.TransferenciaRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferirDinheiroUseCaseTest {

    @Mock
    private ContaRepository contaRepository;

    @Mock
    private BuscarContasComLockUseCase buscarContasComLockUseCase;

    @Mock
    private EnviarNotificacaoUseCase enviarNotificacaoUseCase;

    @Mock
    private BuscarTransferenciaUseCase buscarTransferenciaUseCase;

    @Mock
    private RegistrarTransferenciasUseCase registrarTransferenciasUseCase;

    @InjectMocks
    private TransferirDinheiroUseCase useCase;

    private Conta contaOrigem;
    private Conta contaDestino;

    @BeforeEach
    void setup() {
        contaOrigem = new Conta(
                1L,
                "João",
                "joao@email.com",
                new BigDecimal("100.00")
        );

        contaDestino = new Conta(
                2L,
                "Maria",
                "maria@email.com",
                new BigDecimal("50.00")
        );
    }

    @Test
    void deveTransferirDinheiroComSucesso() {

        String chave = "abc";

        TransferenciaRequestDTO request =
                new TransferenciaRequestDTO(
                        1L,
                        2L,
                        new BigDecimal("30.00")
                );

        Transferencia transferencia = mock(Transferencia.class);

        when(buscarContasComLockUseCase.executar(request))
                .thenReturn(List.of(contaOrigem, contaDestino));

        when(buscarTransferenciaUseCase.executar(
                chave,
                contaOrigem,
                contaDestino))
                .thenReturn(Collections.emptyList());

        when(registrarTransferenciasUseCase.executar(
                chave,
                new BigDecimal("30.00"),
                contaOrigem,
                contaDestino))
                .thenReturn(List.of(transferencia));

        useCase.executar(chave, request);

        assertEquals(
                new BigDecimal("70.00"),
                contaOrigem.getSaldo()
        );

        assertEquals(
                new BigDecimal("80.00"),
                contaDestino.getSaldo()
        );

        verify(contaRepository).save(contaOrigem);
        verify(contaRepository).save(contaDestino);

        verify(enviarNotificacaoUseCase)
                .executar(
                        contaOrigem,
                        contaDestino,
                        new BigDecimal("30.00")
                );

        verify(transferencia).concluirTransferencia();
    }

    @Test
    void deveConcluirTodasTransferenciasRegistradas() {

        Transferencia t1 = mock(Transferencia.class);
        Transferencia t2 = mock(Transferencia.class);

        TransferenciaRequestDTO request =
                new TransferenciaRequestDTO(
                        1L,
                        2L,
                        new BigDecimal("10.00")
                );

        when(buscarContasComLockUseCase.executar(request))
                .thenReturn(List.of(contaOrigem, contaDestino));

        when(buscarTransferenciaUseCase.executar(
                anyString(),
                any(),
                any()))
                .thenReturn(Collections.emptyList());

        when(registrarTransferenciasUseCase.executar(
                anyString(),
                any(),
                any(),
                any()))
                .thenReturn(List.of(t1, t2));

        useCase.executar("abc", request);

        verify(t1).concluirTransferencia();
        verify(t2).concluirTransferencia();
    }

    @Test
    void deveLancarExcecaoQuandoOrigemEDestinoForemIguais() {

        TransferenciaRequestDTO request =
                new TransferenciaRequestDTO(
                        1L,
                        1L,
                        new BigDecimal("10.00")
                );

        RegraNegocioException exception =
                assertThrows(
                        RegraNegocioException.class,
                        () -> useCase.executar("abc", request)
                );

        assertEquals(
                "Conta de origem e destino devem ser diferentes",
                exception.getMessage()
        );

        verifyNoInteractions(
                buscarContasComLockUseCase,
                buscarTransferenciaUseCase,
                registrarTransferenciasUseCase,
                contaRepository
        );
    }

    @Test
    void deveLancarExcecaoQuandoSaldoForInsuficiente() {

        TransferenciaRequestDTO request =
                new TransferenciaRequestDTO(
                        1L,
                        2L,
                        new BigDecimal("200.00")
                );

        when(buscarContasComLockUseCase.executar(request))
                .thenReturn(List.of(contaOrigem, contaDestino));

        when(buscarTransferenciaUseCase.executar(
                anyString(),
                eq(contaOrigem),
                eq(contaDestino)))
                .thenReturn(Collections.emptyList());

        when(registrarTransferenciasUseCase.executar(
                anyString(),
                any(),
                eq(contaOrigem),
                eq(contaDestino)))
                .thenReturn(Collections.emptyList());

        assertThrows(
                RegraNegocioException.class,
                () -> useCase.executar("abc", request)
        );

        verify(contaRepository, never()).save(any());
        verify(enviarNotificacaoUseCase, never())
                .executar(any(), any(), any());
    }

    @Test
    void naoDeveProcessarTransferenciaDuplicada() {

        TransferenciaRequestDTO request =
                new TransferenciaRequestDTO(
                        1L,
                        2L,
                        new BigDecimal("10.00")
                );

        Transferencia transferenciaExistente =
                mock(Transferencia.class);

        when(buscarContasComLockUseCase.executar(request))
                .thenReturn(List.of(contaOrigem, contaDestino));

        when(buscarTransferenciaUseCase.executar(
                "abc",
                contaOrigem,
                contaDestino))
                .thenReturn(List.of(transferenciaExistente));

        TransacaoDuplicadaException exception =
                assertThrows(
                        TransacaoDuplicadaException.class,
                        () -> useCase.executar("abc", request)
                );

        assertEquals(
                "Transferência duplicada",
                exception.getMessage()
        );

        verifyNoInteractions(
                registrarTransferenciasUseCase,
                contaRepository,
                enviarNotificacaoUseCase
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaOrigemNaoForEncontrada() {

        TransferenciaRequestDTO request =
                new TransferenciaRequestDTO(
                        1L,
                        2L,
                        new BigDecimal("10.00")
                );

        when(buscarContasComLockUseCase.executar(request))
                .thenReturn(List.of(contaDestino));

        RegraNegocioException exception =
                assertThrows(
                        RegraNegocioException.class,
                        () -> useCase.executar("abc", request)
                );

        assertEquals(
                "Conta com ID 1 nao encontrada",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoContaDestinoNaoForEncontrada() {

        TransferenciaRequestDTO request =
                new TransferenciaRequestDTO(
                        1L,
                        2L,
                        new BigDecimal("10.00")
                );

        when(buscarContasComLockUseCase.executar(request))
                .thenReturn(List.of(contaOrigem));

        RegraNegocioException exception =
                assertThrows(
                        RegraNegocioException.class,
                        () -> useCase.executar("abc", request)
                );

        assertEquals(
                "Conta com ID 2 nao encontrada",
                exception.getMessage()
        );
    }
}