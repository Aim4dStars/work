package com.bt.nextgen.api.client.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.avaloq.domain.EmailImpl;
import com.bt.nextgen.service.integration.domain.AddressKey;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class EmailFilterUtilTest {

    private EmailFilterUtil emailFilterUtil;
    private List<Email> emails;

    @Before
    public void init(){
        emails = new ArrayList<>();

        // Primary & Preferred, Secondary Emails
        emails.add(buildEmail("primary@goo.le","2","4568",AddressMedium.EMAIL_PRIMARY,true));
        emails.add(buildEmail("primary@goo.le","3","4569",AddressMedium.EMAIL_ADDRESS_SECONDARY,false));
        emails.add(buildEmail("second@goo.le","3","4570",AddressMedium.EMAIL_ADDRESS_SECONDARY,false));
        emails.add(buildEmail("third@googl.ee","3","4570",AddressMedium.EMAIL_ADDRESS_SECONDARY,false));

        emailFilterUtil = new EmailFilterUtil();
    }

    @Test
    public void shouldReturnPrimaryEmail() throws Exception {

        Email primaryEmail = emailFilterUtil.getPrimaryEmail(emails);

        assertNotNull(primaryEmail);
        assertThat(primaryEmail.getEmail(), is("primary@goo.le"));
        assertThat(primaryEmail.getType(), is(AddressMedium.EMAIL_PRIMARY));
    }

    @Test
    public void shouldReturnPrimaryDuplicateFilteredEmails() throws Exception {

        List<Email> duplicateFilteredEmails = emailFilterUtil.filterDuplicates(emails);
        //Only duplicate secondary email should be removed. If the primary email happens to
        //be same as work number or personal number, they should not be removed.
        assertThat(duplicateFilteredEmails.size(), is(3));
        Email secondaryDuplicateEmail = Lambda.selectFirst(duplicateFilteredEmails, new LambdaMatcher<Email>() {
            @Override
            protected boolean matchesSafely(Email email) {
                return email.getType() == AddressMedium.EMAIL_ADDRESS_SECONDARY && email.getEmail().equals("primary@goo.le");
            }
        });
        //Duplicate secondary email should have been removed.
        assertNull(secondaryDuplicateEmail);
    }

    @Test
    public void shouldReturnPrimaryDuplicateAndSecondaryPreferredDuplicateFilteredEmails() throws Exception {
        emails = new ArrayList<>();
        // Primary, Secondary Preferred and other Secondary Emails
        emails.add(buildEmail("primary@goo.le","2","4568",AddressMedium.EMAIL_PRIMARY,false));
        //primary duplicate
        emails.add(buildEmail("primary@goo.le","2","4568",AddressMedium.EMAIL_ADDRESS_SECONDARY,false));
        emails.add(buildEmail("secPreferred@goo.le","3","4569",AddressMedium.EMAIL_ADDRESS_SECONDARY,true));
        //secondary preferred duplicate
        emails.add(buildEmail("secPreferred@goo.le","3","4569",AddressMedium.EMAIL_ADDRESS_SECONDARY,false));
        emails.add(buildEmail("second@goo.le","3","4570",AddressMedium.EMAIL_ADDRESS_SECONDARY,false));
        emails.add(buildEmail("third@googl.ee","3","4570",AddressMedium.EMAIL_ADDRESS_SECONDARY,false));
        
        List<Email> duplicateFilteredEmails = emailFilterUtil.filterDuplicates(emails);
        //Only duplicate secondary email should be removed. If the primary email happens to
        //be same as work number or personal number, they should not be removed.
        assertThat(duplicateFilteredEmails.size(), is(4));
        Email primaryDuplicate = Lambda.selectFirst(duplicateFilteredEmails, new LambdaMatcher<Email>() {
            @Override
            protected boolean matchesSafely(Email email) {
                return email.getType() == AddressMedium.EMAIL_ADDRESS_SECONDARY && email.getEmail().equals("primary@goo.le");
            }
        });
        //Duplicate secondary email should have been removed.
        assertNull(primaryDuplicate);

        List<Email> secondaryPreferredEmails = Lambda.select(duplicateFilteredEmails, new LambdaMatcher<Email>() {
            @Override
            protected boolean matchesSafely(Email email) {
                return email.getType() == AddressMedium.EMAIL_ADDRESS_SECONDARY && email.getEmail().equals("secPreferred@goo.le");
            }
        });
        //only the non preferred duplicate of secondary preferred email will be removed

        assertNotNull(secondaryPreferredEmails);
        assertThat(secondaryPreferredEmails.size(), is(1));
        assertThat(secondaryPreferredEmails.get(0).isPreferred(), is(true));
    }

    private Email buildEmail(String emailAddress, String modificationSeq, String addressKey, AddressMedium addressMedium, boolean isPreffered){
        EmailImpl email = new EmailImpl();
        email.setEmail(emailAddress);
        email.setModificationSeq(modificationSeq);
        email.setEmailKey(AddressKey.valueOf(addressKey));
        email.setType(addressMedium);
        email.setPreferred(isPreffered);
        return email;
    }
}