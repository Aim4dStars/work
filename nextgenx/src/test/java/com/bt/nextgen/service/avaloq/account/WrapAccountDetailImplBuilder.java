package com.bt.nextgen.service.avaloq.account;


import com.btfin.panorama.service.integration.account.LinkedAccount;

import java.util.List;

import static java.util.Arrays.asList;

public class WrapAccountDetailImplBuilder {

    private List<LinkedAccount> linkedAccounts = asList();

    public static WrapAccountDetailImplBuilder aWrapAccountDetailImpl() {
        return new WrapAccountDetailImplBuilder();
    }

    public WrapAccountDetailImpl build() {
        WrapAccountDetailImpl wrapAccountDetail = new WrapAccountDetailImpl();
        wrapAccountDetail.setLinkedAccounts(linkedAccounts);
        return wrapAccountDetail;
    }

    public WrapAccountDetailImplBuilder withLinkedAccounts(List<LinkedAccount> linkedAccounts){
        this.linkedAccounts = linkedAccounts;
        return this;
    }

}
