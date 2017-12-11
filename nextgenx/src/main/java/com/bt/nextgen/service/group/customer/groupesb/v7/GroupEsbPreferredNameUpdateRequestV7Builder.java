package com.bt.nextgen.service.group.customer.groupesb.v7;

import au.com.westpac.gn.common.xsd.commontypes.v3.BooleanENUM;
import au.com.westpac.gn.common.xsd.identifiers.v1.IdentificationScheme;
import au.com.westpac.gn.common.xsd.identifiers.v1.InvolvedPartyIdentifier;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.Action;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.AlternateName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.Individual;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.IndividualName;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.MaintenanceAuditContext;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.ModifyIndividualCustomerRequest;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.modifyindividualcustomer.v2.svc0338.ObjectFactory;
import au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.RetrieveDetailsAndArrangementRelationshipsForIPsResponse;
import com.bt.nextgen.core.type.DateUtil;
import com.bt.nextgen.service.group.customer.groupesb.CustomerData;

import java.util.Date;
import java.util.List;

@SuppressWarnings("squid:S1200")
@Deprecated
public final class GroupEsbPreferredNameUpdateRequestV7Builder
{
    private GroupEsbPreferredNameUpdateRequestV7Builder(){}
    public static ModifyIndividualCustomerRequest createModifyIndividualCustomerRequest(CustomerData updatedData, RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse) {

        final au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory factory = new au.com.westpac.gn.common.xsd.identifiers.v1.ObjectFactory();
        InvolvedPartyIdentifier involvedPartyIdentifier  = factory.createInvolvedPartyIdentifier();
        involvedPartyIdentifier.setIdentificationScheme(IdentificationScheme.CIS_KEY);
        involvedPartyIdentifier.setInvolvedPartyId(updatedData.getRequest().getCISKey().getId());

        ObjectFactory of = new ObjectFactory();

        MaintenanceAuditContext auditContext = of.createMaintenanceAuditContext();
        auditContext.setVersionNumber(cachedResponse.getIndividual().getAuditContext().getVersionNumber());
        Individual individual= of.createIndividual();
        IndividualName individualName = of.createIndividualName();
        populateAlternateNames(updatedData,cachedResponse,individual,individualName,of);
        individual.setInvolvedPartyIdentifier(involvedPartyIdentifier);
        individual.setHasForName(individualName);
        individual.setAuditContext(auditContext);
        ModifyIndividualCustomerRequest request = of.createModifyIndividualCustomerRequest();
        request.setIndividual(individual);

        if(individualName.getHasAlternateName().isEmpty())
            return null;

        return request;
    }

    @SuppressWarnings("squid:MethodCyclomaticComplexity")
    protected static void populateAlternateNames(CustomerData customerData,RetrieveDetailsAndArrangementRelationshipsForIPsResponse cachedResponse,
                                                  Individual individual,IndividualName individualName,ObjectFactory of) {
        boolean isPreferredNameBlank=false;
        String preferredName = "";
        au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.AlternateName cachedName = null;
        List<au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.AlternateName>
                names = cachedResponse.getIndividual().getHasForName().getHasAlternateName();

        if(!names.isEmpty()) {
            for (au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.AlternateName name : names) {
                if ("Y".equals(name.getIsPreferred().toString()) && name.getAuditContext().isIsActive()) {
                    preferredName = name.getName();
                    cachedName = name;
                }
            }
        }

        if (preferredName.isEmpty())
            isPreferredNameBlank = true;

        /*Logic for Requested Action and timestamps*/
        if (isPreferredNameBlank && !customerData.getPreferredName().isEmpty()) {
            getNewAlternateName(customerData, individualName, of);
            individual.setNoAlternateName(BooleanENUM.N);
        } else if (!isPreferredNameBlank && customerData.getPreferredName().isEmpty()) {
            AlternateName alternateName = getEndAlternateName(of, cachedName);
            alternateName.setRequestedAction(Action.DELETE);
            if (names.size() == 1) {
                individual.setNoAlternateName(BooleanENUM.Y);
            }
            else
                individual.setNoAlternateName(BooleanENUM.N);
            individualName.getHasAlternateName().add(alternateName);
        } else if (!preferredName.equalsIgnoreCase(customerData.getPreferredName())) {
            //Modify the existing name
            AlternateName alternateName = getEndAlternateName(of, cachedName);
            alternateName.setRequestedAction(Action.MODIFY);
            individualName.getHasAlternateName().add(alternateName);
            //Add new name
            getNewAlternateName(customerData,individualName,of);
            individual.setNoAlternateName(BooleanENUM.N);
        }
    }

    private static AlternateName getEndAlternateName(ObjectFactory of, au.com.westpac.gn.involvedpartymanagement.services.involvedpartymanagement.xsd.retrievedetailsandarrangementrelationshipsforips.v7.svc0258.AlternateName cachedName) {
        AlternateName alternateName = of.createAlternateName();
        alternateName.setIsPreferred("N");
        alternateName.setName(cachedName.getName());
        alternateName.setStartDate(cachedName.getStartDate());
        alternateName.setEndDate(DateUtil.convertDateInGregorianCalendar(new Date()));
        MaintenanceAuditContext nameAuditContext = of.createMaintenanceAuditContext();
        nameAuditContext.setLastUpdateTimestamp(of.createMaintenanceAuditContextLastUpdateTimestamp(DateUtil.convertDateInGregorianCalendar(new Date())));
        nameAuditContext.setVersionNumber(cachedName.getAuditContext().getVersionNumber());
        alternateName.setAuditContext(nameAuditContext);
        return alternateName;
    }

    private static void getNewAlternateName(CustomerData customerData, IndividualName individualName, ObjectFactory of) {
        AlternateName alternateName = of.createAlternateName();
        alternateName.setRequestedAction(Action.ADD);
        alternateName.setName(customerData.getPreferredName());
        alternateName.setIsPreferred("Y");
        alternateName.setStartDate(DateUtil.convertDateInGregorianCalendar(new Date()));
        individualName.getHasAlternateName().add(alternateName);
    }

}
