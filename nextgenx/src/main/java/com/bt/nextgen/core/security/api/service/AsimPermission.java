package com.bt.nextgen.core.security.api.service;

import com.bt.nextgen.service.avaloq.userinformation.JobRoleType;

/**
 * ASIM permission.
 * <p>
 * ASIM - Adviser Setup Investor Managed.
 */
public interface AsimPermission {
    /**
     * Override value for ASIM user depending on whether the user has specified job role types.
     *
     * @param value             value to override.
     * @param valueOverride     if the override is met, return this value.
     * @param includeRoleTypes  {@code true} if the override should happen when the user has the specified
     *                          {@code jobRoleTypes}, {@code false} otherwise.
     * @param jobRoleTypes      List of job role types to check.
     *
     * @return  {@code valueOverride} if the criteria is met, {@code value} otherwise.
     */
    boolean overrideValue(boolean value, boolean valueOverride, boolean includeRoleTypes,
                          JobRoleType... jobRoleTypes);
}
