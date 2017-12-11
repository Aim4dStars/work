package com.bt.nextgen.userdetails.web;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.model.AddressModel;
import com.bt.nextgen.core.web.model.Intermediary;
import com.bt.nextgen.core.web.model.Investor;
import com.bt.nextgen.core.web.model.Person;
import com.bt.nextgen.core.web.model.PersonInterface;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.externalasset.builder.DateTimeConverter;
import com.bt.nextgen.service.integration.search.PersonResponse;
import com.bt.nextgen.service.integration.search.ProfileUserRole;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("squid:S128")
public class UserDetailsConverter {

    private static Logger logger = LoggerFactory.getLogger(UserDetailsConverter.class);

    public static List<PersonInterface> toPersonInterfaceList(List<PersonResponse> personList) {
        List<PersonInterface> toPersonList = new ArrayList();

        for (PersonResponse personResponse : personList) {
            Person person = toPersonForSearchPersonResponse(personResponse, new ServiceErrorsImpl());
            toPersonList.add(person);
        }

        return toPersonList;
    }

    private static boolean checkRole(String userRole) {
        boolean knownRole = false;
        switch (userRole) {
            case "ACCOUNTANT":
            case "ACCOUNTANT_SUPPORT_STAFF":
            case "ADVISER":
            case "PARAPLANNER":
            case "ASSISTANT":
            case "DEALER_GROUP_MANAGER":
            case "PRACTICE_MANAGER":
            case "INVESTMENT_MANAGER":
            case "FUND_MANAGER":
            case "PORTFOLIO_MANAGER":
                knownRole = true;
                break;
            default:
                knownRole = false;
        }
        return knownRole;
    }

    private static Person toPersonForSearchPersonResponse(PersonResponse personResponse, ServiceErrors serviceErrors) {
        Person personModel = null;
        if (CollectionUtils.isNotEmpty(personResponse.getProfileUserRoles())) {
            String userRole = personResponse.getProfileUserRoles().iterator().next().getUserRole().name();
            personModel = getPerson(personResponse, serviceErrors, userRole);

            populatePersonModel(personResponse, personModel);
        }
        return personModel;
    }

    private static Person getPerson(PersonResponse personResponse, ServiceErrors serviceErrors, String userRole) {
        Person personModel = null;
        boolean knownRole = false;

        if ("INVESTOR".equalsIgnoreCase(userRole)) {
            personModel = new Investor();
        } else {
            if (checkRole(userRole)) {
                knownRole = true;
            }
            Intermediary intermediary = new Intermediary();
            // intermediary.setDealerGroupName(personResponse.getDealerGroupName());
            // intermediary.setCompanyName(personResponse.getCompanyName());
            setProfileAttributes(personResponse, intermediary);
            personModel = intermediary;

            if (knownRole == false) {
                logger.warn("Error while processing role of {}", userRole);
                serviceErrors.addError(new ServiceErrorImpl("Error while processing role of {}", userRole));
            }
        }

        return personModel;
    }

    private static StringBuilder setAttributes(StringBuilder target, String value) {
        if (target == null) {
            StringBuilder newTarget = new StringBuilder();
            newTarget.append(value);
            return newTarget;
        } else {
            target.append(", " + value);
        }
        return target;
    }

    /**
     * checkTerminated - method checks closedate time with current date time to determine if the profile is active or not returns
     * close date if terminated and null if its still active
     * 
     * @param profile
     * @return
     */
    private static DateTime checkTerminated(ProfileUserRole profile) {
        boolean isTerminated = false;
        DateTimeConverter dateTimeConverter = new DateTimeConverter();
        DateTime currDateTime = new DateTime();
        DateTime closeDateTime = null;
        if (profile.getCloseDate() != null) {
            closeDateTime = dateTimeConverter.convert(profile.getCloseDate());
            isTerminated = (closeDateTime.compareTo(currDateTime)) > 0 ? false : true; // check if future date
        }
        return isTerminated ? closeDateTime : null;
    }

    private static void setProfileAttributes(PersonResponse response, Intermediary intermediary) {
        StringBuilder userRole = null;
        StringBuilder companyName = null;
        StringBuilder dealerGroupName = null;
        String currentRole = null;

        for (ProfileUserRole profile : response.getProfileUserRoles()) {

            if ("ASSISTANT".equals(profile.getUserRole().name())) {
                currentRole = Constants.ROLE_ADMIN_ASSISTANT;
            } else
                currentRole = WordUtils.capitalize(profile.getUserRole().name().toLowerCase().replace('_', ' '));

            if (StringUtils.isNotBlank(currentRole)) {
                // Terminated accounts identification
                DateTime closeDateTime = checkTerminated(profile);
                if (closeDateTime != null) {
                    currentRole = currentRole + " - Terminated (" + ApiFormatter.asShortDate(closeDateTime) + ")";
                }
                userRole = setAttributes(userRole, currentRole);
            }
            if (StringUtils.isNotBlank(profile.getCompanyName())) {
                companyName = setAttributes(companyName, profile.getCompanyName());
            }
            if (StringUtils.isNotBlank(profile.getDealerGroupName())) {
                dealerGroupName = setAttributes(dealerGroupName, profile.getDealerGroupName());
            }
        }

        if (userRole != null) {
            intermediary.setRole(userRole.toString());
        }
        if (companyName != null) {
            intermediary.setCompanyName(companyName.toString());
        }
        if (dealerGroupName != null) {
            intermediary.setDealerGroupName(dealerGroupName.toString());
        }

    }

    private static void populatePersonModel(PersonResponse personResponse, Person personModel) {
        if (!StringUtils.isEmpty(personResponse.getClientKey().getId())) {
            personModel.setClientId(EncodedString.fromPlainText(personResponse.getClientKey().getId()));
        }

        if (!StringUtils.isEmpty(personResponse.getLastName())) {
            personModel.setLastName(personResponse.getLastName());
        }

        if (!StringUtils.isEmpty(personResponse.getFirstName())) {
            personModel.setFirstName(personResponse.getFirstName());
        }

        if (!StringUtils.isEmpty(personResponse.getMiddleName())) {
            personModel.setMiddleName(personResponse.getMiddleName());
        }

        if (!StringUtils.isEmpty(personResponse.getFirstName())) {
            personModel.setFullName(personResponse.getFullName());
        }

        if (!StringUtils.isEmpty(personResponse.getPrimaryEmail())) {
            personModel.setPrimaryEmailId(personResponse.getPrimaryEmail());
        }

        if (!StringUtils.isEmpty(personResponse.getGcmId())) {
            personModel.setGcmId(personResponse.getGcmId());
        }

        if (!StringUtils.isEmpty(personResponse.getPrimaryMobile())) {
            personModel.setPrimaryMobileNumber(personResponse.getPrimaryMobile());
        }

        AddressModel primaryDomiAddress = new AddressModel();

        if (!StringUtils.isEmpty(personResponse.getDomiSuburb())) {
            primaryDomiAddress.setAddressLine2(personResponse.getDomiSuburb());
        }

        if (!StringUtils.isEmpty(personResponse.getDomiState())) {
            primaryDomiAddress.setState(personResponse.getDomiState());
        }

        personModel.setPrimaryDomiAddress(primaryDomiAddress);

        if (!StringUtils.isEmpty(personResponse.getOpenDate())) {
            personModel.setOpenDate(personResponse.getOpenDate());
        }
        personModel.setBeneficiary(personResponse.isBenef());
        personModel.setMember(personResponse.isMember());
    }

}
