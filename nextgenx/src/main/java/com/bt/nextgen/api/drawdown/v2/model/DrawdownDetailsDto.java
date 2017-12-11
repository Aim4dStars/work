package com.bt.nextgen.api.drawdown.v2.model;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

public class DrawdownDetailsDto extends DrawdownDto implements KeyedDto<AccountKey> {

    private List<AssetPriorityDto> priorityDrawdownList;

    public DrawdownDetailsDto() {
        super();
    }

    public DrawdownDetailsDto(AccountKey accountKey, String drawdownType, List<AssetPriorityDto> priorityList) {
        super(accountKey, drawdownType);
        this.priorityDrawdownList = priorityList;
    }

    public List<AssetPriorityDto> getPriorityDrawdownList() {
        return priorityDrawdownList;
    }

    public boolean hasValidationError() {
        boolean hasError = false;
        if (getWarnings() != null) {
            for (DomainApiErrorDto err : this.getWarnings()) {
                hasError = ErrorType.ERROR.toString().equals(err.getErrorType());
                if (hasError) {
                    return hasError;
                }
            }
        }
        return hasError;
    }

    public void setPriorityDrawdownList(List<AssetPriorityDto> priorityDrawdownList) {
        this.priorityDrawdownList = priorityDrawdownList;
    }

}
