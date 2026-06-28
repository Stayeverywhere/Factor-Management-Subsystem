package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface StyleFactorValueJpaRepository extends JpaRepository<StyleFactorValueEntity, String> {
    List<StyleFactorValueEntity> findByFundCodeAndStyleFactorId(String fundCode, String styleFactorId, Pageable pageable);

    @Query(value = "SELECT s.* FROM style_factor_value s INNER JOIN (SELECT MAX(id) AS id FROM style_factor_value " +
           "WHERE style_factor_id = :fid GROUP BY data_date ORDER BY MAX(data_date) DESC LIMIT :lim) t ON s.id = t.id " +
           "ORDER BY s.data_date DESC", nativeQuery = true)
    List<StyleFactorValueEntity> findDistinctByStyleFactorId(@Param("fid") String factorId, @Param("lim") int limit);

    @Query(value = "SELECT s.* FROM style_factor_value s INNER JOIN (SELECT MAX(id) AS id FROM style_factor_value " +
           "WHERE style_factor_id = :fid AND data_date BETWEEN :start AND :end GROUP BY data_date " +
           "ORDER BY MAX(data_date) DESC LIMIT :lim) t ON s.id = t.id ORDER BY s.data_date DESC", nativeQuery = true)
    List<StyleFactorValueEntity> findDistinctByStyleFactorIdAndDataDateBetween(@Param("fid") String factorId,
            @Param("start") LocalDate start, @Param("end") LocalDate end, @Param("lim") int limit);
}
