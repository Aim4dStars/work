package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;
import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.client.model.IndividualDto;
import com.bt.nextgen.api.util.ApiConstants;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.List;
import java.util.regex.Pattern;

import static com.btfin.panorama.core.security.encryption.EncodedString.toPlainText;

public class ClientSearchFilter extends LambdaMatcher<ClientDto> {

    private final List<ApiSearchCriteria> criteriaList;

    public ClientSearchFilter(List<ApiSearchCriteria> searchCriteria) {
        this.criteriaList = searchCriteria;
    }

    @Override
    protected boolean matchesSafely(final ClientDto client) {
        List<Boolean> matches = Lambda.convert(criteriaList, convertCriteriaToMatch(client));

        return client.isIdVerified() && Iterables.all(matches, new Predicate<Boolean>() {
            @Override
            public boolean apply(@Nonnull Boolean matched) {
                return matched;
            }
        });
    }

    private Converter<ApiSearchCriteria, Boolean> convertCriteriaToMatch(final ClientDto client) {
        return new Converter<ApiSearchCriteria, Boolean>() {
            @Override
            public Boolean convert(ApiSearchCriteria criteria) {
                switch (criteria.getProperty()) {
                    case ApiConstants.ADVISERID:
                        return anyAdviserWithId(client, criteria.getValue());

                    case ApiConstants.DISPLAY_NAME:
                        return displayNameMatches(client, criteria.getValue());

                    case Attribute.INVESTOR_TYPE:
                        return isInvestorType(client, criteria.getValue());

                    default:
                        return false;
                }
            }
        };
    }

    private boolean isInvestorType(ClientDto client, String type) {
        if(client instanceof IndividualDto){
            if (SearchUtil.matches(SearchUtil.getPattern(type), ((IndividualDto) client).getInvestorType())) {
                return true;
            }
        } else {
            return false;
        }
        return true;
    }

    private boolean displayNameMatches(ClientDto client, String name) {
        return SearchUtil.matches(SearchUtil.getPattern(name), client.getDisplayName());
    }

    private boolean anyAdviserWithId(ClientDto client, String adviserId) {
        final Pattern adviserPattern = SearchUtil.getPattern(toPlainText(adviserId));
        return
                Iterables.any(client.getAccounts(), new Predicate<AccountDto>() {
                    @Override
                    public boolean apply(@Nullable AccountDto accountDto) {
                        if (accountDto != null) {
                            return SearchUtil.matches(adviserPattern, toPlainText(accountDto.getAdviserId()));
                        }
                        return false;
                    }
                });
    }
}