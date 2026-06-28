package com.factor.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "style_factor_value", schema = "biz_factor")
public class StyleFactorValueEntity {

    @Id @Column(length = 64)
    private String id;

    @Column(name = "fund_code", length = 32, nullable = false)
    private String fundCode;

    @Column(name = "style_factor_id", length = 64, nullable = false)
    private String styleFactorId;

    @Column(name = "data_date", nullable = false)
    private LocalDate dataDate;

    @Column(precision = 24, scale = 8)
    private BigDecimal value;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    public StyleFactorValueEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }
    public String getStyleFactorId() { return styleFactorId; }
    public void setStyleFactorId(String styleFactorId) { this.styleFactorId = styleFactorId; }
    public LocalDate getDataDate() { return dataDate; }
    public void setDataDate(LocalDate dataDate) { this.dataDate = dataDate; }
    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }
    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
