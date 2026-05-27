package com.gs.agroid.repository;

import com.gs.agroid.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    List<Alerta> findByPropriedadeIdOrderByTimestampDesc(Long propriedadeId);
    Page<Alerta> findByPropriedadeIdOrderByTimestampDesc(Long propriedadeId, Pageable pageable);
}
