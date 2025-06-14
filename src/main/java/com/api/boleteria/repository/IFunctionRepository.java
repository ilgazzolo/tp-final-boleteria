package com.api.boleteria.repository;

import com.api.boleteria.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface IFunctionRepository extends JpaRepository<Function, Long> {

    boolean existsByCinemaIdAndDate(Long cinemaId, LocalDateTime date);
    List<Function> findByCinemaId(Long id);

}
