package com.bt.nextgen.api.adviser.controller;

import com.bt.nextgen.api.adviser.model.AdviserDetailDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.api.exception.ServiceException;
import com.bt.nextgen.core.api.model.ApiResponse;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AdviserApiControllerIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    private AdviserApiController adviserApiController;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    @SecureTestContext(authorities =
            {
                    "ROLE_ADVISER"
            }, username = "adviser", customerId = "201615361", profileId = "3364")
    public void testGetAdviserDetail_whenAdviserIsLoggedIn() throws Exception {
        String personId = "6C79CCC605583BDC592BF521E54FC551A892EE4ECFCB79D6";
        //System.out.println(EncodedString.fromPlainText("45278").toString());
        ApiResponse response = adviserApiController.getAdviserDetails(personId);
        assertThat(response, is(notNullValue()));
        AdviserDetailDto adviserDetailDto = (AdviserDetailDto) response.getData();
        assertThat(adviserDetailDto.getUserName(), is("adviser"));
        assertThat(adviserDetailDto.getDealerGroupName(), is("collect--1_136 (Dealer Group)"));
        assertThat(adviserDetailDto.getFullName(), is("Richard Jay Peterson"));
        assertThat(adviserDetailDto.getOpenDate(), is(new DateTime("2014-07-14T00:00:00.000+10:00")));
    }


    @Test
    @SecureTestContext(authorities =
            {
                    "ROLE_ADVISER"
            }, username = "adviser", customerId = "201604869", profileId = "2053")
    public void testGetAdviserDetailSomeOtherPersonIsLoggedInShouldReturnError() throws Exception {

        String personId = "8E24E17D1E4CCB14EF92D8D599A37FE09193AF5E46D815E2";
        exception.expect(ServiceException.class);
        adviserApiController.getAdviserDetails(personId);
        exception.expectMessage("User does not have permission to view detail");

    }
}
