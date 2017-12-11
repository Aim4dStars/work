package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.avaloq.modelportfolio.RebalanceAction;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.btfin.abs.trxservice.rebal.v1_0.RebalReq;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ModelRebalanceConverterTest {
    @InjectMocks
    private ModelRebalanceConverter converter;

    @Mock
    private TransactionValidationConverter validationConverter;

    @Before
    public void setUp() {

    }

    @Test
    public void whenRequestSuppliedIsEmpty_thenMatchingMessageProduced() throws Exception {

        RebalReq request = converter.toSubmitRequest(IpsKey.valueOf("ipsId"), RebalanceAction.SCAN);
        Assert.assertNotNull(request);
        Assert.assertEquals("ipsId", request.getData().getIps().getVal());
        Assert.assertEquals(RebalanceAction.SCAN.getCode(), request.getData().getRebalTrig().getExtlVal().getVal());
    }

    @Test
    public void whenValidationErrors_thenExceptionIsThrown() throws Exception {
        ModelRebalanceUpdateResponseImpl response = new ModelRebalanceUpdateResponseImpl();

        ValidationError valErr = new ValidationError("errorId", "errorField", "ErrorMessage", ErrorType.ERROR);
        List<ValidationError> errors = new ArrayList<>();
        errors.add(valErr);

        Mockito.when(validationConverter.toValidationError(Mockito.any(TransactionResponse.class), Mockito.anyList()))
                .thenReturn(errors);
        try {
            converter.processErrors(response);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Model rebalance submission failed validation", e.getMessage());
        }
    }

    @Test
    public void whenNoValidationErrors_thenErrorListIsEmpty() throws Exception {
        ModelRebalanceUpdateResponseImpl response = new ModelRebalanceUpdateResponseImpl();
        List<ValidationError> errors = new ArrayList<>();

        Mockito.when(validationConverter.toValidationError(Mockito.any(TransactionResponse.class), Mockito.anyList()))
                .thenReturn(errors);
        List<ValidationError> valErrors = converter.processErrors(response);
        Assert.assertEquals(0, valErrors.size());
    }

    @Test
    public void whenValidationWarnings_thenWarningIsInErrorList() throws Exception {
        ModelRebalanceUpdateResponseImpl response = new ModelRebalanceUpdateResponseImpl();

        ValidationError valErr = new ValidationError("errorId", "errorField", "ErrorMessage", ErrorType.WARNING);
        List<ValidationError> errors = new ArrayList<>();
        errors.add(valErr);

        Mockito.when(validationConverter.toValidationError(Mockito.any(TransactionResponse.class), Mockito.anyList()))
                .thenReturn(errors);
        List<ValidationError> valErrors = converter.processErrors(response);
        Assert.assertEquals(1, valErrors.size());
    }
}
