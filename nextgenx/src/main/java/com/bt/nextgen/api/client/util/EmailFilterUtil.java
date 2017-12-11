package com.bt.nextgen.api.client.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Filter duplicate emails in secondary email list, if it already is also primary.
 */
@Service
public class EmailFilterUtil {


    public Email getPrimaryEmail(List<Email> emails) {
        return Lambda.selectFirst(emails, new LambdaMatcher<Email>() {
            @Override
            protected boolean matchesSafely(Email emailAddress) {
                return emailAddress.getType() == AddressMedium.EMAIL_PRIMARY;
            }
        });
    }

    private Email getSecondaryPreferredEmail(List<Email> emails) {
        return Lambda.selectFirst(emails, new LambdaMatcher<Email>() {
            @Override
            protected boolean matchesSafely(Email emailAddress) {
                return emailAddress.getType() == AddressMedium.EMAIL_ADDRESS_SECONDARY && emailAddress.isPreferred();
            }
        });
    }

    public List<Email> filterDuplicates(List<Email> emails) {
        Email primaryEmail = getPrimaryEmail(emails);
        List<Email> primaryDuplicateFilteredEmails = getDuplicateFilteredEmails(emails, primaryEmail);
        Email secondaryPrefferedEmail = getSecondaryPreferredEmail(emails);
        if (secondaryPrefferedEmail != null) {
            return getDuplicateFilteredEmails(primaryDuplicateFilteredEmails, secondaryPrefferedEmail);
        }
        return primaryDuplicateFilteredEmails;
    }

    private List<Email> getDuplicateFilteredEmails(List<Email> emails, final Email duplicateEmail) {
        return Lambda.select(emails, new LambdaMatcher<Email>() {
            @Override
            protected boolean matchesSafely(Email emailAddress) {
                return !(emailAddress.getType() == AddressMedium.EMAIL_ADDRESS_SECONDARY &&
                        (emailAddress.getEmail().equals(duplicateEmail.getEmail()) &&
                                !emailAddress.isPreferred()));
            }
        });
    }
}
