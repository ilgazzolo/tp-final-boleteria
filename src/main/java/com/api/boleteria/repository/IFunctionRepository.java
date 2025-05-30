package com.api.boleteria.repository;

import com.api.boleteria.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IFunctionRepository extends JpaRepository<Function, Long> {
}
