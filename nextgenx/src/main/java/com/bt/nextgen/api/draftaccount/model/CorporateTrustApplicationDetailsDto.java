package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.InvestorDto;

import java.util.List;

public class CorporateTrustApplicationDetailsDto extends TrustApplicationDetailsDto {

    private List<InvestorDto> directors;

    public List<InvestorDto> getDirectors() {
        return directors;
    }

    public CorporateTrustApplicationDetailsDto withDirectors(List<InvestorDto> directors){
        this.directors = directors;
        return this;
        
    }
}
