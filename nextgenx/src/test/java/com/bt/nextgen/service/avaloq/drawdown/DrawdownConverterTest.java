package com.bt.nextgen.service.avaloq.drawdown;

import com.bt.nextgen.service.integration.account.SubAccountKey;
import com.bt.nextgen.service.integration.drawdown.DrawdownOption;
import com.btfin.abs.trxservice.cont.v1_0.ContReq;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DrawdownConverterTest {
    @InjectMocks
    private DrawdownConverter drawdownConverter;

    @Test
    public void testUpdateDrawdownOption() {
        ContReq contReq = drawdownConverter.toUpdateRequest(SubAccountKey.valueOf("subAccountId"), DrawdownOption.PRORATA);
        Assert.assertTrue(contReq.getData().getDrawDwn().getStrat().getExtlVal().getVal()
                .equals(DrawdownOption.PRORATA.getIntlId()));
    }

}
