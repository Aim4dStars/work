package com.bt.nextgen.api.draftaccount.builder.v3;

import com.bt.nextgen.api.draftaccount.schemas.v1.base.PaymentAuthorityEnum;
import ns.btfin_com.authorityprofile.v1_0.AuthorityTypeType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AuthorityTypeBuilderTest {

    @InjectMocks
    private AuthorityTypeBuilder authorityTypeBuilder;

    @Test
    public void shouldReturnLimitedChangeAuthorityTypeForNoPayments() {
        assertEquals(AuthorityTypeType.LIMITED_CHANGE, authorityTypeBuilder.getAuthorityType(PaymentAuthorityEnum.NOPAYMENTS));
    }

    @Test
    public void shouldReturnLimitedTransactionAuthorityTypeForLinkedAccounts() {
        assertEquals(AuthorityTypeType.LIMITED_TRANSACTION, authorityTypeBuilder.getAuthorityType(PaymentAuthorityEnum.LINKEDACCOUNTSONLY));
    }

    @Test
    public void shouldReturnApplicationApprovalAuthorityTypeForApplicationApproval() {
        assertEquals(AuthorityTypeType.APPLICATION_APPROVAL, authorityTypeBuilder.getInvestorAuthorityProfileTypeForApplicationApproval().getAuthorityType());
    }

    @Test
    public void shouldReturnFullTransactionAuthorityTypeForAllPayments() {
        assertEquals(AuthorityTypeType.FULL_TRANSACTION, authorityTypeBuilder.getAuthorityType(PaymentAuthorityEnum.ALLPAYMENTS));
    }
}
