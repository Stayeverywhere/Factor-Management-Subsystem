package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DerivativeFactorValueJpaRepository extends JpaRepository<DerivativeFactorValueEntity, String> {
    List<DerivativeFactorValueEntity> findByFundCodeAndDerivativeFactorId(String fundCode, String derivativeFactorId, Pageable pageable);

    @Query(value = "SELECT d.* FROM derivative_factor_value d INNER JOIN (SELECT MAX(id) AS id FROM derivative_factor_value " +
           "WHERE derivative_factor_id = :fid GROUP BY data_date ORDER BY MAX(data_date) DESC LIMIT :lim) t ON d.id = t.id " +
           "ORDER BY d.data_date DESC", nativeQuery = true)
    List<DerivativeFactorValueEntity> findDistinctByDerivativeFactorId(@Param("fid") String factorId, @Param("lim") int limit);

    @Query(value = "SELECT d.* FROM derivative_factor_value d INNER JOIN (SELECT MAX(id) AS id FROM derivative_factor_value " +
           "WHERE derivative_factor_id = :fid AND data_date BETWEEN :start AND :end GROUP BY data_date " +
           "ORDER BY MAX(data_date) DESC LIMIT :lim) t ON d.id = t.id ORDER BY d.data_date DESC", nativeQuery = true)
    List<DerivativeFactorValueEntity> findDistinctByDerivativeFactorIdAndDataDateBetween(@Param("fid") String factorId,
            @Param("start") LocalDate start, @Param("end") LocalDate end, @Param("lim") int limit);
}
