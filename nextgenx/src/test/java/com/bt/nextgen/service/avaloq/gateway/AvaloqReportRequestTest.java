package com.bt.nextgen.service.avaloq.gateway;

import com.avaloq.abs.bb.fld_def.DateFld;
import com.avaloq.abs.bb.fld_def.DateTimeFld;
import com.avaloq.abs.bb.fld_def.IdFld;
import com.avaloq.abs.bb.fld_def.NrFld;
import com.avaloq.abs.bb.fld_def.TextFld;
import com.bt.nextgen.reports.service.ReportGenerationServiceImpl;
import com.bt.nextgen.service.avaloq.AvaloqParameter;
import com.bt.nextgen.service.avaloq.AvaloqTemplate;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.Template;
import com.bt.nextgen.service.avaloq.account.AccountParams;
import com.bt.nextgen.service.avaloq.account.AccountTemplate;
import com.bt.nextgen.service.request.AvaloqOperation;
import com.bt.nextgen.service.request.AvaloqReportRequestImpl;
import com.bt.nextgen.service.request.AvaloqRequest;
import com.btfin.abs.reportservice.reportrequest.v1_0.Exec;
import com.btfin.abs.reportservice.reportrequest.v1_0.Fmt;
import com.btfin.abs.reportservice.reportrequest.v1_0.Param;
import com.btfin.abs.reportservice.reportrequest.v1_0.RepReq;
import com.btfin.abs.reportservice.reportrequest.v1_0.Res;
import com.btfin.abs.reportservice.reportrequest.v1_0.Task;
import com.btfin.abs.reportservice.reportrequest.v1_0.ValList;
import org.apache.commons.lang3.ArrayUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AvaloqReportRequestTest
{
    private AvaloqRequest avaloqReportRequest;
    private AvaloqParameter avaloqParameter;
    private AvaloqTemplate avaloqTemplate;

    @Before
    public void setup(){    }

	@Test
    public void testCreateQueryForListOfValues()
	{
		ArrayList <String> list = new ArrayList <>();
		list.add("123");
		list.add("456");
		ValList value = AvaloqReportRequest.createQueryForListOfValues("FakeValType", list);
		assertNotNull(value);
		assertEquals(2, value.getVal().size());
        assertEquals("FakeValType", ((IdFld) value.getVal().get(0)).getExtlVal().getKey());
        assertEquals("123", ((IdFld) value.getVal().get(0)).getExtlVal().getVal());
		assertEquals("FakeValType", ((IdFld)value.getVal().get(1)).getExtlVal().getKey());
        assertEquals("456", ((IdFld) value.getVal().get(1)).getExtlVal().getVal());
	}

    @Test
    public void testCreateQueryFor_whenDateTime_thenDateFldReturned() {
        DateFld value = (DateFld) AvaloqReportRequest.createQueryFor(new DateTime("2017-01-01"));
        assertNotNull(value);
        assertEquals("2017-01-01", value.getVal().toString());
    }

    @Test
	public void testCreateQueryFor()
	{
		IdFld value = (IdFld)AvaloqReportRequest.createQueryFor("FakeValType", "123");
		assertNotNull(value);
		assertEquals("123", value.getVal());
	}

    @Test
    public void testCreateQueryFor_whenTextVal_thenTextFldReturned() {
        TextFld value = (TextFld) AvaloqReportRequest.createQueryFor(AvaloqUtils.VAL_TEXTVAL, "123");
        assertNotNull(value);
        assertEquals("123", value.getVal());
    }

    @Test
    public void testCreateQueryFor_whenDateVal_thenDateFldReturned() {
        DateFld value = (DateFld) AvaloqReportRequest.createQueryFor(AvaloqUtils.VAL_DATEVAL, "2017-01-01");
        assertNotNull(value);
        assertEquals("2017-01-01", value.getVal().toString());
    }

    @Test
    public void testCreateQueryFor_whenDateTimeVal_thenDateTimeFldReturned() {
        DateTimeFld value = (DateTimeFld) AvaloqReportRequest.createQueryFor(AvaloqUtils.VAL_DATETIMEVAL, "2017-01-01");
        assertNotNull(value);
        assertEquals("2017-01-01T00:00:00.000", value.getVal().toString());
    }

    @Test
    public void testCreateQueryFor_whenMultipleParams_thenAllAdded() {
        ValList vals = AvaloqReportRequest.createQueryFor("FakeValType", ArrayUtils.toArray("123", "456"));
        assertEquals(2, vals.getVal().size());
        assertEquals("123", ((IdFld) vals.getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) vals.getVal().get(1)).getVal());
    }

    @Test
    public void testCreateQueryFor_whenMultipleTextVal_thenAllAdded() {
        ValList vals = AvaloqReportRequest.createQueryFor(AvaloqUtils.VAL_TEXTVAL, ArrayUtils.toArray("123", "456"));
        assertEquals(2, vals.getVal().size());
        assertEquals("123", ((TextFld) vals.getVal().get(0)).getVal());
        assertEquals("456", ((TextFld) vals.getVal().get(1)).getVal());
    }

    @Test
    public void testCreateQueryFor_whenMultipleDateVal_thenAllAdded() {
        ValList vals = AvaloqReportRequest.createQueryFor(AvaloqUtils.VAL_DATEVAL,
                ArrayUtils.toArray("2017-01-01", "2017-02-01"));
        assertEquals(2, vals.getVal().size());
        assertEquals("2017-01-01", ((DateFld) vals.getVal().get(0)).getVal().toString());
        assertEquals("2017-02-01", ((DateFld) vals.getVal().get(1)).getVal().toString());
    }

    @Test
    public void testCreateQueryForId() {
        ValList vals = AvaloqReportRequest.createQueryForId("FakeValType", "123");
        assertNotNull(vals);
        assertEquals("123", ((IdFld) vals.getVal().get(0)).getVal());
    }

	@Test
	public void testCreatingQueryParam()
	{
		Param parameter = AvaloqReportRequest.createQueryParameter("MyParam");
		assertNotNull(parameter);
		assertEquals("MyParam", parameter.getName());

	}

	@Test
	public void testCreatorMethod()
	{
		AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
		assertNotNull(request);
		assertNotNull(request.getRequestObject());
		RepReq avaloqRequest = request.getRequestObject();
		assertEquals("A Fake Template", avaloqRequest.getTask().getTempl());
		assertEquals(Fmt.XML_SPECIFIC, avaloqRequest.getTask().getFmt());

	}

	@Test
	public void testEmulatingUser()
	{
		AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
		request.emulatingUser("investor");
		//TODO:CASH05MERGE where has this gone?
		//assertThat(request.getRequestObject().getTask().getEmulatedUser(), Is.is("investor"));
	}

    @Test
    public void testforAccount() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAccount("accountId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ACCOUNT_ID, param.getName());
        assertEquals("accountId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforAccountList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAccountList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ACCOUNT_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforAccountListId() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAccountListId("accountId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ACCOUNT_LIST_ID, param.getName());
        assertEquals("accountId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforAdviserOeId() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAdviserOeId("oeId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.ADVISER_OE_ID, param.getName());
        assertEquals("oeId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforAssetIds() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAssetIds(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ASSET_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((TextFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((TextFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforAssets() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAssets(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ASSET_LIST_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((TextFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((TextFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforAssetsOptional_whenNotNull_thenParamsAdded() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAssetsOptional(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ASSET_LIST_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((TextFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((TextFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforAssetsOptional_whenNull_thenNoParamsAdded() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAssetsOptional(null);
        assertNull(request.getRequestObject().getTask().getParamList());
    }

    @Test
    public void testforAvokaAppNum() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forAvokaAppNum(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_AVOKA_APP_NO, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforBenchmark() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forBenchmark("benchmarkId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.BENCHMARK_ID, param.getName());
        assertEquals("benchmarkId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforBpIdList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forBpIdList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_BP_ID_LIST, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforBpList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forBpList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_BP_LIST, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

	@Test
	public void testforBpListId()
	{
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forBpListId(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ACCOUNT_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getExtlVal().getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getExtlVal().getVal());
	}

    @Test
    public void testforBpNrList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forBpNrList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_BP_LIST, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getExtlVal().getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getExtlVal().getVal());
    }

    @Test
    public void testforBpNrListVal() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forBpNrListVal(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_BP_LIST, param.getName());
        assertEquals(BigDecimal.valueOf(123), ((NrFld) param.getVal()).getVal());
    }

    @Test
    public void testForCISKey() throws Exception {
        AvaloqReportRequest request = new AvaloqReportRequest(Template.PERSON_LIST_INVSTR_FLAT.getName())
                .forCISKey("12345678901");
        Task task = request.getRequestObject().getTask();
        assertThat(task.getTempl().toString(), is("BTFG$UI_PERSON_LIST.USER#INVSTR_FLAT"));
        assertThat(task.getParamList().getParam().get(0).getName(), is(AvaloqUtils.PARAM_PERSON_LIST));
        assertThat(((IdFld) task.getParamList().getParam().get(0).getValList().getVal().get(0)).getExtlVal().getKey(),
                is(AvaloqUtils.PARAM_CIS_ID));
        assertThat(((IdFld) task.getParamList().getParam().get(0).getValList().getVal().get(0)).getExtlVal().getVal(),
                is("12345678901"));
    }

    @Test
    public void testforClientTermDeposit() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forClientTermDeposit("client");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(ReportGenerationServiceImpl.PARAM_NAME_PORTFOLIO_ID, param.getName());
        assertEquals("client", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforContId() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forContId("contId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.CONT_ID, param.getName());
        assertEquals("contId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforCustomerList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forCustomerList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_PERSON_LIST_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getExtlVal().getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getExtlVal().getVal());
    }

    @Test
    public void testforDateTime() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forDateTime("paramName", new DateTime("2017-01-01"));
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals("paramName", param.getName());
        assertEquals("2017-01-01", ((DateFld) param.getVal()).getVal().toString());
    }

    @Test
    public void testforDateTimeOptional_whenNotNull_thenParamAdded() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forDateTimeOptional("paramName", new DateTime("2017-01-01"));
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals("paramName", param.getName());
        assertEquals("2017-01-01", ((DateFld) param.getVal()).getVal().toString());
    }

    @Test
    public void testforDateTimeOptional_whenNull_thenParamNotAdded() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forDateTimeOptional("paramName", null);
        assertNull(request.getRequestObject().getTask().getParamList());
    }

    @Test
    public void testforDocId() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forDocId("docId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_DOC_ID, param.getName());
        assertEquals("docId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforDocumentIdList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forDocumentIdList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.DOCUMENT_ID_LIST, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforEffectiveDate() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forEffectiveDate(new DateTime("2017-01-01"));
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(ReportGenerationServiceImpl.PARAM_NAME_EFFECTIVE_DATE, param.getName());
        assertEquals("2017-01-01", ((DateFld) param.getVal()).getVal().toString());
    }

    @Test
    public void testforExternalRefId() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forExternalRefId(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.EXTERNAL_REFERENCE_NR, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforF1OeIds_whenSingleParam_thenValAdded() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forF1OeIds(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_OE_LIST_FI_ID, param.getName());
        assertEquals("123", ((IdFld) param.getVal()).getVal());
    }

    @Test
    public void testforF1OeIds_whenMultipleParams_thenValListAdded() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forF1OeIds(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_OE_LIST_FI_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforFinancialYear() throws ParseException {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        request.forFinancialYear(formatter.parse("01/06/2017"));
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_FINANCIAL_YEAR, param.getName());
        assertEquals("2017-06-01", ((DateFld) param.getVal()).getVal().toString());
    }

    @Test
    public void testforIncludeAccount() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forIncludeAccount("123", "456");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_INCLUDE_ACCOUNT_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforIncludeAccountList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forIncludeAccountList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_INCLUDE_ACCOUNT_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforIncludeProductList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forIncludeProductList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_INCLUDE_PRODUCT_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforInvestmentManager() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forInvestmentManager("managerId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_INVESTMENT_MANAGER_ID, param.getName());
        assertEquals("managerId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforInvestmentManagerOeId() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forInvestmentManagerOeId("managerId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_INVESTMENT_MANAGER_OE_ID, param.getName());
        assertEquals("managerId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforInvestmentPolicyStatementId() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forInvestmentPolicyStatementId("ipsId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_INVESTMENT_POLICY_STATEMENT_ID, param.getName());
        assertEquals("ipsId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testformPersonId() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.formPersonID("123", "456");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_PERSON, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testformSearchQuery() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.formSearchQuery("searchParamVal");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_SEARCH_KEY, param.getName());
        assertEquals("searchParamVal", ((TextFld) param.getVal()).getVal());
    }

    @Test
    public void testforOeIds() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forOeIds(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.OE_LIST_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforOrderTypeList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forOrderTypeList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ORDER_TYPE_LIST_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getExtlVal().getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getExtlVal().getVal());
    }

    @Test
    public void testforPastTransactions_whenAllParams_thenParamsAdded() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forPastTransactions("portfolioId", "2017-02-01", "2017-03-01");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ACCOUNT_ID, param.getName());
        assertEquals("portfolioId", ((IdFld) param.getValList().getVal().get(0)).getVal());
        Param param2 = request.getRequestObject().getTask().getParamList().getParam().get(1);
        assertEquals(AvaloqUtils.PARAM_VAL_DATE_FROM, param2.getName());
        assertEquals("2017-02-01", ((DateFld) param2.getValList().getVal().get(0)).getVal().toString());
        Param param3 = request.getRequestObject().getTask().getParamList().getParam().get(2);
        assertEquals(AvaloqUtils.PARAM_VAL_DATE_TO, param3.getName());
        assertEquals("2017-03-01", ((DateFld) param3.getValList().getVal().get(0)).getVal().toString());
    }

    @Test
    public void testforPastTransactions_whenOnlyPortfolioId_thenOnlyPortfolioParamAdded() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forPastTransactions("portfolioId", null, null);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        assertEquals(1, request.getRequestObject().getTask().getParamList().getParam().size());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_ACCOUNT_ID, param.getName());
        assertEquals("portfolioId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforProductList() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forProductList(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_PRODUCT_LIST, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforProfileIds() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forProfileIds(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_JOB_PROFILE_USER, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforRefDocListId() {
        ArrayList<String> list = new ArrayList<>();
        list.add("123");
        list.add("456");
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forRefDocListId(list);
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_REF_DOC_LIST_ID, param.getName());
        assertEquals(2, param.getValList().getVal().size());
        assertEquals("123", ((IdFld) param.getValList().getVal().get(0)).getVal());
        assertEquals("456", ((IdFld) param.getValList().getVal().get(1)).getVal());
    }

    @Test
    public void testforScheduledTransactionAccount() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forScheduledTransactionAccount("accountId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_INCLUDE_ACCOUNT_ID, param.getName());
        assertEquals("accountId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testforTransactionCategory() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.forTransactionCategory("categoryId");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(AvaloqUtils.PARAM_CATEGORY_ID, param.getName());
        assertEquals("categoryId", ((IdFld) param.getValList().getVal().get(0)).getVal());
    }

    @Test
    public void testfromDate() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.fromDate("2017-02-01");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(Constants.PARAM_NAME_START_DATE, param.getName());
        assertEquals("2017-02-01", ((DateFld) param.getVal()).getVal().toString());
    }

    @Test
    public void testtoDate() {
        AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template");
        request.toDate("2017-03-01");
        assertNotNull(request.getRequestObject().getTask().getParamList());
        Param param = request.getRequestObject().getTask().getParamList().getParam().get(0);
        assertEquals(Constants.PARAM_NAME_END_DATE, param.getName());
        assertEquals("2017-03-01", ((DateFld) param.getVal()).getVal().toString());
    }

	@Test
	public void testJmsMessageMode()
	{
		AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template").forJMSResponse();
		RepReq reportRequest = request.getRequestObject();
		assertThat(reportRequest, is(notNullValue()));
		assertThat(reportRequest.getMode(), is(notNullValue()));
		assertThat(reportRequest.getMode().getRes(), is(Res.ASYNC));
		assertNull(reportRequest.getMode().getMaxChunkSz());
	}

	@Test
	public void testForAsyncResponse()
	{
		AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template").processAsynchronously();
		RepReq reportRequest = request.getRequestObject();
		assertThat(reportRequest, is(notNullValue()));
		assertThat(reportRequest.getMode(), is(notNullValue()));
		assertThat(reportRequest.getMode().getExec(), is(Exec.ASYNC));
	}

	@Test
	public void testChunkedResponse()
	{
		AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template").forChunkedResponse(5242880);
		RepReq reportRequest = request.getRequestObject();
		assertThat(reportRequest, is(notNullValue()));
		assertThat(reportRequest.getMode(), is(notNullValue()));
		assertThat(reportRequest.getMode().getMaxChunkSz(), is(5242880));
	}

	//private void assertChunkSizeIs(Integer chunkSize)
	@Test
	public void testFullJmsResponse()
	{
		AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template").forChunkedResponse(5242880).forJMSResponse().processAsynchronously();
		RepReq reportRequest = request.getRequestObject();
		assertThat(reportRequest, is(notNullValue()));
		assertThat(reportRequest.getMode(), is(notNullValue()));
		assertThat(reportRequest.getMode().getMaxChunkSz(), is(5242880));
		assertThat(reportRequest.getMode().getExec(), is(Exec.ASYNC));
		assertThat(reportRequest.getMode().getRes(), is(Res.ASYNC));
	}

	@Test
	public void testFullJmsResponseWithCustomSize()
	{
		AvaloqReportRequest request = new AvaloqReportRequest("A Fake Template").forJMSResponse().forChunkedResponse(5242880).processAsynchronously();
		RepReq reportRequest = request.getRequestObject();
		assertThat(reportRequest, is(notNullValue()));
		assertThat(reportRequest.getMode(), is(notNullValue()));
		assertThat(reportRequest.getMode().getMaxChunkSz(), is(5242880));
		assertThat(reportRequest.getMode().getExec(), is(Exec.ASYNC));
		assertThat(reportRequest.getMode().getRes(), is(Res.ASYNC));
	}

    @Test
    public void testCreateQueryForDateTime()
    {
        DateTime dateTimeVal = new DateTime(2015, 1, 20, 0, 0,0);
        DateTimeFld value = (DateTimeFld)AvaloqReportRequest.createQueryFor("rep:dateTimeVal", dateTimeVal.toString());
        assertNotNull(value);
        assertEquals("2015-01-20T00:00:00.000", value.getVal().toString());
    }

    @Test
    public void testFromCreationTimestamp()
    {
        DateTime dateTimeVal = new DateTime(2015, 1, 20, 0, 0,0);
        AvaloqReportRequest request = new AvaloqReportRequest("TEMPLATE");
        request.fromCreationTimestamp(dateTimeVal.toString());
        assertNotNull(request.getParamList());
        assertThat(request.getParamList().getParam().size(), is(1));

        assertThat(request.getParamList().getParam().get(0), is(notNullValue()));
        assertThat(request.getParamList().getParam().get(0).getName(), is(AvaloqUtils.CREATION_TIMESTAMP_FROM));
        assertThat(request.getParamList().getParam().get(0).getVal(), is(notNullValue()));

    }

    @Test
    public void testToCreationTimestamp()
    {
        DateTime dateTimeVal = new DateTime(2015, 1, 20, 0, 0,0);
        AvaloqReportRequest request = new AvaloqReportRequest("TEMPLATE");
        request.toCreationTimestamp(dateTimeVal.toString());
        assertNotNull(request.getParamList());
        assertThat(request.getParamList().getParam().size(), is(1));

        assertThat(request.getParamList().getParam().get(0), is(notNullValue()));
        assertThat(request.getParamList().getParam().get(0).getName(), is(AvaloqUtils.CREATION_TIMESTAMP_TO));
        assertThat(request.getParamList().getParam().get(0).getVal(), is(notNullValue()));
    }

    @Test
    public void testCreateQueryForBpNumberParamList()
    {
        //setting Avaloq request parameters
        avaloqParameter = AccountParams.PARAM_ACCOUNT_NO;
        avaloqTemplate = AccountTemplate.ACCOUNT;
        avaloqReportRequest = new AvaloqReportRequestImpl(avaloqTemplate);

        ArrayList <String> list = new ArrayList <>();
        list.add("123");
        com.bt.nextgen.service.request.AvaloqRequest request = avaloqReportRequest.forParam(avaloqParameter, list);
        assertNotNull(request);
        assertEquals(false, request.isApplicationLevelRequest());
        assertNotNull(request.getRequestObject());
        assertEquals("BTFG$UI_BP.BP#BP_DET", request.getTemplate().getTemplateName());
        assertEquals("ACCOUNT", request.getTemplate().toString());
        assertEquals(AvaloqOperation.REP_REQ, request.getAvaloqOperation());
        assertNotNull(request.getRequestObject().getHdr());
        assertEquals("123", ((IdFld)request.getRequestObject().getTask().getParamList().getParam().get(0).getValList().getVal().get(0)).getExtlVal().getVal());
    }
}
