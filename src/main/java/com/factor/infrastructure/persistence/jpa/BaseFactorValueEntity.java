package com.factor.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "base_factor_value", schema = "biz_factor")
public class BaseFactorValueEntity {

    @Id @Column(length = 64)
    private String id;

    @Column(name = "fund_code", length = 32, nullable = false)
    private String fundCode;

    @Column(name = "base_factor_id", length = 64, nullable = false)
    private String baseFactorId;

    @Column(name = "data_date", nullable = false)
    private LocalDate dataDate;

    @Column(precision = 24, scale = 8)
    private BigDecimal value;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BaseFactorValueEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }
    public String getBaseFactorId() { return baseFactorId; }
    public void setBaseFactorId(String baseFactorId) { this.baseFactorId = baseFactorId; }
    public LocalDate getDataDate() { return dataDate; }
    public void setDataDate(LocalDate dataDate) { this.dataDate = dataDate; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
