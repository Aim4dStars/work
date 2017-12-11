package com.bt.nextgen.service.avaloq.modelportfolio;

import com.bt.nextgen.core.conversion.BigDecimalConverter;
import com.bt.nextgen.core.conversion.BooleanConverter;
import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceElement;
import com.bt.nextgen.service.avaloq.ips.IpsKeyConverter;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelRebalanceStatus;
import com.btfin.panorama.core.conversion.DateTimeTypeConverter;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;

import java.math.BigDecimal;

@ServiceBean(xpath = "ips | report_foot")
public class ModelPortfolioSummaryImpl implements ModelPortfolioSummary {

    @ServiceElement(xpath = "ips_head_list/ips_head/ips/annot/ctx/id | ips/annot/ctx/id", converter = IpsKeyConverter.class)
    @NotNull
	private IpsKey modelKey;

    @ServiceElement(xpath = "ips_head_list/ips_head/ips/val | ips/val")
    @NotNull
	private String modelName;

    @ServiceElement(xpath = "ips_head_list/ips_head/ips_sym/val | ips_sym/val")
    @NotNull
	private String modelCode;

    @ServiceElement(xpath = "ips_head_list/ips_head/ips_apir/val | ips_apir/val")
	private String apirCode;

    @ServiceElement(xpath = "ips_head_list/ips_head/last_upd_timestp/val | last_upd_timestp/val", converter = DateTimeTypeConverter.class)
	private DateTime lastUpdateDate;

    @ServiceElement(xpath = "ips_head_list/ips_head/last_upd_from/val | last_upd_from/val")
	private String lastUpdatedBy;

    @ServiceElement(xpath = "ips_head_list/ips_head/ips_asset_class_id/val | ips_asset_class_id/val", converter = IpsAssetClassConverter.class)
    private String assetClass;

    @ServiceElement(xpath = "ips_head_list/ips_head/ips_invst_style_id/val | ips_invst_style_id/val", converter = IpsInvestmentStyleConverter.class)
	private String investmentStyle;

    @ServiceElement(xpath = "ips_head_list/ips_head/ips_status_id/val | ips_status_id/val", staticCodeCategory = "IPS_STATUS")
    @NotNull
    private IpsStatus status;

    @ServiceElement(xpath = "ips_head_list/ips_head/aum/val | aum/val", converter = BigDecimalConverter.class)
    @NotNull
	private BigDecimal fum;

    @ServiceElement(xpath = "ips_head_list/ips_head/vol_secevt2_in_work/val | vol_secevt2_in_work/val", converter = BigDecimalConverter.class)
	private BigDecimal voluntaryCorporateActions;

    @ServiceElement(xpath = "ips_head_list/ips_head/mand_secevt2_in_work/val | mand_secevt2_in_work/val", converter = BigDecimalConverter.class)
	private BigDecimal mandatoryCorporateActions;

    @ServiceElement(xpath = "ips_head_list/ips_head/last_doc_id/val | last_doc_id/val")
	private String ipsOrderId;

    @ServiceElement(xpath = "ips_head_list/ips_head/last_edit_doc_id/val | last_edit_doc_id/val")
	private String modelOrderId;

    @ServiceElement(xpath = "ips_head_list/ips_head/last_rebal_user/val | last_rebal_user/val")
    private String lastRebalanceUser;

    @ServiceElement(xpath = "ips_head_list/ips_head/last_rebal_date/val | last_rebal_date/val", converter = DateTimeTypeConverter.class)
    private DateTime lastRebalanceDate;

    @ServiceElement(xpath = "ips_head_list/ips_head/full_scan/val | full_scan/val", converter = BooleanConverter.class)
    private Boolean hasScanTrigger;

    @ServiceElement(xpath = "ips_head_list/ips_head/last_rebal_status_id/val | last_rebal_status_id/val", staticCodeCategory = "IPS_REBAL_STATUS")
    private ModelRebalanceStatus rebalanceStatus;

    @ServiceElement(xpath = "ips_head_list/ips_head/clt_cont_cnt/val | clt_cont_cnt/val")
    private Integer numAccounts;

    @ServiceElement(xpath = "ips_head_list/ips_head/acc_type_id/val | acc_type_id/val", staticCodeCategory = "IPS_MODEL_TYPE")
    private ModelType accountType;

    @ServiceElement(xpath = "ips_head_list/ips_head/cton_type_id/val | cton_type_id/val", staticCodeCategory = "CONSTRUCTION_TYPE")
    private ConstructionType modelConstruction;
    
    @ServiceElement(xpath = "ips_head_list/ips_head/open_date/val | open_date/val", converter = DateTimeTypeConverter.class)
    private DateTime openDate;

    @ServiceElement(xpath = "ips_head_list/ips_head/pp_descn/val | pp_descn/val")
    private String modelDescription;



	@Override
	public IpsKey getModelKey() {
		return modelKey;
	}

	public void setModelKey(IpsKey modelKey) {
		this.modelKey = modelKey;
	}

	@Override
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	@Override
	public String getModelCode() {
		return modelCode;
	}


	@Override
	public String getApirCode(){
		return apirCode;
	}

	public void setModelCode(String modelCode) {
		this.modelCode = modelCode;
	}

	public void setApirCode(String apirCode){
		this.apirCode=apirCode;
	}

	@Override
	public DateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(DateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Override
	public String getLastUpdatedBy() {
		return lastUpdatedBy;
	}

	public void setLastUpdatedBy(String lastUpdatedBy) {
		this.lastUpdatedBy = lastUpdatedBy;
	}

	@Override
	public String getAssetClass() {
		return assetClass;
	}

	public void setAssetClass(String assetClass) {
		this.assetClass = assetClass;
	}

	@Override
	public String getInvestmentStyle() {
		return investmentStyle;
	}

	public void setInvestmentStyle(String investmentStyle) {
		this.investmentStyle = investmentStyle;
	}

	@Override
    public IpsStatus getStatus() {
        return status;
	}

    public void setStatus(IpsStatus status) {
		this.status = status;
	}

	@Override
	public BigDecimal getFum() {
		return fum;
	}

	public void setFum(BigDecimal fum) {
		this.fum = fum;
	}

	@Override
	public Boolean getHasCorporateActions() {
		return getHasVoluntaryCorporateActions() || getHasMandatoryCorporateActions();
	}

	@Override
	public Boolean getHasVoluntaryCorporateActions() {
		return BigDecimal.ONE.equals(voluntaryCorporateActions);
	}

	@Override
	public Boolean getHasMandatoryCorporateActions() {
		return BigDecimal.ONE.equals(mandatoryCorporateActions);
	}

	public BigDecimal getVoluntaryCorporateActions() {
		return voluntaryCorporateActions;
	}

	public void setVoluntaryCorporateActions(BigDecimal voluntaryCorporateActions) {
		this.voluntaryCorporateActions = voluntaryCorporateActions;
	}

	public BigDecimal getMandatoryCorporateActions() {
		return mandatoryCorporateActions;
	}

	public void setMandatoryCorporateActions(BigDecimal mandatoryCorporateActions) {
		this.mandatoryCorporateActions = mandatoryCorporateActions;
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

    @Override
    public String getLastRebalanceUser() {
        return lastRebalanceUser;
    }

    public void setLastRebalanceUser(String lastRebalanceUser) {
        this.lastRebalanceUser = lastRebalanceUser;
    }

    @Override
    public DateTime getLastRebalanceDate() {
        return lastRebalanceDate;
    }

    public void setLastRebalanceDate(DateTime lastRebalanceDate) {
        this.lastRebalanceDate = lastRebalanceDate;
    }

    @Override
    public Integer getNumAccounts() {
        return numAccounts;
    }

    public void setNumAccounts(Integer numAccounts) {
        this.numAccounts = numAccounts;
    }

    @Override
    public Boolean getHasScanTrigger() {
        return hasScanTrigger;
    }

    public void setHasScanTrigger(Boolean hasScanTrigger) {
        this.hasScanTrigger = hasScanTrigger;
    }

    @Override
    public ModelRebalanceStatus getRebalanceStatus() {
        return rebalanceStatus;
    }

    public void setRebalanceStatus(ModelRebalanceStatus rebalanceStatus) {
        this.rebalanceStatus = rebalanceStatus;
    }

    @Override
    public ModelType getAccountType() {
        return accountType;
    }

    public void setAccountType(ModelType accountType) {
        this.accountType = accountType;
    }

    public ConstructionType getModelConstruction() {
        return modelConstruction;
    }

    public void setModelConstruction(ConstructionType modelConstruction) {
        this.modelConstruction = modelConstruction;
    }

    public DateTime getOpenDate() {
        return openDate;
    }

    public void setOpenDate(DateTime openDate) {
        this.openDate = openDate;
    }

    public String getModelDescription() {
        return modelDescription;
    }

    public void setModelDescription(String modelDescription) {
        this.modelDescription = modelDescription;
    }

}
