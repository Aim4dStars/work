package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.AbstractJsonObjectMapperTest;
import com.bt.nextgen.api.draftaccount.model.form.ILinkedAccountForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.linkedaccounts.LinkedAccountsApplication;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the {@code LinkedAccountsForm} class.
 */
public class LinkedAccountsFormTest extends AbstractJsonObjectMapperTest<LinkedAccountsApplication> {
    private LinkedAccountsForm linkedAccountsForm;

    public LinkedAccountsFormTest() {
        super(LinkedAccountsApplication.class);
    }

    @Before
    public void initFormWithLinkedFormsComponent() throws IOException {
        initLinkedForms("company-new-1");
    }

    @Test
    public void testOptionalLinkedAccounts() throws IOException {
        initLinkedForms("company_linkedaccounts_optional");
        assertTrue(linkedAccountsForm.isEmpty());
    }

    @Test
    public void testNoLinkedAccountsPassed() throws IOException {
        initLinkedForms("company_linkedaccounts_notpassed");
        assertTrue(linkedAccountsForm.isEmpty());
    }

    @Test
    public void testPrimaryLinkedAccount() throws IOException {
        assertLinkedAccount(linkedAccountsForm.getPrimaryLinkedAccount(), "013002", "66544561", "Billy Bastard", "Slush Funds", "94322122.00");
        assertFalse(linkedAccountsForm.isEmpty());
    }

    @Test
    public void testOtherLinkedAccount() throws IOException {
        final List<ILinkedAccountForm> others = linkedAccountsForm.getOtherLinkedAccounts();
        assertThat(others, hasSize(3));
        assertLinkedAccount(others.get(0), "013005", "211234569", "Belinda Bastard", "Slosh Funds");
        assertLinkedAccount(others.get(1), "013006", "3324415", "Benny Bastard", "Swish Funds", "1236654.01");
        assertLinkedAccount(others.get(2), "013009", "66541123", "Barbera Bastard", "Splash Funds");
    }

    private void initLinkedForms(String resourceName) throws IOException {
        final LinkedAccountsApplication accounts = readJsonResource(resourceName, "linkedaccounts");
        linkedAccountsForm = new LinkedAccountsForm(accounts);
    }

    public static void assertLinkedAccount(ILinkedAccountForm account, String bsb, String accountNumber, String accountName, String nickName) {
        assertThat(account.getBsb(), is(bsb));
        assertThat(account.getAccountNumber(), is(accountNumber));
        assertThat(account.getAccountName(), is(accountName));
        assertThat(account.getNickName(), is(nickName));
    }

    public static void assertLinkedAccount(ILinkedAccountForm account, String bsb, String accountNumber, String accountName, String nickName, String deposit) {
        assertLinkedAccount(account, bsb, accountNumber, accountName, nickName);
        assertThat(account.getDirectDebitAmount(), is(deposit));
    }
}
