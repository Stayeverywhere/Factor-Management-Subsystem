package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BaseFactorValueJpaRepository extends JpaRepository<BaseFactorValueEntity, String> {
    List<BaseFactorValueEntity> findByFundCodeAndBaseFactorId(String fundCode, String baseFactorId, Pageable pageable);

    // 每日期只取1条：用子查询 GROUP BY data_date
    @Query(value = "SELECT b.* FROM base_factor_value b INNER JOIN (SELECT MAX(id) AS id FROM base_factor_value " +
           "WHERE base_factor_id = :fid GROUP BY data_date ORDER BY MAX(data_date) DESC LIMIT :lim) t ON b.id = t.id " +
           "ORDER BY b.data_date DESC", nativeQuery = true)
    List<BaseFactorValueEntity> findDistinctByBaseFactorId(@Param("fid") String baseFactorId, @Param("lim") int limit);

    @Query(value = "SELECT b.* FROM base_factor_value b INNER JOIN (SELECT MAX(id) AS id FROM base_factor_value " +
           "WHERE base_factor_id = :fid AND data_date BETWEEN :start AND :end GROUP BY data_date " +
           "ORDER BY MAX(data_date) DESC LIMIT :lim) t ON b.id = t.id ORDER BY b.data_date DESC", nativeQuery = true)
    List<BaseFactorValueEntity> findDistinctByBaseFactorIdAndDataDateBetween(@Param("fid") String baseFactorId,
            @Param("start") LocalDate start, @Param("end") LocalDate end, @Param("lim") int limit);
}
