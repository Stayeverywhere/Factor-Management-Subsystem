package com.factor.infrastructure.persistence.jpa;

import com.factor.domain.factor.BaseFactorValue;
import com.factor.domain.factor.repository.BaseFactorValueRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
@Profile("jpa")
@Repository
public class JpaBaseFactorValueRepository implements BaseFactorValueRepository {

    private final BaseFactorValueJpaRepository jpa;

    public JpaBaseFactorValueRepository(BaseFactorValueJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<BaseFactorValue> query(String fundCode, String factorId) {
        // 最多返回最近 31 条（一个月数据）
        PageRequest limit = PageRequest.of(0, 31);
        List<BaseFactorValueEntity> entities;
        if (fundCode != null && !fundCode.isBlank()) {
            entities = jpa.findByFundCodeAndBaseFactorId(fundCode, factorId, limit);
        } else {
            entities = jpa.findByBaseFactorId(factorId, limit);
        }
        return entities.stream().map(this::toDomain).toList();
    }

    public BaseFactorValue save(BaseFactorValue value) {
        BaseFactorValueEntity entity = toEntity(value);
        if (entity.getId() == null) entity.setId(UUID.randomUUID().toString());
        entity.setUpdatedAt(LocalDateTime.now());
        return toDomain(jpa.save(entity));
    }

    private BaseFactorValue toDomain(BaseFactorValueEntity e) {
        return new BaseFactorValue(e.getId(), e.getFundCode(), e.getBaseFactorId(),
                e.getDataDate(), e.getValue(), e.getUpdatedAt());
    }

    private BaseFactorValueEntity toEntity(BaseFactorValue v) {
        BaseFactorValueEntity e = new BaseFactorValueEntity();
        e.setId(v.id()); e.setFundCode(v.fundCode()); e.setBaseFactorId(v.baseFactorId());
        e.setDataDate(v.dataDate()); e.setValue(v.value());
        return e;
    }
}
