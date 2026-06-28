package com.factor.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "factor_category", schema = "biz_factor")
public class FactorCategoryEntity {

    @Id @Column(length = 64)
    private String id;

    @Column(name = "parent_id", length = 64)
    private String parentId;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(name = "cat_level", nullable = false)
    private int catLevel = 1;

    @Column(name = "sort_no", nullable = false)
    private int sortNo;

    private String description;

    @Column(nullable = false)
    private boolean enabled = true;

    public FactorCategoryEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCatLevel() { return catLevel; }
    public void setCatLevel(int catLevel) { this.catLevel = catLevel; }
    public int getSortNo() { return sortNo; }
    public void setSortNo(int sortNo) { this.sortNo = sortNo; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
