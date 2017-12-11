package com.bt.nextgen.api.client.util;

import com.bt.nextgen.api.account.v1.model.AccountDto;

import com.bt.nextgen.api.client.model.ClientDto;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.btfin.panorama.core.security.avaloq.Constants;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;

import java.util.List;
import java.util.regex.Pattern;

import static ch.lambdaj.Lambda.selectFirst;

/**
 * Created by L062329 on 30/12/2014.
 */
public class SearchFilter extends LambdaMatcher<ClientDto> {

    private final String query;

    private static final Matcher<ApiSearchCriteria> CLIENT_DISPLAY_NAME_CRITERIA = new LambdaMatcher<ApiSearchCriteria>() {
        @Override
        protected boolean matchesSafely(ApiSearchCriteria criteria) {
            return Constants.CLIENT_DISPLAY_NAME.equals(criteria.getProperty());
        }
    };

    public SearchFilter(List<ApiSearchCriteria> criteriaList) {
        final ApiSearchCriteria clientDisplayName = selectFirst(criteriaList, CLIENT_DISPLAY_NAME_CRITERIA);
        this.query = clientDisplayName != null ? clientDisplayName.getValue() : null;
    }

    public SearchFilter(String query) {
        this.query = query;
    }

    @Override
    protected boolean matchesSafely(ClientDto client) {
        final Pattern pattern = SearchUtil.getPattern(query);
        if (pattern != null) {
            if (SearchUtil.matches(pattern, client.getDisplayName())) {
                return true;
            } else {
                for (AccountDto account : client.getAccounts()) {
                    if (SearchUtil.matches(pattern, account.getAccountName(), account.getAccountId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isSearch() {
        if (StringUtils.isBlank(query)) {
            return false;
        }
        return true;
    }
}
