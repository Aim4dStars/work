package com.bt.nextgen.core.security.api.service;

public interface BasilPermissionService
{
    boolean hasBasilDocumentAccess(String accountId, String documentId);
}