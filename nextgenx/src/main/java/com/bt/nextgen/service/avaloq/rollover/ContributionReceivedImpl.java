package com.bt.nextgen.service.avaloq.rollover;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.integration.externalasset.builder.IsoDateTimeConverter;
import com.bt.nextgen.service.integration.rollover.ContributionReceived;
import com.bt.nextgen.service.integration.rollover.RolloverContributionStatus;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

@ServiceBean(xpath = "doc", type = ServiceBeanType.CONCRETE)
public class ContributionReceivedImpl implements ContributionReceived {

    public final static String XML_HEADER = "./doc_head_list/doc_head/";

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "doc_id/val")
    private String contributionId;

    @ServiceElement(xpath = XML_HEADER + "book_text/val")
    private String description;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "amount/val")
    private BigDecimal amount;

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "pay_date/val", converter = IsoDateTimeConverter.class)
    private DateTime paymentDate;

    /**
     * Contribution Static code btfg$rlov_contri_status
     */
    @ServiceElement(xpath = XML_HEADER + "status/val", staticCodeCategory = "CONTRIBUTION_STATUS")
    private RolloverContributionStatus contributionStatus;

    public ContributionReceivedImpl() {
        super();
    }

    @Override
    public String getContributionId() {
        return contributionId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public DateTime getPaymentDate() {
        return paymentDate;
    }

    @Override
    public RolloverContributionStatus getContributionStatus() {
        return contributionStatus;
    }

    public void setContributionId(String contributionId) {
        this.contributionId = contributionId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setPaymentDate(DateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setContributionStatus(RolloverContributionStatus contributionStatus) {
        this.contributionStatus = contributionStatus;
    }

}
