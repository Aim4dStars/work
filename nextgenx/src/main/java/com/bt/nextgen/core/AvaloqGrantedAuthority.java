package com.bt.nextgen.core;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.util.Assert;

public final class AvaloqGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String avaloqAuthority;

    public AvaloqGrantedAuthority(String role) {
        Assert.hasText(role, "A granted authority textual representation is required");
        this.avaloqAuthority = role;
    }

    public String getAuthority() {
        return avaloqAuthority;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof AvaloqGrantedAuthority) {
            return avaloqAuthority.equals(((AvaloqGrantedAuthority) obj).avaloqAuthority);
        }

        return false;
    }

    public int hashCode() {
        return this.avaloqAuthority.hashCode();
    }

    public String toString() {
        return this.avaloqAuthority;
    }
}
