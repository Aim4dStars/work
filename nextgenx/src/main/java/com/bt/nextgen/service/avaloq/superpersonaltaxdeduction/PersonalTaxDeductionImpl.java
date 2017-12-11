package com.bt.nextgen.service.avaloq.superpersonaltaxdeduction;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by L067218 on 12/10/2016.
 */
@ServiceBean(xpath = "//bp_list/bp")
public class PersonalTaxDeductionImpl implements PersonalTaxDeduction {

    /**
     * List of Notices.
     */
    @ServiceElement(xpath = "buc_list/buc/buc_head_list/buc_head", type = PersonalTaxDeductionNoticesImpl.class)
    private List<PersonalTaxDeductionNotices> taxDeductionNotices = new ArrayList<>();

    @Override
    public List<PersonalTaxDeductionNotices> getTaxDeductionNotices() {
        return taxDeductionNotices;
    }
}
