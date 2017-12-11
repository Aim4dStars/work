package com.bt.nextgen.service.avaloq.gateway;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.core.webservice.provider.WebServiceProvider;
import com.bt.nextgen.service.avaloq.AvaloqObjectFactory;
import ns.private_btfin_com.avaloq.avaloqgateway.avaloqgatewayrequest.v1_0.AvaloqGatewayRequestMsgType;
import ns.private_btfin_com.avaloq.avaloqgateway.avaloqgatewayresponse.v1_0.AvaloqGatewayResponseMsgType;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.bt.nextgen.core.webservice.provider.SpringWebServiceTemplateProvider.SERVICE_AVALOQ;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class AvaloqGatewayIntegrationTest extends BaseSecureIntegrationTest
{
	private static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<rep:rep_req \n"
		+ "    xmlns:rep=\"http://btfin.com/ABS/ReportService/ReportRequest/V1_0\">\n" + "  <task>\n"
		+ "    <templ>task_afs_config.btfg$custr</templ>\n" + "    <fmt>xml_specific</fmt>\n" + "  </task>\n" + "</rep:rep_req>";

	private static String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<rep:rep_req \n"
		+ "    xmlns:rep=\"http://btfin.com/ABS/ReportService/ReportRequest/V1_0\">\n" + "    <task>\n"
		+ "        <templ>task_afs_config.btfg$custr</templ>\n" + "        <fmt>xml_specific</fmt>\n" + "    </task>\n"
		+ "</rep:rep_req>";

	@Autowired
	private WebServiceProvider factory;

	@Autowired
	private WebServiceProvider serviceProvider;
	
	@Ignore
	@Test
	public void testConnectToStub()
	{
		AvaloqGatewayRequestMsgType request = AvaloqObjectFactory.getRequestGatewayObjectFactory()
			.createAvaloqGatewayRequestMsgType();
		request.setBuId("7");
		//request.setUser("10000705");
		request.setOp("REP_REQ");
		request.setMsg(xml);
		AvaloqGatewayResponseMsgType responseMsgType = (AvaloqGatewayResponseMsgType)serviceProvider.sendWebService(SERVICE_AVALOQ,
				request);
		assertThat(responseMsgType.getResponseDetails().getSuccessResponse(),notNullValue());

	}
}
