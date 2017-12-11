package com.bt.nextgen.service.avaloq.ips;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.avaloq.modelportfolio.detail.OfferDetailImpl;
import com.bt.nextgen.service.avaloq.modelportfolio.detail.TargetAllocationImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.OfferDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@ServiceBean(xpath = "ips")
public class IpsDetails implements ModelPortfolioDetail {

    private static final String XML_HEADER = "ips_head_list/ips_head/";

    @ServiceElement(xpath = XML_HEADER + "ips_id/val")
    private String id;

    @ServiceElement(xpath = XML_HEADER + "ips_name/val")
    private String name;

    @ServiceElement(xpath = XML_HEADER + "ips_sym/val")
    private String symbol;

    @ServiceElement(xpath = XML_HEADER + "invst_mgr_id/val", converter = BrokerKeyConverter.class)
    private BrokerKey investmentManagerId;

    @ServiceElement(xpath = XML_HEADER + "ips_status_id/val", staticCodeCategory = "IPS_STATUS")
    private ModelPortfolioStatus status;

    @ServiceElement(xpath = XML_HEADER + "open_date/val", converter = DateTimeTypeConverter.class)
    private DateTime openDate;

    @ServiceElement(xpath = XML_HEADER + "model_struct_id/val", staticCodeCategory = "MODEL_STRUCT")
    private String modelStructure;

    @ServiceElement(xpath = XML_HEADER + "ips_invst_style_id/val", staticCodeCategory = "IPS_INVESTMENT_STYLE")
    private String investmentStyle;

    @ServiceElement(xpath = XML_HEADER + "mp_type_id/val", staticCodeCategory = "MP_TYPE")
    private String modelType;

    @ServiceElement(xpath = XML_HEADER + "ips_asset_class_id/val", staticCodeCategory = "IPS_ASSET_CLASS")
    private String modelAssetClass;

    @ServiceElement(xpath = XML_HEADER + "cton_type_id/val", staticCodeCategory = "CONSTRUCTION_TYPE")
    private ConstructionType modelConstruction;

    @ServiceElement(xpath = XML_HEADER + "min_init_invst/val")
    private BigDecimal minimumInvestment;

    @ServiceElement(xpath = XML_HEADER + "taa_list_list/taa_list", type = TargetAllocationImpl.class)
    private List<TargetAllocation> targetAllocations;

    @ServiceElement(xpath = XML_HEADER + "aal_collect_id/val")
    private String aalId;

    @ServiceElement(xpath = XML_HEADER + "acc_type_id/val")
    private String accountType;

    @Deprecated
    @ServiceElement(xpath = XML_HEADER + "offer_list/offer", type = OfferDetailImpl.class)
    private List<OfferDetail> offerDetails;

    @ServiceElement(xpath = XML_HEADER + "pp_descn/val")
    private String modelDescription;

    @ServiceElement(xpath = XML_HEADER + "pp_min_trade_amt/val")
    private BigDecimal minimumTradeAmount;

    @ServiceElement(xpath = XML_HEADER + "pp_min_trade_pct/val")
    private BigDecimal minimumTradePercent;

    @ServiceElement(xpath = XML_HEADER + "pp_invst_style_text/val")
    private String investmentStyleText;

    @ServiceElement(xpath = XML_HEADER + "mp_sub_type_id/val", staticCodeCategory = "ASSET_MODEL_TYPE")
    private String mpSubType;

    public IpsKey getIpsKey() {
        return IpsKey.valueOf(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public BrokerKey getInvestmentManagerId() {
        return investmentManagerId;
    }

    public void setInvestmentManagerId(BrokerKey investmentManagerId) {
        this.investmentManagerId = investmentManagerId;
    }

    public ModelPortfolioStatus getStatus() {
        return status;
    }

    public void setStatus(ModelPortfolioStatus status) {
        this.status = status;
    }

    public DateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(DateTime openDate) {
        this.openDate = openDate;
    }

    public String getModelStructure() {
        return modelStructure;
    }

    public void setModelStructure(String modelStructure) {
        this.modelStructure = modelStructure;
    }

    public String getInvestmentStyle() {
        return investmentStyle;
    }

    public void setInvestmentStyle(String investmentStyle) {
        this.investmentStyle = investmentStyle;
    }

    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    public String getModelAssetClass() {
        return modelAssetClass;
    }

    public void setModelAssetClass(String modelAssetClass) {
        this.modelAssetClass = modelAssetClass;
    }

    public ConstructionType getModelConstruction() {
        return modelConstruction;
    }

    public void setModelConstruction(ConstructionType modelConstruction) {
        this.modelConstruction = modelConstruction;
    }

    public BigDecimal getMinimumInvestment() {
        return minimumInvestment;
    }

    public void setMinimumInvestment(BigDecimal minimumInvestment) {
        this.minimumInvestment = minimumInvestment;
    }

    public List<TargetAllocation> getTargetAllocations() {
        return targetAllocations;
    }

    public void setTargetAllocations(List<TargetAllocation> targetAllocations) {
        this.targetAllocations = targetAllocations;
    }

    @Override
    public BigDecimal getPortfolioConstructionFee() {
        return null;
    }

    public String getAalId() {
        return aalId;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public void setAalId(String aalId) {
        this.aalId = aalId;
    }

    @Override
    public List<TransactionValidation> getWarnings() {
        return Collections.emptyList();
    }

    @Override
    public List<ValidationError> getValidationErrors() {
        return Collections.emptyList();
    }

    @Override
    public String getAccountType() {
        return accountType;
    }

    @Deprecated
    @Override
    public List<OfferDetail> getOfferDetails() {
        return this.offerDetails;
    }

    @Deprecated
    public void setOfferDetails(List<OfferDetail> offerDetails) {
        this.offerDetails = offerDetails;
    }

    @Override
    public String getMpSubType() {
        return mpSubType;
    }

    public void setMpSubType(String mpSubType) {
        this.mpSubType = mpSubType;
    }

    @Override
    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    @Override
    public BigDecimal getMinimumTradePercent() {
        return minimumTradePercent;
    }

    public void setMinimumTradePercent(BigDecimal minimumTradePercent) {
        this.minimumTradePercent = minimumTradePercent;
    }

    @Override
    public BigDecimal getMinimumTradeAmount() {
        return minimumTradeAmount;
    }

    public void setMinimumTradeAmount(BigDecimal minimumTradeAmount) {
        this.minimumTradeAmount = minimumTradeAmount;
    }

    @Override
    public String getInvestmentStyleDesc() {
        return investmentStyleText;
    }

    public void setInvestmentStyleDesc(String investmentStyleText) {
        this.investmentStyleText = investmentStyleText;
    }
}
