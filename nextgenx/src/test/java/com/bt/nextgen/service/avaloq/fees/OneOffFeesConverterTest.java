package com.bt.nextgen.service.avaloq.fees;

import com.bt.nextgen.clients.util.JaxbUtil;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.fees.OneOffFees;
import com.btfin.abs.trxservice.advfee.v1_0.AdvFeeReq;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class OneOffFeesConverterTest
{
	@InjectMocks
	private OneOffFeesConverter oneOffFeesConverter = new OneOffFeesConverter();

	@Test
	public void testToOneOffModel() throws Exception
	{
		com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_pos_cacc.Rep report = JaxbUtil.unmarshall("/webservices/response/OneOffFeesAvaloqResponse_UT.xml",
			com.avaloq.abs.screen_rep.hira.btfg$ui_book_list_pos_cacc.Rep.class);

		OneOffFees feesImpl = oneOffFeesConverter.toOneOffModel(report, new ServiceErrorsImpl());
		assertNotNull(feesImpl);

	}

	@Test
	public void testToSubmitOneOffFeeRequest() throws Exception
	{
		OneOffFeesImpl adviceFeesInterface = new OneOffFeesImpl();
		adviceFeesInterface.setFees(new BigDecimal("1"));
		adviceFeesInterface.setDescription("Test Case");
		adviceFeesInterface.setAccountKey(AccountKey.valueOf("46425"));
		AdvFeeReq req = oneOffFeesConverter.toSubmitOneOffFeeRequest(adviceFeesInterface);
		assertNotNull(req);

		assertEquals(req.getData().getAmount().getVal(), AvaloqGatewayUtil.createNumberVal(adviceFeesInterface.getFees()).getVal());
		assertEquals(req.getData().getDesc().getVal(), AvaloqGatewayUtil.createTextVal(adviceFeesInterface.getDescription()).getVal());
		assertEquals(req.getData().getAcct().getVal(), AvaloqGatewayUtil.createIdVal(adviceFeesInterface.getAccountKey().getId()).getVal());
	}

	@Test
	public void testToSubmitOneOffFeesResponse() throws Exception
	{
		com.btfin.abs.trxservice.advfee.v1_0.AdvFeeRsp resp = JaxbUtil.unmarshall("/webservices/response/OneOffAdviceFeesTransactionResponse_UT.xml",
			com.btfin.abs.trxservice.advfee.v1_0.AdvFeeRsp.class);

		OneOffFees impl = oneOffFeesConverter.toSubmitOneOffFeesResponse(resp, new ServiceErrorsImpl());
		assertNotNull(impl);
		assertEquals(resp.getData().getDesc().getVal(), impl.getDescription());
		assertEquals(resp.getData().getAmount().getVal(), impl.getFees());
	}

	@Test
	public void testToValidateOneOffAdviceFeeRequest() throws Exception
	{
		OneOffFeesImpl adviceFeesInterface = new OneOffFeesImpl();
		adviceFeesInterface.setFees(new BigDecimal("1"));
		adviceFeesInterface.setDescription("Test Case");
		adviceFeesInterface.setAccountKey(AccountKey.valueOf("46425"));
		AdvFeeReq req = oneOffFeesConverter.toValidateOneOffAdviceFeeRequest(adviceFeesInterface);
		assertNotNull(req);

		assertEquals(req.getData().getAmount().getVal(), AvaloqGatewayUtil.createNumberVal(adviceFeesInterface.getFees()).getVal());
		assertEquals(req.getData().getDesc().getVal(), AvaloqGatewayUtil.createTextVal(adviceFeesInterface.getDescription()).getVal());
		assertEquals(req.getData().getAcct().getVal(), AvaloqGatewayUtil.createIdVal(adviceFeesInterface.getAccountKey().getId()).getVal());
	}

	@Test
	public void testValidateFeesResponse() throws Exception
	{

		com.btfin.abs.trxservice.advfee.v1_0.AdvFeeRsp resp = JaxbUtil.unmarshall("/webservices/response/OneOffAdviceFeesTransactionResponse_UT.xml",
			com.btfin.abs.trxservice.advfee.v1_0.AdvFeeRsp.class);

		OneOffFees impl = oneOffFeesConverter.validateFeesResponse(null, resp, new ServiceErrorsImpl());
		assertNotNull(impl);
		assertEquals(resp.getData().getDesc().getVal(), impl.getDescription());
		assertEquals(resp.getData().getAmount().getVal(), impl.getFees());
	}
}
