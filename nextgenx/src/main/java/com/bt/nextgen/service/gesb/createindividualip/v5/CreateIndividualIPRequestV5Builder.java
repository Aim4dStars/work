/**
 * 
 */
package com.bt.nextgen.service.gesb.createindividualip.v5;

import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.CreateIndividualIPRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.createindividualip.v5.svc0336.ObjectFactory;


/**
 * @author L081050
 *
 */
public class CreateIndividualIPRequestV5Builder {
    private CreateIndividualIPRequestV5Builder(){
        
    }
    public static CreateIndividualIPRequest createCreateIndividualIPRequest(
            CreateIndvIPRequest input) {
        final ObjectFactory factory =  new ObjectFactory();
        CreateIndividualIPRequest req =factory.createCreateIndividualIPRequest() ;
        req.setIndividual(input.getIndividual());
        return req;
    }
}