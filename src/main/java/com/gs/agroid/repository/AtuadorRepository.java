package com.gs.agroid.repository;

import com.gs.agroid.model.Atuador;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AtuadorRepository extends JpaRepository<Atuador, Long> {
    @EntityGraph(attributePaths = {"propriedade"})
    List<Atuador> findByPropriedadeId(Long propriedadeId);

    @EntityGraph(attributePaths = {"propriedade"})
    Page<Atuador> findByPropriedadeId(Long propriedadeId, Pageable pageable);

    @EntityGraph(attributePaths = {"propriedade"})
    Page<Atuador> findAll(Pageable pageable);
}
