package com.banco_digital.api.domain.conta.useCase;

import com.banco_digital.api.domain.conta.ContaRepository;
import com.banco_digital.api.shared.dto.ContaResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarContasBancariasUseCase {

    private final ContaRepository contaRepository;

    public List<ContaResponseDTO> executar(){
        return contaRepository.findAll()
                .stream()
                .map(ContaResponseDTO::from)
                .toList();
    }
}
