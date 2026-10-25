package com.school.violationsystem.security;

public class SecurityManager {
    public boolean canAccess(String roleName, String requiredRole) {
        return roleName != null && roleName.equalsIgnoreCase(requiredRole);
    }
}
