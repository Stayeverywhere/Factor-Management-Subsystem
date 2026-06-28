package com.factor.infrastructure.persistence.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FundProfileJpaRepository extends JpaRepository<FundProfileEntity, String> {
}
