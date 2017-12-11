package com.bt.nextgen.api.statements.service;

import com.bt.nextgen.api.statements.model.DocumentDto;
import com.bt.nextgen.api.statements.model.DocumentKey;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountImpl;
import com.bt.nextgen.service.avaloq.userinformation.JobRole;
import com.bt.nextgen.service.cmis.constants.DocumentCategories;
import com.bt.nextgen.service.cmis.constants.DocumentConstants;
import com.bt.nextgen.service.cmis.constants.VisibilityRoles;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;

/**
 * Created by L062605 on 13/08/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class DocumentRoleMatcherTest {

    DocumentRoleMatcher documentVisibilityUtil = null;

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private UserProfileService profileService;

    @Mock
    private UserProfile userProfile;

    DocumentDto documentDto = new DocumentDto();

    @Before
    public void setUp(){
        documentVisibilityUtil = new DocumentRoleMatcher(profileService);
        WrapAccountImpl wrapAccount = new WrapAccountImpl();
        AccountKey key = AccountKey.valueOf("1234");
        wrapAccount.setAccountKey(key);
        Map<AccountKey, WrapAccount> accountMap = new HashMap<>();
        accountMap.put(key, wrapAccount);

        DocumentKey documentKey = new DocumentKey();
        documentKey.setAccountId("402DE9E3DEB9612A98E240FF029E4086463BC7A5D27EF6FF");
        documentDto.setStatus("Final");
        documentDto.setDocumentType(DocumentCategories.STATEMENTS.getCode());
        documentDto.setUploadedRole(VisibilityRoles.ACCOUNTANT.name());
        documentDto.setKey(documentKey);

        when(accountIntegrationService.loadWrapAccountWithoutContainers((ServiceErrors) Matchers.anyObject()))
                .thenReturn(accountMap);
        when(profileService.isServiceOperator()).thenReturn(false);
        when(profileService.isAccountant()).thenReturn(true);
        when(profileService.getActiveProfile()).thenReturn(userProfile);
        when(userProfile.getBankReferenceId()).thenReturn("2016351254");
        when(userProfile.getJobRole()).thenReturn(JobRole.ACCOUNTANT);
    }

    @Test
    public void testIsDocumentVisible() {
        documentDto.setStatus(DocumentConstants.DOCUMENT_STATUS_DRAFT);
        boolean visibleDocument = documentVisibilityUtil.isDocumentVisible(documentDto);
        Assert.assertTrue(visibleDocument);
    }
}