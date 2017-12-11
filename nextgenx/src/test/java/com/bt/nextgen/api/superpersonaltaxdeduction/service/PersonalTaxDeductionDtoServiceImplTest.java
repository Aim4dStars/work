package com.bt.nextgen.api.superpersonaltaxdeduction.service;

import com.bt.nextgen.api.superpersonaltaxdeduction.model.PersonalDeductionNoticesDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeduction;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionIntegrationService;
import com.bt.nextgen.service.avaloq.superpersonaltaxdeduction.PersonalTaxDeductionNotices;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType.STRING;
import static com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation.EQUALS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonalTaxDeductionDtoServiceImplTest {

    @InjectMocks
    private PersonalTaxDeductionDtoServiceImpl personalTaxDeductionDtoService;

    @Mock
    private PersonalTaxDeductionIntegrationService personalTaxDeductionIntegrationService;

    @Mock
    private AccountIntegrationService accountService;

    private List<ApiSearchCriteria> criteriaList;
    private ServiceErrors serviceErrors;
    private WrapAccount account;
    private PersonalTaxDeduction personalTaxDeduction;
    private PersonalTaxDeductionNotices personalTaxDeductionNotices;
    private List<PersonalTaxDeductionNotices> notices;

    @Before
    public void setup() {
        serviceErrors = new ServiceErrorsImpl();
        notices = new ArrayList<>();
        criteriaList = new ArrayList<>();
        criteriaList.add(new ApiSearchCriteria("accountId", EQUALS, "12345", STRING));
        criteriaList.add(new ApiSearchCriteria("financialYearDate", EQUALS, "2016-10-10", STRING));

        account = Mockito.mock(WrapAccount.class);
        when(account.getAccountNumber()).thenReturn("12345645");

        personalTaxDeductionNotices = mock(PersonalTaxDeductionNotices.class);
        when(personalTaxDeductionNotices.getDocId()).thenReturn(Long.valueOf(100));
        when(personalTaxDeductionNotices.getNoticeAmount()).thenReturn(BigDecimal.valueOf(1000));
        notices.add(personalTaxDeductionNotices);

        personalTaxDeduction = mock(PersonalTaxDeduction.class);
        when(personalTaxDeduction.getTaxDeductionNotices()).thenReturn(notices);

        when(accountService.loadWrapAccountWithoutContainers(any(AccountKey.class), any(ServiceErrors.class)))
                .thenReturn(account);
        when(personalTaxDeductionIntegrationService.getPersonalTaxDeductionNotices(anyString(), isNull(String.class),
                any(DateTime.class), any(DateTime.class), any(ServiceErrors.class))).thenReturn(personalTaxDeduction);
    }

    @Test
    public void testSearch() {
        PersonalDeductionNoticesDto result = personalTaxDeductionDtoService.search(criteriaList, serviceErrors);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.getTotalNotifiedAmount(), BigDecimal.valueOf(1000));
        Assert.assertEquals(result.getNotices().size(), 1);
        Assert.assertEquals(result.getNotices().get(0).getNoticeType(), "Notice");
        Assert.assertEquals(result.getNotices().get(0).getDocId(), "100");
    }
}
