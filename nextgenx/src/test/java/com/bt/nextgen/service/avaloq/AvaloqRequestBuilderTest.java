package com.bt.nextgen.service.avaloq;

import com.bt.nextgen.service.avaloq.code.StaticCodeEnumTemplate;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.common.v1_0.Res;
import com.btfin.abs.reportservice.reportrequest.v1_0.RepReq;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class AvaloqRequestBuilderTest
{


	@Ignore
	@Test
	public void testGetJMSAwareReportRequest_whichWillUseJms()
	{
        AvaloqRequest avaloqReportRequest= new AvaloqReportRequestImpl(StaticCodeEnumTemplate.STATIC_CODES);
        AvaloqRequest report = AvaloqRequestBuilderUtil.getJMSAwareReportRequest(avaloqReportRequest);
		assertThat(report, is(notNullValue()));
		RepReq avaloqRepReq = report.getRequestObject();
		assertThat(avaloqRepReq,is(notNullValue()));
        assertThat(avaloqRepReq.getHdr().getMode(), is(notNullValue()));
        assertThat(avaloqRepReq.getHdr().getMode().getRes(), is(Res.ASYNC));
        assertThat(avaloqRepReq.getHdr().getMode().isCompress(), is(true));

	}




}
