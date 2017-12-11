package com.bt.nextgen.api.modelportfolio.v2.util.rebalance;

import com.bt.nextgen.service.avaloq.account.PensionAccountDetailImpl;
import com.bt.nextgen.service.avaloq.account.PensionType;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.btfin.panorama.service.integration.account.WrapAccount;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ModelAccountTypeHelperTest {

    private static final String SUPER_DESCRIPTION = "Superannuation";
    private static final String PENSION_DESCRIPTION = "Pension";
    private static final String PENSION_TTR_DESCRIPTION = "Pension (TTR)";

    @Test
    public void testGetDescription_whenDifferentAccountTypes_thenCorrectDescriptionReturned() {
        // Investment
        WrapAccount account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.Individual);

        Assert.assertEquals(AccountStructureType.Individual.name(), ModelAccountTypeHelper.getAccountTypeDescription(account));

        // Super
        account = Mockito.mock(WrapAccount.class);
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);

        Assert.assertEquals(SUPER_DESCRIPTION, ModelAccountTypeHelper.getAccountTypeDescription(account));

        // Pension
        account = Mockito.mock(PensionAccountDetailImpl.class);
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        Mockito.when(((PensionAccountDetailImpl) account).getPensionType()).thenReturn(PensionType.STANDARD);

        Assert.assertEquals(PENSION_DESCRIPTION, ModelAccountTypeHelper.getAccountTypeDescription(account));

        // Pension TTR
        account = Mockito.mock(PensionAccountDetailImpl.class);
        Mockito.when(account.getAccountStructureType()).thenReturn(AccountStructureType.SUPER);
        Mockito.when(((PensionAccountDetailImpl) account).getPensionType()).thenReturn(PensionType.TTR);

        Assert.assertEquals(PENSION_TTR_DESCRIPTION, ModelAccountTypeHelper.getAccountTypeDescription(account));
    }

    @Test
    public void testGetAccountType_whenModelDetailPresent_thenAccountTypeReturned() {
        // Investment
        Assert.assertEquals(ModelType.INVESTMENT.getDisplayValue(),
                ModelAccountTypeHelper.getModelAccountType(ModelType.INVESTMENT.getId()));

        // Super
        Assert.assertEquals(ModelType.SUPERANNUATION.getDisplayValue(),
                ModelAccountTypeHelper.getModelAccountType(ModelType.SUPERANNUATION.getId()));
    }

    @Test
    public void testGetAccountType_whenNoModelDetailPresent_thenEmptyReturned() {
        Assert.assertEquals("", ModelAccountTypeHelper.getModelAccountType(null));
        Assert.assertEquals("", ModelAccountTypeHelper.getModelAccountType("DOES_NOT_COMPUTE"));
    }
}