package com.bt.nextgen.api.client.util;

import ch.lambdaj.Lambda;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Filter duplicate phones in secondary phone list, if it is also primary phone.
 */
@Service
public class PhoneFilterUtil {

    public Phone getPrimaryMobile(List<Phone> phones) {
        return Lambda.selectFirst(phones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.getType() == AddressMedium.MOBILE_PHONE_PRIMARY;
            }
        });
    }

    private Phone getNonPrimaryPreferredPhone(List<Phone> phones) {
        return Lambda.selectFirst(phones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phoneNumber) {
                return phoneNumber.isPreferred() && phoneNumber.getType() != AddressMedium.MOBILE_PHONE_PRIMARY;
            }
        });
    }

    public List<Phone> filterDuplicates(List<Phone> phones) {
        Phone primaryPhone = getPrimaryMobile(phones);
        //Filter the duplicate of the primary in the secondary phones
        List<Phone> primaryDuplicateFilteredPhones = filterPrimaryDuplicate(phones, primaryPhone);
        Phone secondaryAndPreferredPhone = getNonPrimaryPreferredPhone(phones);
        //If the secondary phone has been chosen as preferred, then there could be
        //a duplicate of it existing in the secondary phones. Filter it.
        if (secondaryAndPreferredPhone != null){
            return filterPreferredDuplicate(primaryDuplicateFilteredPhones,
                    secondaryAndPreferredPhone, secondaryAndPreferredPhone.getType());
        }
        return primaryDuplicateFilteredPhones;
    }

    private List<Phone> filterPrimaryDuplicate(List<Phone> phones, final Phone primaryPhone) {
        return filterPreferredDuplicate(phones, primaryPhone, AddressMedium.MOBILE_PHONE_SECONDARY);
    }

    private List<Phone> filterPreferredDuplicate(List<Phone> phones, final Phone duplicatePhone, final AddressMedium phoneType) {
        return Lambda.select(phones, new LambdaMatcher<Phone>() {
            @Override
            protected boolean matchesSafely(Phone phone) {
                return !(phone.getType() == phoneType &&
                        (!phone.isPreferred() && phone.getNumber().equals(duplicatePhone.getNumber())));
            }
        });
    }

}
