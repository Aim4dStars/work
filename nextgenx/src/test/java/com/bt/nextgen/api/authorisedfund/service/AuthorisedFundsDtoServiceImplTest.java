package com.bt.nextgen.api.authorisedfund.service;

import com.bt.nextgen.api.bgl.service.AccountingSoftwareConnectionService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.domain.SmsfImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.authentication.model.TokenIssuer;
import com.bt.nextgen.service.integration.authorisedfund.model.AuthorisedFundDetail;
import com.bt.nextgen.service.integration.authorisedfund.model.TrustDetails;
import com.bt.nextgen.service.integration.authorisedfund.service.AuthorisedFundsIntegrationService;
import com.bt.nextgen.service.integration.userinformation.Client;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by m022641 on 27/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class AuthorisedFundsDtoServiceImplTest {
    private final static String ACCOUNT_ID = "123";
    private final static String ABN = "111222333";
    private final static String GCM_ID = "987654321";

    @Mock
    private AccountIntegrationService accountIntegrationService;

    @Mock
    private AuthorisedFundsIntegrationService authorisedFundsIntegrationService;

    @Mock
    private AccountingSoftwareConnectionService accountingSoftwareConnectionService;

    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private AuthorisedFundsDtoServiceImpl dtoService;

    private AccountKey accountKey;
    private String encodedAccountId;
    private WrapAccountDetailImpl wrapAccountDetail;


    @Before
    public void init() {
        final SmsfImpl smsf = new SmsfImpl();

        accountKey = AccountKey.valueOf(ACCOUNT_ID);
        encodedAccountId = EncodedString.fromPlainText(ACCOUNT_ID).toString();

        smsf.setAbn(ABN);
        wrapAccountDetail = new WrapAccountDetailImpl();
        wrapAccountDetail.setOwners(Arrays.asList((Client) smsf));
    }


    @Test
    public void isAccountAuthorisedWithEmptyFundList() {
        checkAccountAuthorised(makeAuthorisedFundDetails(), false);
    }

    @Test
    public void isAccountAuthorisedForFundListWithOneNullAbn() {
        final List<AuthorisedFundDetail> authorisedFundDetails = new ArrayList<>();

        authorisedFundDetails.add(makeAuthorisedFundDetail(null));
        checkAccountAuthorised(authorisedFundDetails, false);
    }

    @Test
    public void isAccountAuthorisedForFundListWithMultipleNullAbns() {
        checkAccountAuthorised(makeAuthorisedFundDetails(null, "abc", null), false);
    }

    @Test
    public void isAccountAuthorisedForFundListWithoutMatchingAbn() {
        checkAccountAuthorised(makeAuthorisedFundDetails("abc", "def", "xyz"), false);
    }

    @Test
    public void isAccountAuthorisedForFundListWithMatchingAbn() {
        checkAccountAuthorised(makeAuthorisedFundDetails("abc", null, "", ABN, "xyz"), true);
    }

    @Test
    public void isAccountAuthorisedForFundListWithSingleElementAndMatchingAbn() {
        checkAccountAuthorised(makeAuthorisedFundDetails(ABN), true);
    }


    private void checkAccountAuthorised(List<AuthorisedFundDetail> authorisedFundDetails,
                                        boolean expectedResult) {
        when(accountIntegrationService.loadWrapAccountDetail(eq(accountKey), any(ServiceErrors.class)))
                .thenReturn(wrapAccountDetail);
        when(accountingSoftwareConnectionService.getAccountantGcmIdForAccount(encodedAccountId))
                .thenReturn(GCM_ID);
        when(authorisedFundsIntegrationService.loadAuthorisedFunds(any(String.class), eq(TokenIssuer.BGL)))
                .thenReturn((authorisedFundDetails));

        assertThat("account authorised", dtoService.isAccountAuthorised(accountKey),
                equalTo(expectedResult));
    }

    private List<AuthorisedFundDetail> makeAuthorisedFundDetails(String... abns) {
        final List<AuthorisedFundDetail> retval = new ArrayList<>();

        for (String abn : abns) {
            retval.add(makeAuthorisedFundDetail(abn));
        }

        return retval;
    }

    private AuthorisedFundDetail makeAuthorisedFundDetail(final String abn) {
        return new AuthorisedFundDetail() {
            @Override
            public String getOrganisationName() {
                return null;
            }

            @Override
            public void setOrganisationName(String organisationName) {

            }

            @Override
            public String getAbn() {
                return abn;
            }

            @Override
            public void setAbn(String abn) {

            }

            @Override
            public TrustDetails getTrustDetails() {
                return null;
            }

            @Override
            public void setTrustDetails(TrustDetails trustDetails) {

            }
        };
    }
}
