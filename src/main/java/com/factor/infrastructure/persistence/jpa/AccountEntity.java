package com.factor.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "account", schema = "biz_factor")
public class AccountEntity {

    @Id @Column(length = 64)
    private String id;

    @Column(length = 128, nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", length = 256, nullable = false)
    private String passwordHash;

    @Column(name = "display_name", length = 255)
    private String displayName;

    @Column(name = "user_type", length = 32, nullable = false)
    private String userType;

    @Column(name = "role_id", length = 64)
    private String roleId;

    @Column(name = "tenant_id", length = 64)
    private String tenantId;

    @Column(nullable = false)
    private boolean enabled = true;

    public AccountEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getRoleId() { return roleId; }
    public void setRoleId(String roleId) { this.roleId = roleId; }
    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
