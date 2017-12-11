package com.bt.nextgen.security;

import com.bt.nextgen.config.BaseSecureIntegrationTest;
import com.bt.nextgen.config.SecureTestContext;
import com.bt.nextgen.api.safi.model.EventModel;
import com.bt.nextgen.service.security.SafiAnalyzeResult;
import com.bt.nextgen.service.security.SafiAuthenticateResult;
import com.bt.nextgen.service.security.SmsServiceImpl;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.rsa.csd.ws.IdentificationData;
import org.hamcrest.core.IsNull;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertThat;



public class SmsServiceIntegrationTest extends BaseSecureIntegrationTest
{

	@Autowired
	private SmsServiceImpl service;
	




	@SecureTestContext
	@Test
	public void testAnalyze() throws Exception
	{
		EventModel eventModel = new EventModel();
		eventModel.setClientDefinedEventType("BPAY_BILLER");
		boolean response = service.analyzeFromSafi(eventModel);
		assertThat(response, IsNull.notNullValue());
	}

	public HttpRequestParams getHttpRequestParams()
	{
		HttpRequestParams requestParams = new HttpRequestParams();
		requestParams.setHttpAccept("text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
		requestParams.setHttpAcceptChars("ISO-8859-1,utf-8;q=0.7,*;q=0.7");
		requestParams.setHttpAcceptEncoding("gzip,deflate");
		requestParams.setHttpAcceptLanguage("en-ua,en;q=0.5");
		requestParams.setHttpReferrer("http://www.cba.com.uk");
		requestParams.setHttpUserAgent("Mozilla/5.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; .NET CLR 1.1.4322; .NET CLR 2.0.50727; InfoPath.1; .NET CLR 3.0.04506.30)");
		requestParams.setHttpOriginatingIpAddress("127.0.0.1");

		return requestParams;
	}

	public SafiAnalyzeResult createSafiAnalyzeResult()
	{
		SafiAnalyzeResult result = new SafiAnalyzeResult();

		IdentificationData identificationData = new IdentificationData();
		result.setIdentificationData(identificationData);
		result.setDeviceId("234-234234-23423s-343");

		return result;
	}
	
	@SecureTestContext
	@Test
	public void testSendSmsCode() throws Exception
	{
		boolean response = service.sendSmsCodeFromSafi();
		assertThat(response, IsNull.notNullValue());
	}
	
	@SecureTestContext
	@Test
	public void testVerifySmsCode() throws Exception
	{
		String smsCode = "111111";
		boolean response = service.authenticateSmsCodeFromSafi(smsCode);
		assertThat(response, IsNull.notNullValue());
	}

	@Test
	@Ignore
	public void testSendSmsCode_1() throws Exception
	{
		SafiAnalyzeResult response = service.sendSmsCodeFromSafi(createSafiAnalyzeResult(), getHttpRequestParams());
		assertThat(response, IsNull.notNullValue());
	}
	
	@SecureTestContext
	@Test
	@Ignore
	public void testVerifySmsCode_1() throws Exception
	{
		String smsCode = "111111";
		SafiAuthenticateResult response = service.authenticateSmsCodeFromSafi(smsCode,
			getHttpRequestParams(),
			createSafiAnalyzeResult());
		assertThat(response.isSuccessFlag(), IsNull.notNullValue());
	}
	
	//@TEST TODO this doesn't actually do any testing?
	    /*
     * public void test() throws FileNotFoundException, IOException { String
     * file = "/webservices/response/analyze.xml"; Permissions response = new
     * Permissions(); Hashtable <String, String> permissionsMap = new Hashtable
     * <String, String>();
     * 
     * permissionsMap.put("HideAddBillerOrPayeeButton", "Y");
     * permissionsMap.put("PayToPayAnyone", "Y");
     * permissionsMap.put("RepeatPaymentsPayAnyone", "Y");
     * permissionsMap.put("PayToBPAY", "Y");
     * permissionsMap.put("RepeatPayments - BPAY", "Y");
     * permissionsMap.put("PayToLinkedAccounts", "Y");
     * permissionsMap.put("RepeatPaymentsLinkedAccount", "Y");
     * permissionsMap.put("FilterPayDropDownFieldOnlyLinkedAccounts)", "Y");
     * permissionsMap.put("CanNotTransact", "Y");
     * permissionsMap.put("ChangeDailyPaymentLimit", "Y");
     * permissionsMap.put("UserCannotChangeDailyPaymentLimitPayAnyoneOrBPAY",
     * "Y"); permissionsMap.put("ConfirmAndPay", "Y");
     * permissionsMap.put("SuccessfullySubmittedPayments", "Y");
     * permissionsMap.put("PaymentReceiptPDFDownload", "Y");
     * 
     * permissionsMap.put("Deposit", "Y"); permissionsMap.put("RepeatDeposit",
     * "Y"); permissionsMap.put("ConfirmAndDeposit", "Y");
     * permissionsMap.put("SuccessfullySubmittedDeposit", "Y");
     * permissionsMap.put("DepostReceiptPDFDownload", "Y");
     * 
     * permissionsMap.put("AddPayAnyoneAccount", "Y");
     * permissionsMap.put("AddBPAYBiller", "Y");
     * permissionsMap.put("AddLinkedAccount", "Y");
     * permissionsMap.put("EditPayAnyoneDailyPaymentLimit", "Y");
     * permissionsMap.put("EditBPAYDailyPaymentLimit", "Y");
     * permissionsMap.put("DeletePayAnyoneAccount", "Y");
     * permissionsMap.put("DeleteBPAYAccount", "Y");
     * permissionsMap.put("DeleteLinkedAccount", "Y");
     * permissionsMap.put("SelectToMakePayAnyonePayment", "Y");
     * permissionsMap.put("SelectToMakePaymentOrDepositLinkedAccount)", "Y");
     * permissionsMap.put("SelectToMakeBPAYPayment", "Y");
     * 
     * Permissions.PermissionMapObj permissionMapObj = new
     * Permissions.PermissionMapObj();
     * permissionMapObj.setPermissionsMap(permissionsMap); List
     * <Permissions.PermissionMapObj> permissionMapObjList = new ArrayList <>();
     * permissionMapObjList.add(permissionMapObj);
     * response.setPermissionsMapList(permissionMapObjList);
     * 
     * try (OutputStream stream = new FileOutputStream(new
     * File(SmsControllerIntegrationTest.class.getResource(file).getFile()))) {
     * JaxbUtil.marshall(stream, Permissions.class, response); } }
     */
}
