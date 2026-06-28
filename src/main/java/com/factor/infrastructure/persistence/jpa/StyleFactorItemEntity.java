package com.factor.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "style_factor_item", schema = "biz_factor")
public class StyleFactorItemEntity {

    @Id @Column(length = 64)
    private String id;

    @Column(name = "style_factor_id", length = 64, nullable = false)
    private String styleFactorId;

    @Column(name = "derivative_factor_id", length = 64, nullable = false)
    private String derivativeFactorId;

    @Column(nullable = false, precision = 24, scale = 8)
    private BigDecimal weight;

    public StyleFactorItemEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStyleFactorId() { return styleFactorId; }
    public void setStyleFactorId(String styleFactorId) { this.styleFactorId = styleFactorId; }
    public String getDerivativeFactorId() { return derivativeFactorId; }
    public void setDerivativeFactorId(String derivativeFactorId) { this.derivativeFactorId = derivativeFactorId; }
    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
}
