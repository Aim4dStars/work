package com.bt.nextgen.service.avaloq.account;

import com.bt.nextgen.service.AvaloqGatewayUtil;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.abs.epiadviserdownload.v1_0.EpiDataReq;

public class BglConverterTest
{
	AccountBglConverter converter = new AccountBglConverter();

	@Before
	public void setup()
	{}

	@Test
	public void testToModelValuationMovement_whenSuppliedWithValidRequest_thenAvaloqRequestMatches() throws Exception
	{
		DateTime startDate = new DateTime().minusMonths(3).withTimeAtStartOfDay();
		DateTime endDate = new DateTime().withTimeAtStartOfDay();
		AccountKey account = AccountKey.valueOf("1234");
		EpiDataReq epi = converter.toBglRequest(account, startDate, endDate, new FailFastErrorsImpl());
		Assert.assertEquals(startDate, AvaloqGatewayUtil.asDateTime(epi.getData().getSop()));
		Assert.assertEquals(endDate, AvaloqGatewayUtil.asDateTime(epi.getData().getEop()));
		Assert.assertEquals(account.getId(), AvaloqGatewayUtil.asString(epi.getData().getBp()));
	}

}
