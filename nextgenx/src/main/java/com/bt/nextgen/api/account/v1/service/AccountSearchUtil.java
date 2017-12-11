package com.bt.nextgen.api.account.v1.service;

import com.bt.nextgen.api.account.v1.model.AccountDto;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.util.LambdaMatcher;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @deprecated Use V2
 */
@Deprecated
public class AccountSearchUtil extends LambdaMatcher<AccountDto> {

    private String query;

    public AccountSearchUtil(String query) {
        this.query = query;
    }

    @Override
    public boolean matchesSafely(AccountDto item) {
        Pattern pattern = SearchUtil.getPattern(query);
        if (SearchUtil.matches(pattern, item.getAccountName(), item.getAccountNumber())) {
            return true;
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
