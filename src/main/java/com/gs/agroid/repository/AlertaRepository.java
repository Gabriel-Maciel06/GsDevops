package com.gs.agroid.repository;

import com.gs.agroid.model.Alerta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertaRepository extends JpaRepository<Alerta, Long> {
    @EntityGraph(attributePaths = {"propriedade"})
    List<Alerta> findByPropriedadeIdOrderByTimestampDesc(Long propriedadeId);

    @EntityGraph(attributePaths = {"propriedade"})
    Page<Alerta> findByPropriedadeIdOrderByTimestampDesc(Long propriedadeId, Pageable pageable);

    @EntityGraph(attributePaths = {"propriedade"})
    Page<Alerta> findAll(Pageable pageable);
}
