package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.SmsfDto;

import java.util.List;

public class CorporateSmsfApplicationDetailsDto extends ClientApplicationDetailsDto {
    private SmsfDto smsf;
    private List<InvestorDto> directors;
    private List<InvestorDto> shareholdersAndMembers;
    
    public CorporateSmsfApplicationDetailsDto withDirectors(List<InvestorDto> directors) {
        this.directors = directors;
        return this;
    }

    public CorporateSmsfApplicationDetailsDto withShareholdersAndMembers(List<InvestorDto> shareholdersAndMembers) {
        this.shareholdersAndMembers = shareholdersAndMembers;
        return this;
    }
    
    public CorporateSmsfApplicationDetailsDto withSmsf(SmsfDto smsf) {
        this.smsf = smsf;
        return this;
    }

    public SmsfDto getSmsf() {
        return smsf;
    }

    public List<InvestorDto> getDirectors() {
        return directors;
    }

    public List<InvestorDto> getShareholdersAndMembers() {
        return shareholdersAndMembers;
    }
}
