package com.bt.nextgen.serviceops.repository;

/**
 * Created by l069679 on 9/02/2017.
 */
public interface GcmAuditRepository {

    public void logAuditEntry(String userId, String reqType, String reqMsg);
}
