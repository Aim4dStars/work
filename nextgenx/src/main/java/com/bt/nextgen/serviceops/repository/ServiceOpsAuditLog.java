/**
 * 
 */
package com.bt.nextgen.serviceops.repository;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author l081050
 *
 */
@Entity
@Table(name = "SERVICEOPS_AUDIT_LOG")
public class ServiceOpsAuditLog {
    public Long getAuditId() {
        return auditId;
    }

    public void setAuditId(Long auditId) {
        this.auditId = auditId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Id
    @Column(name = "LOG_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SERVICEOPS_LOG_SEQ")
    @SequenceGenerator(name = "SERVICEOPS_LOG_SEQ", sequenceName = "SERVICEOPS_LOG_SEQ", allocationSize = 1)
    private Long auditId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;

    @Column(name = "ACTION")
    private String action;

    

    @Column(name = "MESSAGE")
    private String message;

    public ServiceOpsAuditLog() {
        //Default constructor for GcmOpsAuditTrail.
    }

    public ServiceOpsAuditLog(String userId, String action,  String message) {
        this.userId = userId;
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        this.message = message;
        this.action = action;
    }

}
