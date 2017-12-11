package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsSummaryDetails;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;

import javax.validation.constraints.NotNull;

@ServiceBean(xpath = "ips")
public class IpsSummaryDetailsImpl implements IpsSummaryDetails {

    private static final String XML_HEADER = "ips_head_list/ips_head/";

    @NotNull
    @ServiceElement(xpath = XML_HEADER + "ips_id/val", converter = IpsKeyConverter.class)
    private IpsKey modelKey;

    @ServiceElement(xpath = XML_HEADER + "ips_name/val")
    private String modelName;

    @ServiceElement(xpath = XML_HEADER + "ips_sym/val")
    private String modelCode;

    @ServiceElement(xpath = XML_HEADER + "ips_apir/val")
    private String apirCode;

    @ServiceElement(xpath = XML_HEADER + "ips_status_id/val", staticCodeCategory = "IPS_STATUS")
    private ModelPortfolioStatus status;

    @ServiceElement(xpath = XML_HEADER + "last_doc_id/val")
    private String ipsOrderId;

    @ServiceElement(xpath = XML_HEADER + "last_edit_doc_id/val")
    private String modelOrderId;

    @ServiceElement(xpath = XML_HEADER + "invst_mgr_id/annot/ctx/id", converter = BrokerKeyConverter.class)
    private BrokerKey investmentManagerId;

    @ServiceElement(xpath = XML_HEADER + "acc_type_id/val")
    private String accountType;

    @ServiceElement(xpath = XML_HEADER + "cton_type_id/val", staticCodeCategory = "CONSTRUCTION_TYPE")
    private ConstructionType modelConstruction;

    public IpsKey getModelKey() {
        return modelKey;
    }

    public void setModelKey(IpsKey modelKey) {
        this.modelKey = modelKey;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getApirCode() {
        return apirCode;
    }

    public void setApirCode(String apirCode) {
        this.apirCode = apirCode;
    }

    public ModelPortfolioStatus getStatus() {
        return status;
    }

    public void setStatus(ModelPortfolioStatus status) {
        this.status = status;
    }

    public String getIpsOrderId() {
        return ipsOrderId;
    }

    public void setIpsOrderId(String ipsOrderId) {
        this.ipsOrderId = ipsOrderId;
    }

    public String getModelOrderId() {
        return modelOrderId;
    }

    public void setModelOrderId(String modelOrderId) {
        this.modelOrderId = modelOrderId;
    }

    public BrokerKey getInvestmentManagerId() {
        return investmentManagerId;
    }

    public void setInvestmentManagerId(BrokerKey investmentManagerId) {
        this.investmentManagerId = investmentManagerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public ConstructionType getModelConstruction() {
        return modelConstruction;
    }

    public void setModelConstruction(ConstructionType modelConstruction) {
        this.modelConstruction = modelConstruction;
    }

}
