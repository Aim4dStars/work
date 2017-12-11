package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class HoldingApplicationHelperTest {

    ApplicationDocument applicationDocument = mock(ApplicationDocument.class);

    @Test
    public void testCheckAccountTypeForNewIndividualSMSF() throws Exception {
        when(applicationDocument.getOrderType()).thenReturn(OrderType.NewIndividualSMSF.getOrderType());
        when(applicationDocument.getCompanyACN()).thenReturn(null);

        assertThat(HoldingApplicationHelper.checkAccountTypeForNewSmsf(applicationDocument), is("newIndividualSMSF"));
    }

    @Test
    public void testCheckAccountTypeForNewCorporateSMSF() throws Exception {
        when(applicationDocument.getOrderType()).thenReturn(OrderType.NewCorporateSMSF.getOrderType());
        when(applicationDocument.getCompanyACN()).thenReturn("111");

        assertThat(HoldingApplicationHelper.checkAccountTypeForNewSmsf(applicationDocument), is("newCorporateSMSF"));
    }

    @Test
    public void testCheckAccountTypeForExistingIndividualSMSF() throws Exception {
        when(applicationDocument.getOrderType()).thenReturn(null);
        when(applicationDocument.getCompanyACN()).thenReturn(null);

        assertThat(HoldingApplicationHelper.checkAccountTypeForNewSmsf(applicationDocument), isEmptyOrNullString());
    }
}