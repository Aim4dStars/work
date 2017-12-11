package com.bt.nextgen.service.avaloq.modelportfolio.detail;

import com.bt.nextgen.core.validation.ValidationError;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.client.BrokerKeyConverter;
import com.bt.nextgen.service.avaloq.transaction.TransactionValidationImpl;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.ModelPortfolioStatus;
import com.bt.nextgen.service.integration.modelportfolio.detail.OfferDetail;
import com.bt.nextgen.service.integration.modelportfolio.detail.TargetAllocation;
import com.bt.nextgen.service.integration.transaction.TransactionResponse;
import com.bt.nextgen.service.integration.transaction.TransactionValidation;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@ServiceBean(xpath = "/", type = ServiceBeanType.CONCRETE)
public class ModelPortfolioDetailImpl implements ModelPortfolioDetail, TransactionResponse {

    private static final String XML_HEADER = "//data/";

    private static final String CODE_CATEGORY_STATUS = "IPS_STATUS";
    private static final String CODE_CATEGORY_STRUCTURE = "MODEL_STRUCT";
    private static final String CODE_CATEGORY_INVESTMENT_STYLE = "IPS_INVESTMENT_STYLE";
    private static final String CODE_CATEGORY_TYPE = "MP_TYPE";
    private static final String CODE_CATEGORY_MODEL_ASSET_CLASS = "IPS_ASSET_CLASS";
    private static final String CODE_CATEGORY_CONSTRUCTION = "CONSTRUCTION_TYPE";
    private static final String CODE_ACCOUNT_TYPE = "IPS_MODEL_TYPE";

    @ServiceElement(xpath = XML_HEADER + "ips_id/val")
    private String id;

    @ServiceElement(xpath = XML_HEADER + "name/val")
    private String name;

    @ServiceElement(xpath = XML_HEADER + "ips_sym/val")
    private String symbol;

    @ServiceElement(xpath = XML_HEADER + "invst_mgr/val", converter = BrokerKeyConverter.class)
    private BrokerKey investmentManagerId;

    @ServiceElement(xpath = XML_HEADER + "status/val", staticCodeCategory = CODE_CATEGORY_STATUS)
    private ModelPortfolioStatus status;

    @ServiceElement(xpath = XML_HEADER + "open_date/val", converter = DateTimeTypeConverter.class)
    private DateTime openDate;

    @ServiceElement(xpath = XML_HEADER + "model_struct/val", staticCodeCategory = CODE_CATEGORY_STRUCTURE)
    private String modelStructure;

    @ServiceElement(xpath = XML_HEADER + "invst_style/val", staticCodeCategory = CODE_CATEGORY_INVESTMENT_STYLE)
    private String investmentStyle;

    @ServiceElement(xpath = XML_HEADER + "mp_type/val", staticCodeCategory = CODE_CATEGORY_TYPE)
    private String modelType;

    @ServiceElement(xpath = XML_HEADER + "asset_class/val", staticCodeCategory = CODE_CATEGORY_MODEL_ASSET_CLASS)
    private String modelAssetClass;

    @ServiceElement(xpath = XML_HEADER + "cton_type/val", staticCodeCategory = CODE_CATEGORY_CONSTRUCTION)
    private ConstructionType modelConstruction;

    @ServiceElement(xpath = XML_HEADER + "portf_cton_fee/val")
    private BigDecimal portfolioConstructionFee;

    @ServiceElement(xpath = XML_HEADER + "min_init_invst/val")
    private BigDecimal minimumInvestment;

    @ServiceElement(xpath = XML_HEADER + "taa_list/taa", type = TargetAllocationImpl.class)
    private List<TargetAllocation> targetAllocations;

    @ServiceElement(xpath = XML_HEADER + "acc_type/val", staticCodeCategory = CODE_ACCOUNT_TYPE)
    private String accountType;

    @Deprecated
    @ServiceElement(xpath = XML_HEADER + "offer_list/offer", type = OfferDetailImpl.class)
    private List<OfferDetail> offerDetails;

    @ServiceElement(xpath = XML_HEADER + "invst_style_text/val")
    private String investmentStyleDesc;

    @ServiceElement(xpath = XML_HEADER + "pp_descn/val")
    private String modelDescription;

    @ServiceElement(xpath = XML_HEADER + "pp_par/min_trade_pct/val")
    private BigDecimal minimumTradePercent;

    @ServiceElement(xpath = XML_HEADER + "pp_par/min_trade_amount/val")
    private BigDecimal minimumTradeAmount;

    @ServiceElement(xpath = "//rsp/valid/err_list/err | //rsp/exec/err_list/err", type = TransactionValidationImpl.class)
    private List<TransactionValidation> warnings;

    private List<ValidationError> validationErrors;
    private String mpSubType;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public BrokerKey getInvestmentManagerId() {
        return investmentManagerId;
    }

    public void setInvestmentManagerId(BrokerKey investmentManagerId) {
        this.investmentManagerId = investmentManagerId;
    }

    @Override
    public ModelPortfolioStatus getStatus() {
        return status;
    }

    public void setStatus(ModelPortfolioStatus status) {
        this.status = status;
    }

    @Override
    public DateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(DateTime openDate) {
        this.openDate = openDate;
    }

    @Override
    public String getModelStructure() {
        return modelStructure;
    }

    public void setModelStructure(String modelStructure) {
        this.modelStructure = modelStructure;
    }

    @Override
    public String getInvestmentStyle() {
        return investmentStyle;
    }

    public void setInvestmentStyle(String investmentStyle) {
        this.investmentStyle = investmentStyle;
    }

    @Override
    public String getModelAssetClass() {
        return modelAssetClass;
    }

    public void setModelAssetClass(String modelAssetClass) {
        this.modelAssetClass = modelAssetClass;
    }

    @Override
    public String getModelType() {
        return modelType;
    }

    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    @Override
    public ConstructionType getModelConstruction() {
        return modelConstruction;
    }

    public void setModelConstruction(ConstructionType modelConstruction) {
        this.modelConstruction = modelConstruction;
    }

    @Override
    public BigDecimal getPortfolioConstructionFee() {
        return portfolioConstructionFee;
    }

    public void setPortfolioConstructionFee(BigDecimal portfolioConstructionFee) {
        this.portfolioConstructionFee = portfolioConstructionFee;
    }

    @Override
    public BigDecimal getMinimumInvestment() {
        return minimumInvestment;
    }

    public void setMinimumInvestment(BigDecimal minimumInvestment) {
        this.minimumInvestment = minimumInvestment;
    }

    @Override
    public List<TargetAllocation> getTargetAllocations() {
        return targetAllocations;
    }

    public void setTargetAllocations(List<TargetAllocation> targetAllocations) {
        this.targetAllocations = targetAllocations;
    }

    @Override
    public List<TransactionValidation> getWarnings() {
        return warnings;
    }

    public void setWarnings(List<TransactionValidation> warnings) {
        this.warnings = warnings;
    }

    @Override
    public String getLocListItem(Integer index) {
        if (targetAllocations != null) {
            return targetAllocations.get(index).getIndexAssetId();
        }
        return null;
    }

    @Override
    public BigInteger getLocItemIndex(String itemId) {
        int i = 1;
        if (targetAllocations != null) {
            for (TargetAllocation allocation : targetAllocations) {
                if (allocation.getIndexAssetId().equals(itemId)) {
                    return BigInteger.valueOf(i);
                }
                i++;
            }
        }
        return null;
    }

    @Override
    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    @Override
    public String getAalId() {
        return null;
    }

    @Deprecated
    public List<OfferDetail> getOfferDetails() {
        return offerDetails;
    }

    @Deprecated
    public void setOfferDetails(List<OfferDetail> offerDetails) {
        this.offerDetails = offerDetails;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getMpSubType() {
        return mpSubType;
    }

    public void setMpSubType(String mpSubType) {
        this.mpSubType = mpSubType;
    }

    public String getInvestmentStyleDesc() {
        return investmentStyleDesc;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public BigDecimal getMinimumTradePercent() {
        return minimumTradePercent;
    }

    public BigDecimal getMinimumTradeAmount() {
        return minimumTradeAmount;
    }

    public void setInvestmentStyleDesc(String investmentStyleDesc) {
        this.investmentStyleDesc = investmentStyleDesc;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

    public void setMinimumTradePercent(BigDecimal minimumTradePercent) {
        this.minimumTradePercent = minimumTradePercent;
    }

    public void setMinimumTradeAmount(BigDecimal minimumTradeAmount) {
        this.minimumTradeAmount = minimumTradeAmount;
    }

}