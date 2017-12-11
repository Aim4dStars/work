/**
 * 
 */
package com.bt.nextgen.service.gesb.createindividualip.v5;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.Individual;

/**
 * @author L081050
 */
public class CreateIndvIPRequest {
    public Individual getIndividual() {
        return individual;
    }

    public void setIndividual(Individual individual) {
        this.individual = individual;
    }

    private Individual individual;
}