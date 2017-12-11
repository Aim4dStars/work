package com.bt.nextgen.marketdata.v1.controller;

import com.bt.nextgen.api.marketdata.v1.controller.MarkitOnDemandApiController;
import com.bt.nextgen.api.marketdata.v1.model.ShareNotificationsDto;
import com.bt.nextgen.api.marketdata.v1.model.ServerUrlDto;
import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.bt.nextgen.core.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L070589 on 7/10/2015.
 */
public class MarkitOnDemandApiControllerTest extends BaseSecureIntegrationTest {

    @Autowired
    private MarkitOnDemandApiController markitOnDemandApiController;

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "adviser", customerId = "M034010", profileId = "561", jobRole = "ADVISER", jobId = "89436")
    public void testMarketOnDemandTest() {
        ApiResponse apiResponse = markitOnDemandApiController.getServerUrl();
        Assert.assertNotNull(apiResponse.getData());
        ServerUrlDto serverUrlDto = (ServerUrlDto) apiResponse.getData();
        Assert.assertEquals(serverUrlDto.getServerUrl(), Properties.get("markit.on.demand.env.url"));
    }

    @Test
    @SecureTestContext(authorities = {"ROLE_ADVISER"}, username = "adviser", customerId = "M034010", profileId ="561" , jobRole="ADVISER", jobId = "89436")
    public void setMarkitOnDemandApiController() {
        ShareNotificationsDto dto = new ShareNotificationsDto();
        dto.setType("ASX");
        List<String> encryptedPersonKeys = new ArrayList<>();
        encryptedPersonKeys.add(ConsistentEncodedString.fromPlainText("130960").toString());
        encryptedPersonKeys.add(ConsistentEncodedString.fromPlainText("130960").toString());
        dto.setConsistentlyEncryptedClientKeys(encryptedPersonKeys);
        dto.setUrl("http://www.google.com");
        dto.setUrlText("Google ....");

        ApiResponse apiResponse = markitOnDemandApiController.shareStory(dto);
        Assert.assertNotNull(apiResponse.getData());
        ShareNotificationsDto response = (ShareNotificationsDto) apiResponse.getData();
        Assert.assertEquals(response.getStatus(), "Success");
        Assert.assertEquals(response.getKey(),
                ConsistentEncodedString.fromPlainText("130960").toString() + "," +
                        ConsistentEncodedString.fromPlainText("130960").toString());

    }

}
