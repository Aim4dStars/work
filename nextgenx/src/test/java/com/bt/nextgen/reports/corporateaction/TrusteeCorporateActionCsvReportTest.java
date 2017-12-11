package com.bt.nextgen.reports.corporateaction;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionApprovalDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionBaseDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionDtoParams;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDto;
import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionListDtoKey;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionApprovalListDtoService;
import com.btfin.panorama.core.security.profile.UserProfile;
import com.bt.nextgen.core.security.profile.UserProfileAdapterImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.avaloq.userinformation.FunctionalRole;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionStatus;
import com.bt.nextgen.service.integration.trustee.TrusteeApprovalStatus;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.integration.userinformation.JobKey;
import com.btfin.panorama.core.security.integration.userinformation.UserInformation;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TrusteeCorporateActionCsvReportTest {

    @InjectMocks
    private TrusteeCorporateActionCsvReport trusteeCorporateActionCsvReport;

    @Mock
    private CorporateActionApprovalListDtoService corporateActionApprovalListDtoService;

    @Mock
    private UserProfileService userProfileService;

    private Asset trusteeCorporateActionAsset;

    @Test
    public void testRetrieveCashTransactionDtos() throws ParseException {
        trusteeCorporateActionAsset = Mockito.mock(Asset.class);
        Mockito.when(trusteeCorporateActionAsset.getAssetCode()).thenReturn("AMA");
        Mockito.when(trusteeCorporateActionAsset.getAssetName()).thenReturn("AMA Group Limited");

        String id = "123456";
        CorporateActionDtoParams params = new CorporateActionDtoParams();
        params.setCloseDate(new DateTime());
        params.setAnnouncementDate(new DateTime());
        params.setAsset(trusteeCorporateActionAsset);
        params.setCorporateActionType("SHARE_PURCHASE_PLAN");
        params.setCorporateActionTypeDescription("Share Purchase Plan");
        params.setEligible(null);
        params.setUnconfirmed(null);
        params.setStatus(CorporateActionStatus.CLOSED);
        params.setHoldingLimitPercent(null);
        params.setTrusteeApprovalStatus(TrusteeApprovalStatus.PENDING);
        params.setTrusteeApprovalStatusDate(new DateTime());
        params.setTrusteeApprovalUserId("27090");
        params.setTrusteeApprovalUserName("Boris Yeltsin - SUPER");
        params.setPayDate(new DateTime());

        CorporateActionApprovalDto corporateActionBaseDto = new CorporateActionApprovalDto(id, params);

        List<CorporateActionBaseDto> corporateActions = new ArrayList<>();
        corporateActions.add(corporateActionBaseDto);

        CorporateActionListDto corporateActionListDto = new CorporateActionListDto(false, corporateActions);

        when(corporateActionApprovalListDtoService.find(
                Matchers.any(CorporateActionListDtoKey.class),
                any(ServiceErrorsImpl.class))
        ).thenReturn(corporateActionListDto);

        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("account-id", "975AF9B7FE27CF0A2A7134DC4BBC3EDD510AAB21532150E0");
        urlParams.put("startDate", "2014-09-09");
        urlParams.put("endDate", "2014-12-09");

        List<CorporateActionBaseDto> trusteeCorporateActionDtoList = (List<CorporateActionBaseDto>)
                trusteeCorporateActionCsvReport.retrieveTrusteeCorporateActionDtos(urlParams);

        assertNotNull(trusteeCorporateActionDtoList);

        Assert.assertEquals(1, trusteeCorporateActionDtoList.size());

        CorporateActionApprovalDto ca = (CorporateActionApprovalDto) trusteeCorporateActionDtoList.get(0);

        Assert.assertEquals("AMA", ca.getCompanyCode());
        Assert.assertEquals("AMA Group Limited", ca.getCompanyName());
        Assert.assertEquals(TrusteeApprovalStatus.PENDING, ca.getTrusteeApprovalStatus());
        Assert.assertEquals("27090", ca.getTrusteeApprovalUserId());
        Assert.assertEquals("Boris Yeltsin - SUPER", ca.getTrusteeApprovalUserName());
        Assert.assertEquals("SHARE_PURCHASE_PLAN", ca.getCorporateActionType());
        Assert.assertEquals("Share Purchase Plan", ca.getCorporateActionTypeDescription());
    }

    @Test
    public void testDates() {
        Map<String, String> params = new HashMap<>();

        DateTime testDate = new DateTime(2017, 1, 1, 0, 0); // midnight @ 01/01/2017

        // no start date parameter
        DateTime dateTime = trusteeCorporateActionCsvReport.getStartDate(params);
        Assert.assertNotEquals(testDate, dateTime);

        // with start date parameter
        params.put("startDate", testDate.toString());
        dateTime = trusteeCorporateActionCsvReport.getStartDate(params);
        Assert.assertEquals(testDate, dateTime);

        // no end date parameter
        dateTime = trusteeCorporateActionCsvReport.getEndDate(params);
        Assert.assertNotEquals(testDate, dateTime);

        // with end date parameter
        params.put("endDate", testDate.toString());
        dateTime = trusteeCorporateActionCsvReport.getEndDate(params);
        Assert.assertEquals(testDate, dateTime);
    }

    @Test
    public void testTrusteeName() {
        UserInformationTestImpl userInformation = new UserInformationTestImpl();
        UserProfile userProfile = new UserProfileAdapterImpl(userInformation, null);

        when(userProfileService.getActiveProfile()).thenReturn(userProfile);

        Assert.assertEquals("Test Trustee Name 123", trusteeCorporateActionCsvReport.getTrusteeName(null));
    }

    private class UserInformationTestImpl implements UserInformation {
        @Override
        public String getSafiDeviceId() {
            return null;
        }

        @Override
        public String getFullName() {
            return "Test Trustee Name 123";
        }

        @Override
        public String getFirstName() {
            return null;
        }

        @Override
        public String getLastName() {
            return null;
        }

        @Override
        public ClientKey getClientKey() {
            return null;
        }

        @Override
        public void setClientKey(ClientKey clientKey) {

        }

        @Override
        public List<FunctionalRole> getFunctionalRoles() {
            return null;
        }

        @Override
        public void setFunctionalRoles(List<FunctionalRole> list) {

        }

        @Override
        public List<String> getUserRoles() {
            return null;
        }

        @Override
        public JobKey getJob() {
            return null;
        }

        @Override
        public String getProfileId() {
            return null;
        }
    }
}
