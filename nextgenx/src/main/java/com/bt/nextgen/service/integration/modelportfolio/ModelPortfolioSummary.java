package com.bt.nextgen.service.integration.modelportfolio;

import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.modelportfolio.common.IpsStatus;
import com.bt.nextgen.service.integration.modelportfolio.common.ModelType;
import com.bt.nextgen.service.integration.modelportfolio.detail.ConstructionType;
import com.bt.nextgen.service.integration.modelportfolio.rebalance.ModelRebalanceStatus;
import org.joda.time.DateTime;

import java.math.BigDecimal;

public interface ModelPortfolioSummary {
	IpsKey getModelKey();

	String getModelName();

	String getModelCode();

	String getApirCode();

	DateTime getLastUpdateDate();

	String getLastUpdatedBy();

	String getAssetClass();

	String getInvestmentStyle();

    IpsStatus getStatus();

	Boolean getHasCorporateActions();

	Boolean getHasVoluntaryCorporateActions();

	Boolean getHasMandatoryCorporateActions();

	BigDecimal getFum();

	/**
	 * Order ID of the last IPS update.
	 *
	 * @return
	 */
	String getIpsOrderId();

	/**
	 * Order ID of the last model edit.
	 *
	 * @return
	 */
	String getModelOrderId();

    String getLastRebalanceUser();

    DateTime getLastRebalanceDate();

    Integer getNumAccounts();

    ModelRebalanceStatus getRebalanceStatus();

    Boolean getHasScanTrigger();

    ModelType getAccountType();

    ConstructionType getModelConstruction();

    String getModelDescription();

    DateTime getOpenDate();
}
