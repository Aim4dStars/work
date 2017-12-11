/**
 *
 */
package com.bt.nextgen.api.account.v2.service;

import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.api.account.v2.model.TermDepositAccountDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.TermDepositInterface;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountType;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.HoldingKey;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import com.bt.nextgen.service.integration.termdeposit.TermDepositIntegrationService;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author L072463
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TermDepositAccountDtoServiceTest {

    @InjectMocks
    TermDepositAccountDtoServiceImpl termDepositAccountDtoServiceImpl;

    @Mock
    private TermDepositIntegrationService termDepositIntegrationService;

    @Mock
    private AccountIntegrationService accountService;

    @Mock
    private PortfolioIntegrationService portfolioService;

    @Mock
    private StaticIntegrationService staticIntegrationService;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {

    }

    /**
     * Test method for
     * {@link com.bt.nextgen.api.account.v2.service.TermDepositAccountDtoServiceImpl#update(com.bt.nextgen.portfolio.api.model.TermDepositAccountDto, com.bt.nextgen.service.ServiceErrors)}
     * .
     */
    @Test
    public final void testUpdate() {
        Code code = Mockito.mock(Code.class);
        Mockito.when(code.getCodeId()).thenReturn("codeId");
        Mockito.when(
                staticIntegrationService.loadCodeByUserId(Mockito.any(CodeCategory.class), Mockito.anyString(),
                        Mockito.any(ServiceErrorsImpl.class))).thenReturn(code);

        Mockito.when(
                termDepositIntegrationService.updateTermDeposit(Mockito.any(TermDepositInterface.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(true);

        WrapAccountDetail accDetails = Mockito.mock(WrapAccountDetail.class);
        Mockito.when(accDetails.getAccountType()).thenReturn(AccountType.TERM_DEPOSIT);
        Mockito.when(accDetails.getAccountName()).thenReturn("accountName");

        Mockito.when(
                accountService.loadWrapAccountDetail(Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class),
                        Mockito.any(ServiceErrors.class))).thenReturn(accDetails);

        WrapAccountValuation valuation = Mockito.mock(WrapAccountValuation.class);
        Mockito.when(valuation.getSubAccountValuations()).thenAnswer(new Answer<List<SubAccountValuation>>() {

            @Override
            public List<SubAccountValuation> answer(InvocationOnMock invocation) throws Throwable {
                List<SubAccountValuation> accountValuations = new ArrayList<SubAccountValuation>();

                TermDepositAccountValuation termDepositAccountValuation = new TermDepositAccountValuation() {
                    @Override
                    public List<AccountHolding> getHoldings() {
                        TermDepositHoldingImpl td = new TermDepositHoldingImpl();
                        td.setHoldingKey(HoldingKey.valueOf(
                                EncodedString.toPlainText("6F7AE7E41CBD180BA3245476B77FECE1BF7E1E388109A387"), "Test Td"));
                        td.setMarketValue(new BigDecimal(10000));
                        td.setMaturityDate(new DateTime(2010, 10, 2, 2, 2));

                        return Collections.singletonList((AccountHolding) td);
                    }

                    @Override
                    public BigDecimal getMarketValue() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                    @Override
                    public BigDecimal getBalance() {
                        return new BigDecimal(10000);
                    }

                    @Override
                    public AssetType getAssetType() {
                        return AssetType.TERM_DEPOSIT;
                    }

                    @Override
                    public BigDecimal getAccruedIncome() {
                        // TODO Auto-generated method stub
                        return null;
                    }
                };

                accountValuations.add(termDepositAccountValuation);

                return accountValuations;
            }
        });

        Mockito.when(portfolioService.loadWrapAccountValuation(
                Mockito.any(com.bt.nextgen.service.integration.account.AccountKey.class), Mockito.any(DateTime.class),
                Mockito.any(ServiceErrors.class))).thenReturn(valuation);

        TermDepositAccountDto termDepositAccountDto = new TermDepositAccountDto(new AccountKey(
                "80A063E31114DEA03439E0BD2F82ABFCB056BED9910DDAEA"), "6F7AE7E41CBD180BA3245476B77FECE1BF7E1E388109A387",
                "renewModeId");

        termDepositAccountDto = termDepositAccountDtoServiceImpl.update(termDepositAccountDto, null);

        Assert.assertEquals(termDepositAccountDto.getTermDuration(), ApiFormatter.asShortDate(new DateTime(2010, 10, 2, 2, 2)));
        Assert.assertEquals(termDepositAccountDto.getInvestmentAmount(), "10000");

    }

}

