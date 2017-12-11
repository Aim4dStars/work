package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.portfolio.v3.model.InvestmentAssetDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.CashManagementValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.DatedValuationKey;
import com.bt.nextgen.api.portfolio.v3.model.valuation.InvestmentValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedFundValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ManagedPortfolioValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.OtherAssetValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.QuantisedAssetValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ShareValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.TermDepositValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationDto;
import com.bt.nextgen.api.portfolio.v3.model.valuation.ValuationSummaryDto;
import com.bt.nextgen.api.portfolio.v3.service.valuation.ValuationDtoService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.UriMappingConstants;
import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.api.operation.FindByKey;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.service.integration.account.IncomePreference;
import com.bt.nextgen.service.integration.base.SystemType;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Report("portfolioValuationCsvReport")
public class PortfolioValuationCsvReport {

    private static final String CATEGORY_MANAGED_PORTFOLIO = "Managed portfolios";
    private static final String CATEGORY_CASH = "Cash";
    private static final String CATEGORY_TERM_DEPOSITS = "Term deposits";
    private static final String PARAM_INCLUDE_EXTERNAL = "include-external";
    private static final String CATEGORY_TAILORED_PORTFOLIOS = "Tailored portfolios";
    private static final String ASSET_NAME_INCOME_ACCRUED = "Income accrued";
    private static final String TERMDEPOSIT_ASSET_NAME_INTEREST_ACCRUED = "Interest accrued";
    private static final String ASSET_NAME_OUTSTANDING_CASH = "Outstanding cash";
    private static final String HYPHEN = "-";
    @Autowired
    @Qualifier("ValuationDtoServiceV3")
    private ValuationDtoService valuationService;

    private ValuationDto getValuation(Map<String, String> params) {
        String accountId = params.get(UriMappingConstants.ACCOUNT_ID_URI_MAPPING);

        String effectiveDateStr = params.get(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING);
        effectiveDateStr = StringUtils.isBlank(effectiveDateStr) ? null : effectiveDateStr;
        DateTime effectiveDate = new DateTime(effectiveDateStr);

        String includeExternal = params.get(PARAM_INCLUDE_EXTERNAL);
        boolean includeExternalBool = Boolean.parseBoolean(includeExternal);

        DatedValuationKey key = new DatedValuationKey(accountId, effectiveDate, includeExternalBool);

        ApiResponse response = new FindByKey<>(ApiVersion.CURRENT_VERSION, valuationService, key).performOperation();
        ValuationDto valuationDto = (ValuationDto) response.getData();

        return valuationDto;
    }

    @ReportBean("valuations")
    public List<InvestmentDetailDto> getInvestmentDetails(Map<String, String> params) {
        ValuationDto valuationDto = getValuation(params);
        List<ValuationSummaryDto> valuationCategories = valuationDto.getCategories();

        List<InvestmentDetailDto> investmentDetails = getInvestmentDetails(valuationCategories, params);

        return investmentDetails;

    }

    private List<InvestmentDetailDto> getInvestmentDetails(List<ValuationSummaryDto> valuationCategories,
                                                           Map<String, String> params) {
        List<InvestmentDetailDto> investmentDetails = new ArrayList<>();
        String cashAccountName = getCashAccountName(valuationCategories);
        for (ValuationSummaryDto valuationCategory : valuationCategories) {
            List<InvestmentValuationDto> investmentValuations = valuationCategory.getInvestments();
            for (InvestmentValuationDto investmentValuation : investmentValuations) {
                List<InvestmentDetailDto> investmentDetailDtos = retrieveInvestmentDetail(valuationCategory, investmentValuation,
                        cashAccountName, valuationCategory.getThirdPartySource());
                investmentDetails.addAll(investmentDetailDtos);
            }
            if (!SystemType.WRAP.name().equals(valuationCategory.getThirdPartySource())) {
                if (CATEGORY_CASH.equals(valuationCategory.getCategoryName())) {
                    investmentDetails.add(getOutstandingCash(valuationCategory, params));
                }
                if (!CATEGORY_MANAGED_PORTFOLIO.equals(valuationCategory.getCategoryName())
                        && !CATEGORY_TAILORED_PORTFOLIOS.equals(valuationCategory.getCategoryName())) {
                    InvestmentDetailDto incomeAccInvestmentDetail = getIncomeAccruedInvestmentDetail(valuationCategory, null);
                    investmentDetails.add(incomeAccInvestmentDetail);
                }
            }
        }
        return investmentDetails;
    }

    private List<InvestmentDetailDto> retrieveInvestmentDetail(ValuationSummaryDto valuationCategory,
                                                               InvestmentValuationDto investmentValuationDto, String cashAccountName, String thirdPartySource) {
        List<InvestmentDetailDto> investmentDetails = new ArrayList<>();
        if (investmentValuationDto instanceof CashManagementValuationDto) {
            investmentDetails.add(new InvestmentDetailDto((CashManagementValuationDto) investmentValuationDto, thirdPartySource));
        }
        else if (investmentValuationDto instanceof TermDepositValuationDto) {
            investmentDetails.add(new InvestmentDetailDto((TermDepositValuationDto) investmentValuationDto, thirdPartySource));
        }
        else if (investmentValuationDto instanceof ManagedFundValuationDto) {
            investmentDetails.add(new InvestmentDetailDto((ManagedFundValuationDto) investmentValuationDto));
        }
        else if (investmentValuationDto instanceof QuantisedAssetValuationDto) {
            investmentDetails.add(new InvestmentDetailDto((QuantisedAssetValuationDto) investmentValuationDto));
        }
        else if (investmentValuationDto instanceof OtherAssetValuationDto) {
            investmentDetails.add(new InvestmentDetailDto((OtherAssetValuationDto) investmentValuationDto));
        }
        else if (investmentValuationDto instanceof ManagedPortfolioValuationDto) {
            investmentDetails = getManagedPortfolioInvestmentDetails(valuationCategory,
                    (ManagedPortfolioValuationDto) investmentValuationDto, cashAccountName);
        }
        else if (investmentValuationDto instanceof ShareValuationDto) {
            investmentDetails.add(new InvestmentDetailDto((ShareValuationDto) investmentValuationDto));
        }
        return investmentDetails;
    }

    private InvestmentDetailDto getIncomeAccruedInvestmentDetail(ValuationSummaryDto valuationCategory,
                                                                 InvestmentValuationDto investmentValuation) {
        if (CATEGORY_MANAGED_PORTFOLIO.equals(valuationCategory.getCategoryName())
                || CATEGORY_TAILORED_PORTFOLIOS.equals(valuationCategory.getCategoryName())) {
            ManagedPortfolioValuationDto mpInvestmentValuation = (ManagedPortfolioValuationDto) investmentValuation;
            String categoryName = valuationCategory.getCategoryName() + HYPHEN + mpInvestmentValuation.getName();
            return new InvestmentDetailDto(categoryName, ASSET_NAME_INCOME_ACCRUED, mpInvestmentValuation.getIncome(),
                    mpInvestmentValuation.getIncomePercent());
        }
        else if (CATEGORY_TERM_DEPOSITS.equals(valuationCategory.getCategoryName())) {
            return new InvestmentDetailDto(valuationCategory.getCategoryName(), TERMDEPOSIT_ASSET_NAME_INTEREST_ACCRUED,
                    valuationCategory.getIncome(), valuationCategory.getIncomePercent());
        }
        else {
            return new InvestmentDetailDto(valuationCategory.getCategoryName(), ASSET_NAME_INCOME_ACCRUED,
                    valuationCategory.getIncome(), valuationCategory.getIncomePercent());
        }
    }

    private InvestmentDetailDto getOutstandingCash(ValuationSummaryDto valuationCategory, Map<String, String> params) {
        return new InvestmentDetailDto(valuationCategory.getCategoryName(), ASSET_NAME_OUTSTANDING_CASH,
                valuationCategory.getOutstandingCash(), valuationCategory.getOutstandingCashPercent());
    }

    private String getCashAccountName(List<ValuationSummaryDto> valuationCategories) {
        for (ValuationSummaryDto valuationCategory : valuationCategories) {
            if (CATEGORY_CASH.equals(valuationCategory.getCategoryName()) && !valuationCategory.getInvestments().isEmpty()) {
                return valuationCategory.getInvestments().get(0).getName();
            }
        }
        return "";
    }

    private List<InvestmentDetailDto> getManagedPortfolioInvestmentDetails(ValuationSummaryDto valuationCategory,
                                                                           ManagedPortfolioValuationDto mpValuationDto, String cashAccountName) {
        List<InvestmentDetailDto> mpInvestmentDetails = new ArrayList<>();
        List<InvestmentAssetDto> investmentAssets = mpValuationDto.getInvestmentAssets();
        InvestmentDetailDto incomeAccInvestmentDetail = null;
        String categoryName = null;
        if (!mpValuationDto.getExternalAsset()) {
            for (InvestmentAssetDto investmentAsset : investmentAssets) {
                if (mpValuationDto.getTailorMade()) {
                    categoryName = CATEGORY_TAILORED_PORTFOLIOS + HYPHEN + mpValuationDto.getName();
                }
                else {
                    categoryName = CATEGORY_MANAGED_PORTFOLIO + HYPHEN + mpValuationDto.getName();
                }

                String incomeElection = null;
                if (!AssetType.CASH.name().equals(investmentAsset.getAssetType())) {
                    if (IncomePreference.TRANSFER.equals(mpValuationDto.getIncomePreference())) {
                        incomeElection = "Transfer to " + cashAccountName;
                    }
                    else {
                        incomeElection = "Reinvest into model";
                    }
                }

                mpInvestmentDetails.add(new InvestmentDetailDto(investmentAsset, categoryName, incomeElection));
                if (AssetType.CASH.name().equals(investmentAsset.getAssetType())) {
                    incomeAccInvestmentDetail = getIncomeAccruedInvestmentDetail(valuationCategory, mpValuationDto);
                    mpInvestmentDetails.add(incomeAccInvestmentDetail);
                }
            }
        }
        else {
            mpInvestmentDetails.add(new InvestmentDetailDto(mpValuationDto));
        }
        return mpInvestmentDetails;
    }

    @ReportBean("showCurrentDateView")
    public Boolean showCurrentDateView(Map<String, String> params) {
        return isToday(params);
    }

    private boolean isToday(Map<String, String> params) {
        String effectiveDateStr = params.get(UriMappingConstants.EFFECTIVE_DATE_PARAMETER_MAPPING);
        effectiveDateStr = StringUtils.isBlank(effectiveDateStr) ? null : effectiveDateStr;
        DateTime effectiveDate = new DateTime(effectiveDateStr);
        return effectiveDate.toLocalDate().equals(new LocalDate());
    }

}
