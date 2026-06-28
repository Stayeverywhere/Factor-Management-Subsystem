package com.factor.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "ak_fund_profile", schema = "biz_factor")
public class FundProfileEntity {

    @Id
    @Column(name = "fund_code", length = 32, nullable = false)
    private String fundCode;

    @Column(name = "fund_name", length = 255, nullable = false)
    private String fundName;

    @Column(name = "fund_type", length = 128)
    private String fundType;

    @Column(name = "company_name", length = 255)
    private String companyName;

    @Column(name = "manager_name", length = 255)
    private String managerName;

    @Column(name = "issue_date")
    private LocalDate issueDate;

    @Column(name = "setup_date")
    private LocalDate setupDate;

    @Column(name = "fund_size", length = 64)
    private String fundSize;

    @Column(name = "fee_rate", length = 64)
    private String feeRate;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "source_system", length = 64, nullable = false)
    private String sourceSystem;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public FundProfileEntity() {}

    public String getFundCode() { return fundCode; }
    public void setFundCode(String fundCode) { this.fundCode = fundCode; }
    public String getFundName() { return fundName; }
    public void setFundName(String fundName) { this.fundName = fundName; }
    public String getFundType() { return fundType; }
    public void setFundType(String fundType) { this.fundType = fundType; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }
    public LocalDate getSetupDate() { return setupDate; }
    public void setSetupDate(LocalDate setupDate) { this.setupDate = setupDate; }
    public String getFundSize() { return fundSize; }
    public void setFundSize(String fundSize) { this.fundSize = fundSize; }
    public String getFeeRate() { return feeRate; }
    public void setFeeRate(String feeRate) { this.feeRate = feeRate; }
    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }
    public String getSourceSystem() { return sourceSystem; }
    public void setSourceSystem(String sourceSystem) { this.sourceSystem = sourceSystem; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
