package com.bt.nextgen.serviceops.model;

import ns.btfin_com.party.v3_0.CustomerNoAllIssuerType;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by L069552 on 6/11/17.
 */
public class ProvisionMFARequestDataBuilder {


    private ProvisionMFARequestDataBuilder(){

    }

    public static ProvisionMFABuilder make(){

        return new ProvisionMFABuilder();
    }

    public static class ProvisionMFABuilder {

        private ProvisionMFARequestData provisionMFARequestData = null;

        ProvisionMFABuilder(){
            provisionMFARequestData = new ProvisionMFARequestData();
        }

        public ProvisionMFABuilder withPrimaryMobileNumber(String primaryMobileNumber){
            provisionMFARequestData.setPrimaryMobileNumber(primaryMobileNumber);
            return this;
        }

        public ProvisionMFABuilder withRole(String role){
            provisionMFARequestData.setRole(role);
            return this;
        }

        public ProvisionMFABuilder withCanonicalProductName(String canonicalProductName){
            provisionMFARequestData.setCanonicalProductName(canonicalProductName);
            return this;
        }

        public ProvisionMFABuilder withGcmId(String gcmId){
            provisionMFARequestData.setGcmId(gcmId);
            return this;
        }


        public ProvisionMFABuilder withCISKey(String cisKey){
            provisionMFARequestData.setCisKey(cisKey);
            return this;
        }

        public ProvisionMFABuilder withCustomerNumber(String customerNumber){
            provisionMFARequestData.setCustomerNumber(customerNumber);
            return this;
        }


        public ProvisionMFARequestData collect(){
            return provisionMFARequestData;
        }
    }
}