package com.bt.nextgen.core.security;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.List;

public class AvaloqFunctionalRolePermissionEvaluator implements PermissionEvaluator
{
    private final static String IS_NOT_EMULATING = "isNotEmulating";

    @Autowired
    UserProfileService userProfileService;

    @Override
    public boolean hasPermission(Authentication authentication, Object domainObject, Object permission)
    {
        return hasPermission(permission);
    }

    @Override public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType,
        Object permission)
    {
        return hasPermission(permission);
    }

    private boolean hasPermission(Object permission)
    {
        //TODO: check if user has accepted T&C's

        //Below has been added for compatibility with the old permission evaluator (AvaloqAclPermissionEvaluator)
        switch (permission.toString())
        {
            case "isValidAdviser":
                return true;

            case "isValidCashAccount":
                return true;

            case "isValidInvestor":
                return true;
        }
        List<String> permissions = Lambda.convert(((String) permission).split(","), new Converter<String, String>() {
            @Override
            public String convert(String s) {
                return s.trim();
            }
        });

        if(permissions.contains(IS_NOT_EMULATING) && userProfileService.isEmulating()){
            return false;
        }
        permissions.remove(IS_NOT_EMULATING);

        final List<FunctionalRole> functionalRoles = userProfileService.getActiveProfile().getFunctionalRoles();

        return Iterables.all(permissions, new Predicate<String>() {
            @Override
            public boolean apply(final String userPermission) {
                return Iterables.any(functionalRoles, new Predicate<FunctionalRole>() {
                    @Override
                    public boolean apply(FunctionalRole functionalRole) {
                        return FunctionalRole.valueOf(userPermission).equals(functionalRole);
                    }
                });
            }
        });
    }
}