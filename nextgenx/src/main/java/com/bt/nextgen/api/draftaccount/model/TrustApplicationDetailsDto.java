package com.bt.nextgen.api.draftaccount.model;

import com.bt.nextgen.api.client.model.InvestorDto;
import com.bt.nextgen.api.client.model.TrustDto;

import java.util.ArrayList;
import java.util.List;

public class TrustApplicationDetailsDto extends ClientApplicationDetailsDto{
    private TrustDto trust;

    private List <InvestorDto> shareholdersAndMembers = new ArrayList<>();

    public TrustApplicationDetailsDto withTrust(TrustDto trust){
        this.trust = trust;
        return this;
        
    }

    public TrustApplicationDetailsDto withShareHoldersAndMembers(List <InvestorDto> shareholdersAndMembers){
        this.shareholdersAndMembers = shareholdersAndMembers;
        return this;

    }

    public TrustDto getTrust() {
        return trust;
    }

    public List <InvestorDto> getShareholdersAndMembers() {
        return shareholdersAndMembers;
    }
}

