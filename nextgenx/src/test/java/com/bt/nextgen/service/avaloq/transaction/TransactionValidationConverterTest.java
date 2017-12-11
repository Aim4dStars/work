package com.bt.nextgen.service.avaloq.transaction;

import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.Properties;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TransactionValidationConverterTest {

    @InjectMocks
    TransactionValidationConverter converter;

    @Mock
    protected CmsService cmsService;

    @Test
    public void test_toValidationError_errorIdMapped() {

        when(cmsService.getDynamicContent("mappedErrorId", null)).thenReturn("mappedErrorMessage");

        Properties.all().setProperty("errorcode.errorId", "mappedErrorId");
        List<TransactionValidation> txnValidations = new ArrayList<>();
        TransactionValidation val = mock(TransactionValidation.class);
        when(val.getErrorId()).thenReturn("errorId");
        when(val.getExternalKey()).thenReturn(null);
        when(val.getErrorMessage()).thenReturn("errMesage");
        txnValidations.add(val);
        List<ValidationError> results = converter.toValidationError(null, txnValidations);
        Assert.assertTrue(results.size() == 1);
        Assert.assertEquals("mappedErrorMessage", results.get(0).getMessage());
    }

    @Test
    public void test_toValidationError_errorIdsNotMapped() {

        when(cmsService.getDynamicContent("mappedErrorId", null)).thenReturn("mappedErrorMessage");

        Properties.all().setProperty("errorcode.errorId", "mappedErrorId");
        List<TransactionValidation> txnValidations = new ArrayList<>();
        TransactionValidation val = mock(TransactionValidation.class);
        when(val.getErrorId()).thenReturn("errorId2");
        when(val.getExternalKey()).thenReturn(null);
        when(val.getErrorMessage()).thenReturn("errMessage");
        txnValidations.add(val);
        List<ValidationError> results = converter.toValidationError(null, txnValidations);
        Assert.assertTrue(results.size() == 1);
        Assert.assertEquals("errMessage", results.get(0).getMessage());
    }

    @Test
    public void test_toValidationError_errorExtlIdMapped() {

        when(cmsService.getDynamicContent("mappedErrorId", null)).thenReturn("mappedErrorMessage");

        Properties.all().setProperty("errorcode.extlKey", "mappedErrorId");
        List<TransactionValidation> txnValidations = new ArrayList<>();
        TransactionValidation val = mock(TransactionValidation.class);
        when(val.getErrorId()).thenReturn("errorId");
        when(val.getExternalKey()).thenReturn("extlKey");
        when(val.getErrorMessage()).thenReturn("errMesage");
        txnValidations.add(val);
        List<ValidationError> results = converter.toValidationError(null, txnValidations);
        Assert.assertTrue(results.size() == 1);
        Assert.assertEquals("mappedErrorMessage", results.get(0).getMessage());
    }
}
