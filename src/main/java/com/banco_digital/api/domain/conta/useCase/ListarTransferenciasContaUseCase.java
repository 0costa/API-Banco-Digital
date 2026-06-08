package com.banco_digital.api.domain.conta.useCase;

import com.banco_digital.api.domain.transferencia.TransferenciaRepository;
import com.banco_digital.api.shared.dto.TransferenciaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarTransferenciasContaUseCase {

    private final TransferenciaRepository transferenciaRepository;

    public List<TransferenciaResponseDTO> executar(Long id){
        return transferenciaRepository.listaTransferenciasDaConta(id);
    }
}
