package com.bt.nextgen.api.draftaccount.model.form.v1;

import com.bt.nextgen.api.draftaccount.model.form.IShareholderAndMembersForm;
import com.bt.nextgen.api.draftaccount.schemas.v1.base.AnswerTypeEnum;
import com.bt.nextgen.api.draftaccount.schemas.v1.company.AdditionalShareholderAndMember;
import com.bt.nextgen.api.draftaccount.schemas.v1.company.InvestorsWithRole;
import com.bt.nextgen.api.draftaccount.schemas.v1.company.ShareholderAndMembers;

import java.util.List;

/**
 * Created by m040398 on 24/03/2016.
 */
class ShareholderAndMembersForm implements IShareholderAndMembersForm {

    private final ShareholderAndMembers members;

    public ShareholderAndMembersForm(ShareholderAndMembers members) {
        this.members = members;
    }

    @Override
    public boolean hasbeneficiaryClasses() {
        return members != null ? AnswerTypeEnum.YES.equals(members.getHasbeneficiaryclasses()) : false;
    }

    @Override
    public String getBeneficiaryClassDetails() {
        return members != null ? members.getBeneficiaryclassdetails() : null;
    }

    @Override
    public String getMajorShareholder() {
        return (members != null && AnswerTypeEnum.YES.equals(members.getIsMajorShareholder()))
                ? members.getIsMajorShareholder().toString() : null;
    }

    @Override
    public String getCompanySecretaryValue() {
        return members != null && members.getCompanysecretary() != null ? members.getCompanysecretary() : null;
    }


    /**
     * Package protected. Not part of public interface
     *
     * @return
     */
    List<InvestorsWithRole> getInvestorsWithRoles() {
        return members != null ? members.getInvestorsWithRoles() : null;
    }


    /**
     * Package protected. Not part of public interface.
     *
     * @return
     */
    List<AdditionalShareholderAndMember> getAdditionalShareholdersAndMembers() {
        return members.getAdditionalShareHoldersAndMembers();
    }

}
