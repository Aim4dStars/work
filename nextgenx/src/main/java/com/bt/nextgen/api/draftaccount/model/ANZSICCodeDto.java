package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.core.api.model.BaseDto;
import com.bt.nextgen.core.api.model.KeyedDto;

public class ANZSICCodeDto extends BaseDto implements KeyedDto<String> {

    /** User ID of this code, as stored in Avaloq. */
    private String key;

    /** Proper, standard ANZSIC code for the industry. */
    private String code;

    /** Westpac-specific code applied to the industry, meaningful only in the UCM system. */
    private String ucmCode;

    private String industryDivision;
    private String industrySubdivision;
    private String industryGroup;
    private String industryClass;

    public ANZSICCodeDto(String key, String code, String ucmCode, String industryDivision, String industrySubdivision, String industryGroup, String industryClass) {
        this.key = key;
        this.code = code;
        this.ucmCode = ucmCode;
        this.industryDivision = industryDivision;
        this.industrySubdivision = industrySubdivision;
        this.industryGroup = industryGroup;
        this.industryClass = industryClass;
    }

    public ANZSICCodeDto() {
        this(null, null, null, null, null, null, null);
    }

    @Override
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUcmCode() {
        return ucmCode;
    }

    public void setUcmCode(String ucmCode) {
        this.ucmCode = ucmCode;
    }

    public String getIndustryDivision() {
        return industryDivision;
    }

    public void setIndustryDivision(String industryDivision) {
        this.industryDivision = industryDivision;
    }

    public String getIndustrySubdivision() {
        return industrySubdivision;
    }

    public void setIndustrySubdivision(String industrySubdivision) {
        this.industrySubdivision = industrySubdivision;
    }

    public String getIndustryGroup() {
        return industryGroup;
    }

    public void setIndustryGroup(String industryGroup) {
        this.industryGroup = industryGroup;
    }

    public String getIndustryClass() {
        return industryClass;
    }

    public void setIndustryClass(String industryClass) {
        this.industryClass = industryClass;
    }
}
