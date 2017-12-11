package com.bt.nextgen.chessService.service;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorIntegrationService;
import com.bt.nextgen.service.integration.chessparameter.ChessSponsorService;
import com.btfin.panorama.service.client.error.ServiceErrorsImpl;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
/**
 * Created by l078480 on 27/06/2017.
 */
public class ChessIntegrationServiceIntegrationTest extends BaseSecureIntegrationTest {

    @Autowired
    ChessSponsorIntegrationService chessSponsorIntegrationService;

    @SecureTestContext
    @Test
    public void test_loadChessData() throws Exception {
        ChessSponsorService chessSponsorService= chessSponsorIntegrationService.getChessSponsorData(new ServiceErrorsImpl());
        assertThat(chessSponsorService.getChessSponsor().size(), equalTo(95));


    }

}
