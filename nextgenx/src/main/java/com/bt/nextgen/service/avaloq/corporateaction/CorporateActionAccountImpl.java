package com.bt.nextgen.service.avaloq.corporateaction;

import java.math.BigDecimal;
import java.util.List;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.integration.xml.annotation.ServiceElementList;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.avaloq.payeedetails.ContainerTypeConverter;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccount;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionAccountParticipationStatus;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOption;

/**
 * See interface for documentation
 */
@ServiceBean(xpath = "/")
public class CorporateActionAccountImpl implements CorporateActionAccount {

    public static final String XML_HEADER = "trig_pos_head_list/trig_pos_head/";

    @ServiceElement(xpath = XML_HEADER + "trig_pos_id/val")
    private String positionId;

    @ServiceElement(xpath = XML_HEADER + "bp_id/val")
    private String accountId;

    @ServiceElement(xpath = XML_HEADER + "avsr_person_oe_id/val", converter = BrokerKeyConverter.class)
    private BrokerKey adviserId;

    @ServiceElement(xpath = XML_HEADER + "prod_id/val")
    private String productId;

    @ServiceElement(xpath = XML_HEADER + "ips_id/val")
    private String ipsId;

    @ServiceElement(xpath = XML_HEADER + "cont_type_id/val", converter = ContainerTypeConverter.class)
    private ContainerType containerType;

    @ServiceElement(xpath = XML_HEADER + "trig_qty/val")
    private BigDecimal eligibleQuantity;

    @ServiceElement(xpath = XML_HEADER + "avl_qty/val")
    private BigDecimal availableQuantity;

    @ServiceElement(xpath = XML_HEADER + "decsn_conf_id/val", converter = CorporateActionAccountParticipationStatusConverter.class)
    private CorporateActionAccountParticipationStatus electionStatus;

    @ServiceElementList(xpath = XML_HEADER + "decsn_list_list/decsn_list", type = CorporateActionDecisionImpl.class)
    private List<CorporateActionOption> decisions;

    @ServiceElement(xpath = XML_HEADER + "avl_cash/val")
    private BigDecimal availableCash;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BrokerKey getAdviserId() {
        return adviserId;
    }

    public void setAdviserId(BrokerKey adviserId) {
        this.adviserId = adviserId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getIpsId() {
        return ipsId;
    }

    public void setIpsId(String ipsId) {
        this.ipsId = ipsId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContainerType getContainerType() {
        return containerType;
    }

    public void setContainerType(ContainerType containerType) {
        this.containerType = containerType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getEligibleQuantity() {
        return eligibleQuantity;
    }

    public void setEligibleQuantity(BigDecimal eligibleQuantity) {
        this.eligibleQuantity = eligibleQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(BigDecimal availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CorporateActionAccountParticipationStatus getElectionStatus() {
        return electionStatus;
    }

    public void setElectionStatus(CorporateActionAccountParticipationStatus electionStatus) {
        this.electionStatus = electionStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CorporateActionOption> getDecisions() {
        return decisions;
    }

    public void setDecisions(List<CorporateActionOption> decisions) {
        this.decisions = decisions;
    }

    public BigDecimal getAvailableCash() {
        return availableCash;
    }

    public void setAvailableCash(BigDecimal availableCash) {
        this.availableCash = availableCash;
    }
}
