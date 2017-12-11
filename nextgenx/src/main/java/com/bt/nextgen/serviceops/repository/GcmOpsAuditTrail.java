package com.bt.nextgen.serviceops.repository;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * Created by l069679 on 9/02/2017.
 */
@Entity
@Table(name = "GCM_AUDIT_TRAIL")
public class GcmOpsAuditTrail implements Serializable {
    @Id
    @Column(name = "AUDIT_LOG_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUDIT_LOG_SEQ")
    @SequenceGenerator(name = "AUDIT_LOG_SEQ", sequenceName = "AUDIT_LOG_SEQ", allocationSize = 1)
    private Long auditId;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;

    @Column(name = "SILO")
    private String silo;

    @Column(name = "REQ_TYPE")
    private String reqType;

    @Column(name = "REQ_MSG")
    private String reqMsg;

    public GcmOpsAuditTrail() {
        //Default constructor for GcmOpsAuditTrail.
    }

    public GcmOpsAuditTrail(String userId, String silo, String reqType, String reqMsg) {
        this.userId = userId;
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        this.silo = silo;
        this.reqType = reqType;
        this.reqMsg = reqMsg;
    }

}
