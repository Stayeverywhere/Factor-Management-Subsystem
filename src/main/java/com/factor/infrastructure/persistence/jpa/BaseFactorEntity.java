package com.factor.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "base_factor", schema = "biz_factor")
public class BaseFactorEntity {

    @Id @Column(length = 64)
    private String id;

    @Column(length = 64, nullable = false)
    private String code;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(name = "category_id", length = 64)
    private String categoryId;

    @Column(name = "data_type", length = 32)
    private String dataType;

    @Column(length = 32)
    private String unit;

    @Column(name = "update_frequency", length = 32)
    private String updateFrequency;

    @Column(name = "data_source", length = 64)
    private String dataSource;

    @Column(name = "fetch_logic")
    private String fetchLogic;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean derivable = true;

    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BaseFactorEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getUpdateFrequency() { return updateFrequency; }
    public void setUpdateFrequency(String updateFrequency) { this.updateFrequency = updateFrequency; }
    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
    public String getFetchLogic() { return fetchLogic; }
    public void setFetchLogic(String fetchLogic) { this.fetchLogic = fetchLogic; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isDerivable() { return derivable; }
    public void setDerivable(boolean derivable) { this.derivable = derivable; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
