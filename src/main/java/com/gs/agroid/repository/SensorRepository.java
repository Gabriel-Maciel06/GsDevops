package com.gs.agroid.repository;

import com.gs.agroid.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    List<Sensor> findByPropriedadeId(Long propriedadeId);
}
