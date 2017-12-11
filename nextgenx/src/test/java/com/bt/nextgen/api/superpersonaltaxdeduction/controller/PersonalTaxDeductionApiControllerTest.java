package com.bt.nextgen.api.superpersonaltaxdeduction.controller;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalDeductionNoticesDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalTaxDeductionNoticeTrxnDto;
import com.bt.nextgen.api.superpersonaltaxdeduction.service.PersonalTaxDeductionDtoService;
import com.bt.nextgen.api.superpersonaltaxdeduction.service.PersonalTaxDeductionNoticeValidator;
import com.bt.nextgen.api.superpersonaltaxdeduction.service.SavePersonalTaxDeductionDtoService;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonalTaxDeductionApiControllerTest {

    @InjectMocks
    private PersonalTaxDeductionApiController apiController;

    @Mock
    private PersonalTaxDeductionDtoService personalTaxDeductionDtoService;

    @Mock
    private SavePersonalTaxDeductionDtoService savePersonalTaxDeductionDtoService;

    @Mock
    private PersonalTaxDeductionNoticeValidator validator;

    @Mock
    private ApiResponse apiResponse;

    private final String ACCOUNT_ID = "676AA77A418C5BC1AB5E2DEBC7E023DA15A6C416331D7421";


    @Captor
    private ArgumentCaptor<List<ApiSearchCriteria>> listArgumentCaptor;

    private static PersonalTaxDeductionNoticeTrxnDto personalTaxDeductionNoticeTrxnDtoResponse;


    @Test
    public void testViewPersonalTaxDeduction() {
        Mockito.when(personalTaxDeductionDtoService.search(anyList(), any(ServiceErrorsImpl.class)))
                .thenReturn(new PersonalDeductionNoticesDto());

        ApiResponse apiResponse = apiController.viewPersonalTaxDeduction(EncodedString.fromPlainText("12345")
                .toString(), "2016-10-10");
        assertThat(apiResponse, is(notNullValue()));

        verify(personalTaxDeductionDtoService).search(anyListOf(ApiSearchCriteria.class), any(ServiceErrorsImpl.class));
        verify(personalTaxDeductionDtoService).search(listArgumentCaptor.capture(), any(ServiceErrorsImpl.class));

        List<ApiSearchCriteria> apiSearchCriteriaList = listArgumentCaptor.getValue();
        ApiSearchCriteria apiSearchCriteria1 = apiSearchCriteriaList.get(0);
        assertThat(apiSearchCriteria1.getProperty(), is("accountId"));
        assertThat(apiSearchCriteria1.getValue(), is("12345"));

        ApiSearchCriteria apiSearchCriteria2 = apiSearchCriteriaList.get(1);
        assertThat(apiSearchCriteria2.getProperty(), is("financialYearDate"));
        assertThat(apiSearchCriteria2.getValue(), is("2016-10-10"));

        assertThat(apiSearchCriteriaList.size(), is(2));
    }

    @BeforeClass
    public static void init() {
        personalTaxDeductionNoticeTrxnDtoResponse = new PersonalTaxDeductionNoticeTrxnDto();
        personalTaxDeductionNoticeTrxnDtoResponse.setTransactionStatus("saved");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createOrVaryDeductionNoticeWithEmptyAccountId() {
        apiController.createOrVaryDeductionNotice(null, null, null);
    }

    @Test
    public void createOrVaryDeductionNotice() {
        final PersonalTaxDeductionNoticeTrxnDto taxTrxnDtoRequest = new PersonalTaxDeductionNoticeTrxnDto();

        taxTrxnDtoRequest.setAmount(new BigDecimal("100"));
        when(validator.validate(Matchers.eq(taxTrxnDtoRequest), any(ServiceErrorsImpl.class))).thenReturn(personalTaxDeductionNoticeTrxnDtoResponse);
        when(savePersonalTaxDeductionDtoService.submit(any(PersonalTaxDeductionNoticeTrxnDto.class),
                any(ServiceErrorsImpl.class))).thenReturn(personalTaxDeductionNoticeTrxnDtoResponse);

        ApiResponse apiResponse = apiController.createOrVaryDeductionNotice(ACCOUNT_ID, "2016-07-01", taxTrxnDtoRequest);
        verify(savePersonalTaxDeductionDtoService, times(1)).submit(any(PersonalTaxDeductionNoticeTrxnDto.class),
                any(ServiceErrors.class));
        assertThat(apiResponse, is(notNullValue()));

        PersonalTaxDeductionNoticeTrxnDto resultDtoObject = (PersonalTaxDeductionNoticeTrxnDto) apiResponse.getData();
        assertThat("TransactionStatus", resultDtoObject.getTransactionStatus(), is("saved"));
    }
}