package com.bt.nextgen.service.avaloq.modelportfolio.rebalance;

import com.bt.nextgen.core.exception.ValidationException;
import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.core.validation.ValidationError.ErrorType;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationConverter;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceAccount;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.RebalanceExclusion;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.btfin.abs.trxservice.rebaldet.v1_0.RebalDetReq;
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

@RunWith(MockitoJUnitRunner.class)
public class ModelExclusionConverterTest {
    @InjectMocks
    private ModelPortfolioExclusionConverter converter;

    @Mock
    private TransactionValidationConverter validationConverter;

    @Before
    public void setUp() {

    }

    @Test
    public void whenExclusionListIsEmpty_thenRequestIsEmpty() throws Exception {
        List<RebalanceExclusion> exclusions = new ArrayList<>();
        List<RebalanceAccount> accounts = new ArrayList<>();
        RebalDetReq request = converter.toExcludeRequest(accounts, exclusions);
        Assert.assertNotNull(request);
        Assert.assertEquals(0, request.getData().getRebalDet().getRebal().size());
    }

    @Test
    public void whenAccountNotFound_thenAccountExclusionIsOmitted() throws Exception {
        RebalanceExclusionImpl exclusion = new RebalanceExclusionImpl(AccountKey.valueOf("accountKey"), true, "reason");
        List<RebalanceExclusion> exclusions = new ArrayList<>();
        exclusions.add(exclusion);
        List<RebalanceAccount> accounts = new ArrayList<>();
        RebalDetReq request = converter.toExcludeRequest(accounts, exclusions);
        Assert.assertNotNull(request);
        Assert.assertEquals(0, request.getData().getRebalDet().getRebal().size());
    }

    @Test
    public void whenAccountFound_thenAccountIsInRequest() throws Exception {
        RebalanceExclusionImpl exclusion = new RebalanceExclusionImpl(AccountKey.valueOf("accountKey"), true, "reason");
        List<RebalanceExclusion> exclusions = new ArrayList<>();
        exclusions.add(exclusion);
        List<RebalanceAccount> accounts = new ArrayList<>();
        RebalanceAccountImpl account = new RebalanceAccountImpl();
        accounts.add(account);
        account.setAccount(AccountKey.valueOf("accountKey"));
        account.setRebalDocId("123456");
        RebalDetReq request = converter.toExcludeRequest(accounts, exclusions);
        Assert.assertNotNull(request);
        Assert.assertEquals(1, request.getData().getRebalDet().getRebal().size());
        Assert.assertEquals(BigDecimal.valueOf(123456),
                request.getData().getRebalDet().getRebal().get(0).getRebalDocId().getVal());
        Assert.assertEquals(false, request.getData().getRebalDet().getRebal().get(0).getDoExcl().isVal());
    }

    @Test
    public void whenValidationErrors_thenExceptionIsThrown() throws Exception {
        ModelPortfolioExclusionResponseImpl response = new ModelPortfolioExclusionResponseImpl();

        ValidationError valErr = new ValidationError("errorId", "errorField", "ErrorMessage", ErrorType.ERROR);
        List<ValidationError> errors = new ArrayList<>();
        errors.add(valErr);

        Mockito.when(validationConverter.toValidationError(Mockito.any(TransactionResponse.class), Mockito.anyList()))
                .thenReturn(errors);
        try {
            converter.processErrors(response);
            Assert.fail();
        } catch (ValidationException e) {
            Assert.assertEquals("Model exclusion failed validation", e.getMessage());
        }
    }

    @Test
    public void whenNoValidationErrors_thenErrorListIsEmpty() throws Exception {
        ModelPortfolioExclusionResponseImpl response = new ModelPortfolioExclusionResponseImpl();
        List<ValidationError> errors = new ArrayList<>();

        Mockito.when(validationConverter.toValidationError(Mockito.any(TransactionResponse.class), Mockito.anyList()))
                .thenReturn(errors);
        List<ValidationError> valErrors = converter.processErrors(response);
        Assert.assertEquals(0, valErrors.size());
    }

    @Test
    public void whenValidationWarnings_thenWarningIsInErrorList() throws Exception {
        ModelPortfolioExclusionResponseImpl response = new ModelPortfolioExclusionResponseImpl();

        ValidationError valErr = new ValidationError("errorId", "errorField", "ErrorMessage", ErrorType.WARNING);
        List<ValidationError> errors = new ArrayList<>();
        errors.add(valErr);

        Mockito.when(validationConverter.toValidationError(Mockito.any(TransactionResponse.class), Mockito.anyList()))
                .thenReturn(errors);
        List<ValidationError> valErrors = converter.processErrors(response);
        Assert.assertEquals(1, valErrors.size());
    }
}
