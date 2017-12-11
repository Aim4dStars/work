package com.bt.nextgen.service.integration.staticrole;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StaticRoleIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest
{

    @Autowired
    StaticRoleIntegrationService service;


    
    @Test
    public void testFunctionalRoles() throws Exception
    {
        String profileId = "";
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        Map<String, List<FunctionalRole>> roles = service.loadStaticRoles(serviceErrors);
        assertNotNull(roles);
        		
        List<FunctionalRole> functionalRole = new ArrayList<>();
        functionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$BASIC_EXTL"));
        functionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_BASIC_MK_C"));
        functionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_CLT_PROFILE_UI_MNG"));
        functionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_CRM_ISSUE_UI_COMPLAINT_MNG"));
        functionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_MP_UI_RO"));
        functionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_MP_UI_UPL_INIT"));
        functionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_PRINT_EXPORT_ANY"));
        functionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_USER_UI_BASIC"));
  
        assertEquals(functionalRole.get(0), roles.get("$UR_INVST_MGR_BASIC").get(0));
        assertEquals(functionalRole.get(1), roles.get("$UR_INVST_MGR_BASIC").get(1));
        assertEquals(functionalRole.get(2), roles.get("$UR_INVST_MGR_BASIC").get(2));
        assertEquals(functionalRole.get(3), roles.get("$UR_INVST_MGR_BASIC").get(3));
        assertEquals(functionalRole.get(4), roles.get("$UR_INVST_MGR_BASIC").get(4));
        assertEquals(functionalRole.get(5), roles.get("$UR_INVST_MGR_BASIC").get(5));
        assertEquals(functionalRole.get(6), roles.get("$UR_INVST_MGR_BASIC").get(6));
        assertEquals(functionalRole.get(7), roles.get("$UR_INVST_MGR_BASIC").get(7));

        List<FunctionalRole> custrFunctionalRole = new ArrayList<>();
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$BASIC_EXTL"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$BASIC_IPS"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_ACC_ACT_UI_REQ"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_ACC_UI_REP"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_BASIC_MK_C"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_BILLER_PAYEE_UI_MNG"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_BPAY_UI_PAY_ANYONE_MNG"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_CLT_ONB_APPLN_UI_REQ"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_CLT_PROFILE_UI_MNG"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_CRM_ISSUE_UI_COMPLAINT_MNG"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_PAY_UI_LINKED_ACC_MNG"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_PERSON_MK"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_PERSON_UI_REQ"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_PRD_NEWS_UI_RO"));
        custrFunctionalRole.add(FunctionalRole.getFunctionalRoleFromAvaloqVal("$FR_USER_UI_BASIC"));
        
        assertEquals(custrFunctionalRole.get(0), roles.get("$UR_CUSTR").get(0));
        assertEquals(custrFunctionalRole.get(1), roles.get("$UR_CUSTR").get(1));
        assertEquals(custrFunctionalRole.get(2), roles.get("$UR_CUSTR").get(2));
        assertEquals(custrFunctionalRole.get(3), roles.get("$UR_CUSTR").get(3));
        assertEquals(custrFunctionalRole.get(4), roles.get("$UR_CUSTR").get(4));
        assertEquals(custrFunctionalRole.get(5), roles.get("$UR_CUSTR").get(5));
        assertEquals(custrFunctionalRole.get(6), roles.get("$UR_CUSTR").get(6));
        assertEquals(custrFunctionalRole.get(7), roles.get("$UR_CUSTR").get(7));
        assertEquals(custrFunctionalRole.get(8), roles.get("$UR_CUSTR").get(8));
        assertEquals(custrFunctionalRole.get(9), roles.get("$UR_CUSTR").get(9));
        assertEquals(custrFunctionalRole.get(10), roles.get("$UR_CUSTR").get(10));
        assertEquals(custrFunctionalRole.get(11), roles.get("$UR_CUSTR").get(11));
        assertEquals(custrFunctionalRole.get(12), roles.get("$UR_CUSTR").get(12));
        assertEquals(custrFunctionalRole.get(13), roles.get("$UR_CUSTR").get(13));
    }

    
    @Ignore
    @SecureTestContext(username = "explode")
    @Test
    public void testPayeeDetailsServiceError() throws Exception
    {
        String profileId = "";
        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        try {
            Map<String, List<FunctionalRole>> roles = service.loadStaticRoles(serviceErrors);
        }
        catch(Exception e)
        {

        }
        assertThat(serviceErrors.hasErrors(), Is.is(true));

    }
}
