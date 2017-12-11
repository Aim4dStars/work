package com.bt.nextgen.api.transactionhistory.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bt.nextgen.core.web.ApiFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.transactionhistory.model.SmsfMembersDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.account.WrapAccountDetailImpl;
import com.bt.nextgen.service.avaloq.client.ClientImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.userinformation.Client;
import com.bt.nextgen.web.controller.cash.util.Attribute;

@Service
public class RetrieveSmsfMembersDtoServiceImpl implements RetrieveSmsfMembersDtoService
{
    @Autowired
    @Qualifier("cacheAvaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Override
    public List <SmsfMembersDto> search(List <ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors)
    {
        EncodedString accountId = null;
        List <SmsfMembersDto> membersList = new ArrayList <SmsfMembersDto>();
        if (!criteriaList.isEmpty())
        {
            for (ApiSearchCriteria parameter : criteriaList)
            {
                if (Attribute.ACCOUNT_ID.equals(parameter.getProperty()))
                {
                    accountId = new EncodedString(parameter.getValue());
                }
                else
                {
                    throw new IllegalArgumentException("Unsupported search");
                }
            }
            if (accountId == null)
            {
                throw new IllegalArgumentException("Unsupported search");
            }

            WrapAccountDetail accountDetail = accountService.loadWrapAccountDetail(AccountKey.valueOf(accountId.plainText()),
                    serviceErrors);
            membersList = toMembersList(accountDetail);

        }

        return membersList;

    }

    //Get Smsf Members Details from account detail
    List <SmsfMembersDto> toMembersList(WrapAccountDetail accountDetail)
    {
        List <SmsfMembersDto> membersList = new ArrayList <SmsfMembersDto>();
        if (((WrapAccountDetailImpl)accountDetail).getAllAssociatedPersons() != null)
        {
            for (Client client : ((WrapAccountDetailImpl)accountDetail).getAllAssociatedPersons())
            {
                if (((ClientImpl)client).isMember())
                {
                    SmsfMembersDto dto = new SmsfMembersDto();
                    dto.setFirstName(client.getFirstName());
                    dto.setLastName(client.getLastName());
                    dto.setPersonId(client.getClientKey().getId());
                    dto.setDateOfBirth(ApiFormatter.asShortDate(((IndividualDetailImpl) client).getDateOfBirth()));
                    membersList.add(dto);
                }
            }
        }
        Collections.sort(membersList, new Comparator <SmsfMembersDto>()

        {
            public int compare(SmsfMembersDto smsfMember1, SmsfMembersDto smsfMember2)
            {
                StringBuilder memberFullName1 = new StringBuilder();
                StringBuilder memberFullName2 = new StringBuilder();
                memberFullName1.append(smsfMember1.getFirstName()).append(smsfMember1.getLastName());
                memberFullName2.append(smsfMember2.getFirstName()).append(smsfMember2.getLastName());
                return memberFullName1.toString().toUpperCase().compareTo(memberFullName2.toString().toUpperCase());

            }
        });

        return membersList;
    }
}
