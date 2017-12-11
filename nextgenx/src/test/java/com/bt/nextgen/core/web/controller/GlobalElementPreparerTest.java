package com.bt.nextgen.core.web.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.tiles.AttributeContext;
import org.apache.tiles.context.TilesRequestContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.core.security.UserRole;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.web.model.ServiceOperator;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.integration.userinformation.JobPermission;
import com.btfin.panorama.core.security.integration.userinformation.UserInformationIntegrationService;
import com.btfin.panorama.core.security.integration.userprofile.JobProfile;
import com.bt.nextgen.web.controller.cash.util.Attribute;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GlobalElementPreparerTest {
    @InjectMocks
    private GlobalElementPreparer globalElementPreparer;

    @Mock
    private UserProfileService userProfileService;

    @Mock
    private UserInformationIntegrationService userInformationIntegrationService;

    @Mock
    private TilesRequestContext tilesContext;

    @Mock
    private AttributeContext attributeContext;

    @Before
    public void setUp() {
        Map<String, Object> modelMap = new HashMap<>();
        when(tilesContext.getRequestScope()).thenReturn(modelMap);
    }

    @Test
    public void testExecute_whenThereIsCompleteDetailsAndTrusteeUserRoles_thenPopulateMapModelAccordingly() {
        when(userProfileService.getUserId()).thenReturn(null);
        when(userProfileService.isEmulating()).thenReturn(false);

        JobProfile jobProfile = mock(JobProfile.class);
        JobPermission jobPermission = mock(JobPermission.class);

        when(jobPermission.getUserRoles()).thenReturn(Arrays.asList(UserRole.TRUSTEE_BASIC.getRole()));
        when(userProfileService.getAvailableProfiles()).thenReturn(Arrays.asList(jobProfile));
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrors.class)))
                .thenReturn(jobPermission);

        globalElementPreparer.execute(tilesContext, attributeContext);

        Map<String, Object> modelMap = tilesContext.getRequestScope();

        assertNotNull(modelMap.get(Attribute.PERSON_MODEL));
        assertEquals("", ((ServiceOperator) modelMap.get(Attribute.PERSON_MODEL)).getUserName());
        assertEquals(false, modelMap.get(Attribute.IS_DEALERGROUP));
        assertEquals(true, modelMap.get(Attribute.TRUSTEE_APPROVAL_ACCESS));
        assertEquals(false, modelMap.get(Attribute.IRG_APPROVAL_ACCESS));

        when(userProfileService.getUserId()).thenReturn("CS00001");
        when(jobPermission.getUserRoles()).thenReturn(Arrays.asList(UserRole.TRUSTEE_READ_ONLY.getRole()));
        globalElementPreparer.execute(tilesContext, attributeContext);

        assertEquals("CS00001", ((ServiceOperator) modelMap.get(Attribute.PERSON_MODEL)).getUserName());
        assertEquals(true, modelMap.get(Attribute.TRUSTEE_APPROVAL_ACCESS));
    }

    @Test
    public void testExecute_whenThereIsCompleteDetailsAndIrgUserRoles_thenPopulateMapModelAccordingly() {
        when(userProfileService.getUserId()).thenReturn(null);
        when(userProfileService.isEmulating()).thenReturn(false);

        JobProfile jobProfile = mock(JobProfile.class);
        JobPermission jobPermission = mock(JobPermission.class);

        when(jobPermission.getUserRoles()).thenReturn(Arrays.asList(UserRole.IRG_BASIC.getRole()));
        when(userProfileService.getAvailableProfiles()).thenReturn(Arrays.asList(jobProfile));
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrors.class)))
                .thenReturn(jobPermission);

        globalElementPreparer.execute(tilesContext, attributeContext);

        Map<String, Object> modelMap = tilesContext.getRequestScope();

        assertEquals(false, modelMap.get(Attribute.TRUSTEE_APPROVAL_ACCESS));
        assertEquals(true, modelMap.get(Attribute.IRG_APPROVAL_ACCESS));

        when(jobPermission.getUserRoles()).thenReturn(Arrays.asList(UserRole.IRG_READ_ONLY.getRole()));
        globalElementPreparer.execute(tilesContext, attributeContext);
        assertEquals(true, modelMap.get(Attribute.IRG_APPROVAL_ACCESS));
    }

    @Test
    public void testExecute_whenThereIsNoJobProfile_thenTrusteeOrIrgApprovalAccessShouldBeFalse() {
        when(userProfileService.getUserId()).thenReturn(null);
        when(userProfileService.isEmulating()).thenReturn(false);

        when(userProfileService.getAvailableProfiles()).thenReturn(null);

        globalElementPreparer.execute(tilesContext, attributeContext);

        Map<String, Object> modelMap = tilesContext.getRequestScope();

        assertEquals(false, modelMap.get(Attribute.TRUSTEE_APPROVAL_ACCESS));
        assertEquals(false, modelMap.get(Attribute.IRG_APPROVAL_ACCESS));
    }

    @Test
    public void testExecute_whenThereIsNoUserRoles_thenTrusteeOrIrgApprovalAccessShouldBeFalse() {
        when(userProfileService.getUserId()).thenReturn(null);
        when(userProfileService.isEmulating()).thenReturn(false);

        JobProfile jobProfile = mock(JobProfile.class);
        JobPermission jobPermission = mock(JobPermission.class);

        when(jobPermission.getUserRoles()).thenReturn(null);
        when(userProfileService.getAvailableProfiles()).thenReturn(Arrays.asList(jobProfile));
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrors.class)))
                .thenReturn(jobPermission);

        globalElementPreparer.execute(tilesContext, attributeContext);

        Map<String, Object> modelMap = tilesContext.getRequestScope();

        assertEquals(false, modelMap.get(Attribute.TRUSTEE_APPROVAL_ACCESS));
        assertEquals(false, modelMap.get(Attribute.IRG_APPROVAL_ACCESS));
    }

    @Test
    public void testExecute_whenThereIsTrusteeUserRole_thenTrusteeOrIrgApprovalAccessShouldBeFalse() {
        when(userProfileService.getUserId()).thenReturn(null);
        when(userProfileService.isEmulating()).thenReturn(false);

        JobProfile jobProfile = mock(JobProfile.class);
        JobPermission jobPermission = mock(JobPermission.class);

        when(jobPermission.getUserRoles()).thenReturn(Arrays.asList(UserRole.SERVICEOPS_ADMINISTRATOR_BASIC.getRole()));
        when(userProfileService.getAvailableProfiles()).thenReturn(Arrays.asList(jobProfile));
        when(userInformationIntegrationService.getAvailableRoles(any(JobProfile.class), any(ServiceErrors.class)))
                .thenReturn(jobPermission);

        globalElementPreparer.execute(tilesContext, attributeContext);

        Map<String, Object> modelMap = tilesContext.getRequestScope();

        assertEquals(false, modelMap.get(Attribute.TRUSTEE_APPROVAL_ACCESS));
        assertEquals(false, modelMap.get(Attribute.IRG_APPROVAL_ACCESS));
    }
}