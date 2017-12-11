package com.bt.nextgen.api.modelportfolio.v2.model;

import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolio;
import com.bt.nextgen.service.integration.modelportfolio.ModelPortfolioSummary;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.List;

public class ModelPortfolioDto extends ModelDto {

    private final DateTime asAtDate;
    private final DateTime lastUpdateDate;
    private final String status;
    private final Boolean hasCorporateActions;
    private final CashForecastDto cashForecast;
    private final List<ShadowPortfolioDto> shadowPortfolios;
    private final List<ShadowTransactionDto> shadowTransactions;

    public ModelPortfolioDto(ModelPortfolio model, ModelPortfolioSummary summary, CashForecastDto cashForecast,
            List<ShadowPortfolioDto> shadowPortfolios, List<ShadowTransactionDto> shadowTransactions) {
        super(new ModelPortfolioKey(model.getModelKey().getId()), summary);
        this.hasCorporateActions = summary == null ? false : summary.getHasCorporateActions();
        this.asAtDate = model.getShadowPortfolio() == null ? null : model.getShadowPortfolio().getAsAtDate();
        this.status = StringUtils.capitalize(model.getStatus().toLowerCase());
        this.lastUpdateDate = model.getLastUpdateDate();
        this.cashForecast = cashForecast;
        this.shadowPortfolios = shadowPortfolios;
        this.shadowTransactions = shadowTransactions;
    }

    public DateTime getAsAtDate() {
        return asAtDate;
    }

    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public String getStatus() {
        return status;
    }

    public CashForecastDto getCashForecast() {
        return cashForecast;
    }

    public List<ShadowPortfolioDto> getShadowPortfolios() {
        return shadowPortfolios;
    }

    public List<ShadowTransactionDto> getShadowTransactions() {
        return shadowTransactions;
    }

    public Boolean getHasCorporateActions() {
        return hasCorporateActions;
    }
}
