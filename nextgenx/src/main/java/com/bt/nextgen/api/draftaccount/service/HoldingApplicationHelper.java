package com.bt.nextgen.api.draftaccount.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.draftaccount.model.HoldingApplicationClientDto;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.integration.account.AccountStructureType;
import com.bt.nextgen.service.integration.accountactivation.ApplicationDocument;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Email;
import com.bt.nextgen.service.integration.domain.Phone;
import com.btfin.panorama.core.security.avaloq.accountactivation.AssociatedPerson;
import com.btfin.panorama.core.security.avaloq.userinformation.PersonRelationship;
import org.hamcrest.Matchers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Helper class to create HoldingApplicationDto
 */
public final class HoldingApplicationHelper {

    private HoldingApplicationHelper() {
    }

    /**
     * Get first email which matches the given type
     *
     * @param emailList
     * @param type
     * @return
     */
    public static String getEmailByType(List<Email> emailList, AddressMedium type) {
        Email email = Lambda.selectFirst(emailList, Lambda.having(Lambda.on(Email.class).getType(),
                Matchers.is(type)));
        return email != null ? email.getEmail() : null;
    }

    /**
     * Get first phone which matches the given type
     *
     * @param phoneList
     * @param type
     * @return
     */
    public static String getPhoneByType(List<Phone> phoneList, AddressMedium type) {
        Phone phone = Lambda.selectFirst(phoneList, Lambda.having(Lambda.on(Phone.class).getType(),
                Matchers.is(type)));
        return phone != null ? phone.getNumber() : null;
    }

    /**
     * @param applicationDocument
     * @return
     */
    public static String checkAccountTypeForNewSmsf(ApplicationDocument applicationDocument) {

        if(OrderType.NewIndividualSMSF.getOrderType().equals(applicationDocument.getOrderType())) {
            return "newIndividualSMSF";
        }
        if(OrderType.NewCorporateSMSF.getOrderType().equals(applicationDocument.getOrderType())) {
            return "newCorporateSMSF";
        }
        return null;
    }

    /**
     * Get the list of associated persons on the application
     *
     * @param accountStructureType
     * @param applicationDocument
     * @return
     */
    public static List<AssociatedPerson> getAssociatedPersons(
        final AccountStructureType accountStructureType, ApplicationDocument applicationDocument) {
        return Lambda.filter(
            new LambdaMatcher<AssociatedPerson>() {
                @Override
                protected boolean matchesSafely(AssociatedPerson item) {
                    List<PersonRelationship> personRelationshipList;
                    switch (accountStructureType) {
                        case Individual:
                        case Joint:
                            personRelationshipList = Arrays.asList(PersonRelationship.AO);
                            break;
                        case Company:
                        case SMSF:
                        case Trust:
                            personRelationshipList = Arrays.asList(PersonRelationship.DIRECTOR, PersonRelationship.TRUSTEE,PersonRelationship.SECRETARY, PersonRelationship.SIGNATORY);
                            break;
                        default:
                            throw new IllegalArgumentException("Account type " + accountStructureType + " not supported");
                    }
                    return personRelationshipList.contains(item.getPersonRel());
                }
            }, applicationDocument.getPersonDetails());
    }

    /**
     * Sort the list of clients on the application by full name
     *
     * @param holdingApplicationClientList
     */
    public static void sortHoldingApplicationClientList(List<HoldingApplicationClientDto> holdingApplicationClientList) {
        Collections.sort(holdingApplicationClientList, new Comparator<HoldingApplicationClientDto>() {
            @Override
            public int compare(HoldingApplicationClientDto client1, HoldingApplicationClientDto client2) {
                return client1.getFullName().compareToIgnoreCase(client2.getFullName());
            }
        });
    }
}
