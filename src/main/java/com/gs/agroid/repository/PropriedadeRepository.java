package com.gs.agroid.repository;

import com.gs.agroid.model.Propriedade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

@Repository
public interface PropriedadeRepository extends JpaRepository<Propriedade, Long> {
    @EntityGraph(attributePaths = {"usuario"})
    List<Propriedade> findByUsuarioId(Long usuarioId);

    @EntityGraph(attributePaths = {"usuario"})
    Page<Propriedade> findAll(Pageable pageable);
}
