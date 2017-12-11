package com.bt.nextgen.core.repository;

import com.bt.nextgen.draftaccount.repository.ClientApplication;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by F058391 on 19/11/2015.
 */
@Entity
@Table(name = "CIS_KEY_CLIENT_APPLICATION")
public class CisKeyClientApplication {

    public CisKeyClientApplication() {
    }

    public CisKeyClientApplication(String cisKey, ClientApplication clientApplication) {
        this.cisClientApplicationId = new CisClientApplicationId(cisKey, clientApplication);
    }

    @EmbeddedId
    private CisClientApplicationId cisClientApplicationId;

    public CisClientApplicationId getCisClientApplicationId() {
        return cisClientApplicationId;
    }

    @Embeddable
    static class CisClientApplicationId implements Serializable {

        public CisClientApplicationId() {

        }

        public CisClientApplicationId(String cisKey, ClientApplication clientApplication) {
            this.cisKey = cisKey;
            this.clientApplication = clientApplication;
        }

        @Column(name = "CIS_KEY")
        private String cisKey;

        @ManyToOne(cascade = CascadeType.REFRESH)
        @JoinColumn(name = "CLIENT_APPLICATION_ID", referencedColumnName = "CLIENT_APPLICATION_ID")
        private ClientApplication clientApplication;


        public String getCisKey() {
            return cisKey;
        }

        public void setCisKey(String cisKey) {
            this.cisKey = cisKey;
        }

        public ClientApplication getClientApplication() {
            return clientApplication;
        }

        public void setClientApplication(ClientApplication clientApplication) {
            this.clientApplication = clientApplication;
        }
    }


}
