package com.bt.nextgen.api.client.v2.service;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.client.model.ClientIdentificationDto;
import com.bt.nextgen.api.client.v2.model.ClientDto;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

@Service("ClientSearchDtoServiceV2")
public class ClientSearchDtoServiceImpl implements ClientSearchDtoService {
    @Autowired
    private ClientListDtoService clientListDtoService;

    @Override
    public List<ClientIdentificationDto> getFilteredValue(String queryString, List<ApiSearchCriteria> filterCriteria,
                                                          ServiceErrors serviceErrors) {
        List<ClientIdentificationDto> clientList;
        final List<ClientIdentificationDto> filteredList = new ArrayList<>();

        if (SearchUtil.isSearchKeyValid(queryString)) {
            try {
                clientList = clientListDtoService.getFilteredValue(queryString, filterCriteria, serviceErrors);
                if (CollectionUtils.isNotEmpty(filterCriteria)) {
                    filterList(clientList, filterCriteria);
                }
                final Pattern pattern = SearchUtil.getPattern(queryString);
                for (ClientIdentificationDto clientId : clientList) {
                    final ClientDto client = (ClientDto) clientId;
                    if (SearchUtil.matches(pattern, client.getDisplayName())) {
                        filteredList.add(client);
                    } else {
                        for (AccountDto account : client.getAccounts()) {
                            if (SearchUtil.matches(pattern, account.getAccountName(), account.getAccountNumber())) {
                                filteredList.add(client);
                                break;
                            }
                        }
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Unable to process filter criteria list", e);
            }
        }
        return filteredList;
    }

    private void filterList(List<ClientIdentificationDto> clientList,
                            List<ApiSearchCriteria> filterCriteria) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        List<AccountDto> accountList;
        for (Iterator<ClientIdentificationDto> iterator = clientList.iterator(); iterator.hasNext(); ) {
            final ClientDto client = (ClientDto) iterator.next();
            accountList = new ArrayList<>();
            for (AccountDto account : client.getAccounts()) {
                boolean allMatched = true;
                for (ApiSearchCriteria criteria : filterCriteria) {
                    String value = BeanUtils.getProperty(account, criteria.getProperty());
                    if (!Matchers.equalTo(value).matches(criteria.getValue())) {
                        allMatched = false;
                    }
                }

                if (allMatched) {
                    accountList.add(account);
                }
            }
            client.setAccounts(accountList);
            if (CollectionUtils.isEmpty(client.getAccounts())) {
                iterator.remove();
            }
        }
    }

    @Override
    public List<ClientIdentificationDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        return clientListDtoService.getFilteredValue(null, criteriaList, serviceErrors);
    }
}
