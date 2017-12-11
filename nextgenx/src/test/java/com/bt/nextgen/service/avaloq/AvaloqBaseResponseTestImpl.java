package com.bt.nextgen.service.avaloq;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import com.bt.nextgen.integration.xml.annotation.ConditionType;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.extractor.DefaultResponseExtractor;
import com.bt.nextgen.integration.xml.namespace.RuntimeNamespaceContext;

public class AvaloqBaseResponseTestImpl
{

	String errorResponse = "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
		"    <env:Header/>\n" +
		"    <env:Body>\n" +
		"        <ns3:AvaloqGatewayResponseMsg xmlns:ns3=\"ns://private.btfin.com/Avaloq/AvaloqGateway/AvaloqGatewayResponse/V1_0\">\n" +
		"            <ResponseDetails>\n" +
		"                <SuccessResponse>\n" +
		"\n" +
		"                    <err:err_rsp xmlns:err=\"http://btfin.com/ABS/Err/V1_0\" xmlns:avqxsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
		"                        <err>Session could not be opened</err>\n" +
		"                        <context>\n" +
		"                            <phase>msg#req_sync - open session</phase>\n" +
		"                            <bu_id>7</bu_id>\n" +
		"                            <person_auth_key>201651249</person_auth_key>\n" +
		"                            <req_msg>&lt;?xml version=\"1.0\" encoding=\"UTF-8\"?&gt;\n" +
		"                                &lt;rep:rep_req\n" +
		"                                xmlns:fldb=\"http://www.avaloq.com/abs/bb/fld_def\"\n" +
		"                                xmlns:rep=\"http://btfin.com/ABS/ReportService/ReportRequest/V1_0\"\n" +
		"                                xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"&gt;\n" +
		"                                &lt;hdr&gt;\n" +
		"                                &lt;req_id&gt;052c9a8e-947c-3879-e054-d48564c938e3&lt;/req_id&gt;\n" +
		"                                &lt;/hdr&gt;\n" +
		"                                &lt;task&gt;\n" +
		"                                &lt;templ&gt;btfg$ui_ntfcn_list.user#ntfcn&lt;/templ&gt;\n" +
		"                                &lt;param_list&gt;\n" +
		"                                &lt;param&gt;\n" +
		"                                &lt;name&gt;resp_sec_user_list_id&lt;/name&gt;\n" +
		"                                &lt;val_list&gt;\n" +
		"                                &lt;val xsi:type=\"fldb:IdFld\"&gt;\n" +
		"                                &lt;val&gt;201651249&lt;/val&gt;\n" +
		"                                &lt;/val&gt;\n" +
		"                                &lt;/val_list&gt;\n" +
		"                                &lt;/param&gt;\n" +
		"                                &lt;param&gt;\n" +
		"                                &lt;name&gt;creation_timestamp_from&lt;/name&gt;\n" +
		"                                &lt;val xsi:type=\"fldb:DateTimeFld\"&gt;\n" +
		"                                &lt;val&gt;2015-01-20T00:00:00&lt;/val&gt;\n" +
		"                                &lt;/val&gt;\n" +
		"                                &lt;/param&gt;\n" +
		"                                &lt;param&gt;\n" +
		"                                &lt;name&gt;creation_timestamp_to&lt;/name&gt;\n" +
		"                                &lt;val xsi:type=\"fldb:DateTimeFld\"&gt;\n" +
		"                                &lt;val&gt;2015-01-20T23:59:59&lt;/val&gt;\n" +
		"                                &lt;/val&gt;\n" +
		"                                &lt;/param&gt;\n" +
		"                                &lt;/param_list&gt;\n" +
		"                                &lt;fmt&gt;xml_specific&lt;/fmt&gt;\n" +
		"                                &lt;/task&gt;\n" +
		"                                &lt;/rep:rep_req&gt;\n" +
		"                            </req_msg>\n" +
		"                            <op>REP_REQ</op>\n" +
		"                        </context>\n" +
		"                        <err_list>\n" +
		"                            <err>\n" +
		"                                <type>fa</type>\n" +
		"                                <id/>\n" +
		"                                <err_msg/>\n" +
		"                            </err>\n" +
		"                            <err>\n" +
		"                                <type>fa</type>\n" +
		"                                <id/>\n" +
		"                                <err_msg>Fatal error occurred: </err_msg>\n" +
		"                            </err>\n" +
		"                        </err_list>\n" +
		"                    </err:err_rsp>\n" +
		"                    <!-- dbce5b74-523f-487c-933e-b8b9270f3ba3 at 2014-12-04T22:26:02.433699000Z on AFD030A by 201601388 -->\n" +
		"                </SuccessResponse>\n" +
		"            </ResponseDetails>\n" +
		"        </ns3:AvaloqGatewayResponseMsg>\n" +
		"    </env:Body>\n" +
		"</env:Envelope>";


	@Test
	public void testErrorMapping () throws Exception
	{
		//final ClassPathResource classPathResource = new ClassPathResource(
		//	"/TASK_PERSON_LIST.BTFG$UI_PERSON.xml");

		//		final ClassPathResource classPathResource = new ClassPathResource(
		//				"/saml-sample.xml");

		//String content = FileCopyUtils.copyToString(new InputStreamReader(classPathResource.getInputStream()));

		Map<String, String> nsMapper = new HashMap<>();
		nsMapper.put("saml", "urn:oasis:names:tc:SAML:2.0:assertion");
		nsMapper.put("err","http://btfin.com/ABS/Err/V1_0");
		RuntimeNamespaceContext nsContext = new RuntimeNamespaceContext(nsMapper);

		DefaultResponseExtractor<NotificationUnreadCountResponseImpl> defaultResponseExtractor= new DefaultResponseExtractor<NotificationUnreadCountResponseImpl>(NotificationUnreadCountResponseImpl.class);
		defaultResponseExtractor.setNamespaceContext(nsContext);
		NotificationUnreadCountResponseImpl unknownChildBean = defaultResponseExtractor.extractData(errorResponse);
		assertThat(unknownChildBean, is (not(nullValue())));
		assertThat(unknownChildBean.getErrorList(), is (not(nullValue())));
		assertThat(unknownChildBean.getErrorList().size(), is (2));
		assertThat(unknownChildBean.getErrorInfo(), is (not(nullValue())));
		assertThat(unknownChildBean.getErrorInfo().getBankReferenceId(), is (not(nullValue())));

		assertThat(unknownChildBean.getErrorInfo().getMainErrorMessage(), is (not(nullValue())));
		assertThat(unknownChildBean.getErrorInfo().getMainErrorMessage(), is ("Session could not be opened"));

	}

	/*@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
	public class AbstractParentBean extends AvaloqBaseResponseImpl
	{
		@ServiceElement(xpath = "/somethingThatWillNeverMatch")
		private String tmpSting;

	}*/

}
