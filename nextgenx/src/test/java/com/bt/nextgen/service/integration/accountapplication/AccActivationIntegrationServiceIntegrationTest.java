package com.bt.nextgen.service.integration.accountapplication;

import java.util.ArrayList;
import java.util.List;

import com.btfin.panorama.core.security.profile.UserProfileService;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.accountactivation.AccountStructure;
import com.bt.nextgen.service.avaloq.accountactivation.ApplicationIdentifierImpl;
import com.btfin.panorama.core.security.avaloq.accountactivation.ApplicationStatus;
import com.bt.nextgen.service.avaloq.accountactivation.ApprovalType;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import com.bt.nextgen.service.integration.accountactivation.AccActivationIntegrationService;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.accountactivation.ApplicationIdentifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class AccActivationIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest {
    @Autowired
    AccActivationIntegrationService accActService;
    @Autowired
    UserProfileService userProfileService;

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testAccountApplicationService() throws Exception {
        ArrayList<ApplicationIdentifier> docIdList = new ArrayList<>();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ApplicationIdentifier identifier = new ApplicationIdentifierImpl();
        identifier.setDocId("227362");
        docIdList.add(identifier);

        List<ApplicationDocument> applications = accActService.loadAccApplicationForApplicationId(docIdList, userProfileService.getActiveProfile().getJobRole(),userProfileService.getActiveProfile().getClientKey(),serviceErrors);
        assertNotNull(applications);

        //Application related details
        assertEquals("227362", applications.get(0).getAppNumber());
        assertEquals("319", applications.get(0).getAppLastUpdatedBy());
        assertEquals("319", applications.get(0).getAppStartedBy());
        assertEquals(ApplicationStatus.PEND_ACCEPT, applications.get(0).getAppState());
        assertEquals("29 Sep 2014 14:00:00 GMT", applications.get(0).getAppSubmitDate().toGMTString());
        assertEquals("319", applications.get(0).getChangeReqByPersonId());
        assertEquals("29 Sep 2014 14:00:00 GMT", applications.get(0).getChangeReqDate().toGMTString());
        assertEquals(ApprovalType.ONLINE, applications.get(0).getApprovalType());

        //Person related details
        assertEquals("72506", applications.get(0).getPersonDetails().get(0).getClientKey().getId());
        assertEquals(false, applications.get(0).getPersonDetails().get(0).isHasApprovedTnC());
        assertEquals(true, applications.get(0).getPersonDetails().get(0).isRegisteredOnline());
        assertEquals(false, applications.get(0).getPersonDetails().get(0).isHasToAcceptTnC());
        assertEquals(PersonRelationship.AO.name(), applications.get(0).getPersonDetails().get(0).getPersonRel().name());
        assertEquals(applications.get(0).getPersonDetails().get(0).getFormerNames().size(), 1);
        assertEquals(applications.get(0).getPersonDetails().get(0).getFormerNames().get(0).getFormerName(), "Former Hasad");

        //Multi Investor details
        assertEquals("72505", applications.get(0).getPersonDetails().get(1).getClientKey().getId());
        assertEquals(false, applications.get(0).getPersonDetails().get(1).isHasApprovedTnC());
        assertEquals(false, applications.get(0).getPersonDetails().get(1).isRegisteredOnline());
        assertEquals(false, applications.get(0).getPersonDetails().get(1).isHasToAcceptTnC());
        assertEquals(PersonRelationship.TRUSTEE.name(), applications.get(0).getPersonDetails().get(1).getPersonRel().name());

        assertEquals("72504", applications.get(0).getPersonDetails().get(2).getClientKey().getId());
        assertEquals(true, applications.get(0).getPersonDetails().get(2).isHasApprovedTnC());
        assertEquals(false, applications.get(0).getPersonDetails().get(2).isRegisteredOnline());
        assertEquals(true, applications.get(0).getPersonDetails().get(2).isHasToAcceptTnC());
        assertEquals(PersonRelationship.DIRECTOR.name(), applications.get(0).getPersonDetails().get(2).getPersonRel().name());

        assertEquals("72502", applications.get(0).getPersonDetails().get(3).getClientKey().getId());
        assertEquals(true, applications.get(0).getPersonDetails().get(3).isHasApprovedTnC());
        assertEquals(false, applications.get(0).getPersonDetails().get(3).isRegisteredOnline());
        assertEquals(true, applications.get(0).getPersonDetails().get(3).isHasToAcceptTnC());
        assertEquals(PersonRelationship.BENEFICIARY.name(), applications.get(0).getPersonDetails().get(3).getPersonRel().name());

        assertEquals("72503", applications.get(0).getPersonDetails().get(4).getClientKey().getId());
        assertEquals(true, applications.get(0).getPersonDetails().get(4).isHasApprovedTnC());
        assertEquals(false, applications.get(0).getPersonDetails().get(4).isRegisteredOnline());
        assertEquals(true, applications.get(0).getPersonDetails().get(4).isHasToAcceptTnC());
        assertEquals(PersonRelationship.BENEFICIARY.name(), applications.get(0).getPersonDetails().get(4).getPersonRel().name());

        //Bp related details
        assertEquals("72510", applications.get(0).getPortfolio().get(0).getPortfolioId());
        assertEquals("9 Sep 2014 14:00:00 GMT", applications.get(0).getPortfolio().get(0).getSignDate().toGMTString());
        assertEquals(true, applications.get(0).getPortfolio().get(0).isBpOpen());
        assertEquals("Deepshikha", applications.get(0).getPortfolio().get(0).getBpPrimaryContactFirstName());
        assertEquals("Singh", applications.get(0).getPortfolio().get(0).getBpPrimaryContactLastName());
        assertEquals("0414222333", applications.get(0).getPortfolio().get(0).getBpPrimaryContactPhoneNr());
        assertEquals(AccountStructure.T.getName(), applications.get(0).getPortfolio().get(0).getAccountType().getName());
    }

    @Ignore
    @SecureTestContext(username = "explode", customerId = "201101101")
    @Test
    public void testLoadWrapAccountContainersError() {
        ArrayList<ApplicationIdentifier> docIdList = new ArrayList<>();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ApplicationIdentifier identifier = new ApplicationIdentifierImpl();
        identifier.setDocId("227362");
        docIdList.add(identifier);
        List<ApplicationDocument> applications = accActService.loadAccApplicationForApplicationId(docIdList,null,null, serviceErrors);
        MatcherAssert.assertThat(serviceErrors.hasErrors(), Is.is(true));
    }

    @SecureTestContext(authorities = {"ADVISER"})
    @Test
    public void testAccountApplicationService_withNoDocId() throws Exception {
        ArrayList<ApplicationIdentifier> docIdList = new ArrayList<>();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        ApplicationIdentifier identifier = new ApplicationIdentifierImpl();
        identifier.setDocId("");
        docIdList.add(identifier);

        List<ApplicationDocument> applications = accActService.loadAccApplicationForApplicationId(docIdList, userProfileService.getActiveProfile().getJobRole(),userProfileService.getActiveProfile().getClientKey(),serviceErrors);
        assertEquals(applications.size(), 0);
    }
}
