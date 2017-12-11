package com.bt.nextgen.api.ips.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

import java.util.List;

/**
 * Corporate action Dto object.
 */
public class InvestmentPolicyStatementDto extends BaseDto implements KeyedDto<InvestmentPolicyStatementKey> {
    private InvestmentPolicyStatementKey key;
    private String id;
    private String name;
    private String code;
    private String apirCode;
    private List<IpsFeeDto> feeList;

    public InvestmentPolicyStatementDto() {

    }

    public InvestmentPolicyStatementDto(String id, String name, String code, String apirCode) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.apirCode = apirCode;
    }

    public InvestmentPolicyStatementDto(InvestmentPolicyStatementKey key, String name, String code, String apirCode, List feeList) {
        this.key = key;
        this.name = name;
        this.code = code;
        this.apirCode = apirCode;
        this.feeList = feeList;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getApirCode() {
        return apirCode;
    }

    public List getFeeList() {
        return feeList;
    }

    @Override
    public InvestmentPolicyStatementKey getKey() {
        return key;
    }
}
