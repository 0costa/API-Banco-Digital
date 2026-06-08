package com.banco_digital.api.domain.transferencia;

import com.banco_digital.api.shared.dto.TransferenciaResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferenciaRepository extends JpaRepository<Transferencia, Long> {
    @Query("SELECT t FROM Transferencia t WHERE t.chaveIdempotencia IN :chaves")
    List<Transferencia> findAllByChavesIdempotencia(@Param("chaves") List<String> chaves);

    @Query(
        """
        SELECT new com.banco_digital.api.shared.dto.TransferenciaResponseDTO(
            t.descricao,
            t.valor,
            t.createdAt
        )
        FROM Transferencia t
        WHERE t.ID = :id
        """
    )
    List<TransferenciaResponseDTO> listaTransferenciasDaConta(@Param("id") Long id);

}
