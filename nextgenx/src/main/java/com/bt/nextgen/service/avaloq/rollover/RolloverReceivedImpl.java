package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import com.bt.nextgen.service.integration.rollover.RolloverContributionStatus;
import com.bt.nextgen.service.integration.rollover.RolloverReceived;
import com.bt.nextgen.service.integration.rollover.RolloverType;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

@ServiceBean(xpath = "doc", type = ServiceBeanType.CONCRETE)
public class RolloverReceivedImpl implements RolloverReceived {

    public final static String XML_HEADER = "./doc_head_list/doc_head/";

    @ServiceElement(xpath = XML_HEADER + "doc_id/val")
    private String fundId;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "fund_name/val")
    private String fundName;

    @ServiceElement(xpath = XML_HEADER + "fund_abn/val")
    private String fundAbn;

    @ServiceElement(xpath = XML_HEADER + "fund_usi/val")
    private String usi;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "fund_estim_amt/val")
    private BigDecimal amount;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "rlov_type/val", staticCodeCategory = "ROLLOVER_TYPE")
    private RolloverType rolloverType;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "rcvd_date/val", converter = IsoDateTimeConverter.class)
    private DateTime receivedDate;

    /**
     * Contribution Static code btfg$rlov_contri_status
     */
    @ServiceElement(xpath = XML_HEADER + "status/val", staticCodeCategory = "CONTRIBUTION_STATUS")
    private RolloverContributionStatus contributionStatus;

    public RolloverReceivedImpl() {
        super();
    }

    @Override
    public String getFundId() {
        return fundId;
    }

    @Override
    public String getFundUsi() {
        return this.usi;
    }

    @Override
    public String getFundName() {
        return this.fundName;
    }

    @Override
    public String getFundAbn() {
        return this.fundAbn;
    }

    @Override
    public BigDecimal getAmount() {
        return this.amount;
    }

    @Override
    public RolloverType getRolloverType() {
        return this.rolloverType;
    }

    @Override
    public DateTime getReceivedDate() {
        return this.receivedDate;
    }

    @Override
    public RolloverContributionStatus getContributionStatus() {
        return this.contributionStatus;
    }

    public void setFundId(String fundId) {
        this.fundId = fundId;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public void setFundAbn(String fundAbn) {
        this.fundAbn = fundAbn;
    }

    public void setFundUsi(String usi) {
        this.usi = usi;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setRolloverType(RolloverType rolloverType) {
        this.rolloverType = rolloverType;
    }

    public void setReceivedDate(DateTime receivedDate) {
        this.receivedDate = receivedDate;
    }

    public void setContributionStatus(RolloverContributionStatus contributionStatus) {
        this.contributionStatus = contributionStatus;
    }

}
