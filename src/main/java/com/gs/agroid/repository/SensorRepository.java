package com.gs.agroid.repository;

import com.gs.agroid.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    @EntityGraph(attributePaths = {"propriedade"})
    List<Sensor> findByPropriedadeId(Long propriedadeId);

    @EntityGraph(attributePaths = {"propriedade"})
    Page<Sensor> findAll(Pageable pageable);
}
