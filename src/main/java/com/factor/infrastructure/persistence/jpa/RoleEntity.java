package com.factor.infrastructure.persistence.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role", schema = "biz_factor")
public class RoleEntity {

    @Id @Column(length = 64)
    private String id;

    @Column(length = 64, nullable = false, unique = true)
    private String code;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(name = "user_type", length = 32, nullable = false)
    private String userType;

    @Column(length = 32, nullable = false)
    private String scope;

    private String permissions;

    @Column(name = "built_in", nullable = false)
    private boolean builtIn = false;

    public RoleEntity() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public String getPermissions() { return permissions; }
    public void setPermissions(String permissions) { this.permissions = permissions; }
    public boolean isBuiltIn() { return builtIn; }
    public void setBuiltIn(boolean builtIn) { this.builtIn = builtIn; }
}
