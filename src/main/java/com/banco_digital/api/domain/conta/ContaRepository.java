package com.banco_digital.api.domain.conta;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
       select c
       from Conta c
       where c.id in (:ids)
       order by c.id
       """)
    List<Conta> buscarComLock(List<Long> ids);
}
