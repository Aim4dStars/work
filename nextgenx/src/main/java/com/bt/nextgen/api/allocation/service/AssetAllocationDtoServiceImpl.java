package com.bt.nextgen.api.allocation.service;

import com.bt.nextgen.api.account.v1.model.DatedAccountKey;
import com.bt.nextgen.api.account.v2.service.TermDepositPresentationService;
import com.bt.nextgen.api.allocation.model.AllocationByAssetSectorDto;
import com.bt.nextgen.api.allocation.model.AllocationDetails;
import com.bt.nextgen.api.allocation.model.AllocationDto;
import com.bt.nextgen.api.allocation.model.AssetSectorEnum;
import com.bt.nextgen.api.allocation.model.HoldingAllocationDto;
import com.bt.nextgen.api.allocation.model.InvestmentAllocationDto;
import com.bt.nextgen.api.allocation.model.TermDepositAllocationDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PortfolioUtils;
import com.bt.nextgen.service.avaloq.asset.TermDepositPresentation;
import com.bt.nextgen.service.avaloq.portfolio.valuation.TermDepositHoldingImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.portfolio.PortfolioIntegrationService;
import com.bt.nextgen.service.integration.portfolio.valuation.AccountHolding;
import com.bt.nextgen.service.integration.portfolio.valuation.ManagedPortfolioAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.SubAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.TermDepositAccountValuation;
import com.bt.nextgen.service.integration.portfolio.valuation.WrapAccountValuation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @deprecated use account.v2.service.allocation
 */
@Service
@Transactional(value = "springJpaTransactionManager")
@Deprecated
public class AssetAllocationDtoServiceImpl implements AllocationDtoService {
    private static final Logger logger = LoggerFactory.getLogger(AssetAllocationDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqPortfolioIntegrationService")
    private PortfolioIntegrationService portfolioIntegrationService;

    @Autowired
    private TermDepositPresentationService termDepositPresentationService;

    @Override
    @Transactional(value = "springJpaTransactionManager", readOnly = true)
    public AllocationDto find(DatedAccountKey key, ServiceErrors serviceErrors) {
        logger.info("Start of method find");

        // Retrieve valuations
        com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                .valueOf(EncodedString.toPlainText(key.getAccountId()));
        WrapAccountValuation valuation = portfolioIntegrationService.loadWrapAccountValuation(accountKey, key.getEffectiveDate(),
                serviceErrors);
        if (valuation == null) {
            return new AllocationByAssetSectorDto(key, BigDecimal.ZERO, null);
        }

        List<HoldingAllocationDto> allocations = getAllInvestmentHoldings(valuation, serviceErrors);

        logger.info("End of method find");
        return new AllocationByAssetSectorDto(key, valuation.getBalance(), allocations);
    }

    private List<HoldingAllocationDto> getAllInvestmentHoldings(WrapAccountValuation valuation, ServiceErrors serviceErrors) {
        List<HoldingAllocationDto> holdingAllocations = new ArrayList<>();

        // 1. Retrieve all cash-investmentHolding within the subAccount
        holdingAllocations.addAll(processCashHoldingAllocation(valuation));
        holdingAllocations.addAll(processTermDepositsAllocation(valuation, serviceErrors));
        holdingAllocations.addAll(processManagedPortfolioCashAllocation(valuation));

        List<HoldingAllocationDto> securityList = new ArrayList<>();
        securityList.addAll(this.processManagedPortfolioAllocation(valuation));
        securityList.addAll(this.processManagedFundAllocation(valuation));
        // Consolidate investment by Asset-Id.
        holdingAllocations.addAll(this.process(securityList));

        return holdingAllocations;
    }

    private List<HoldingAllocationDto> processCashHoldingAllocation(WrapAccountValuation valuation) {
        List<HoldingAllocationDto> dtoList = new ArrayList<>();
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            AssetType assetType = subAccount.getAssetType();
            if (AssetType.CASH == assetType) {
                AllocationDetails details = new AllocationDetails(null, AssetSectorEnum.CASH.getDesc(),
                        AssetSectorEnum.CASH.getDesc(), AssetSectorEnum.CASH.getDesc(), subAccount.getBalance(),
                        PortfolioUtils.getValuationAsPercent(subAccount.getBalance(), valuation.getBalance()));

                HoldingAllocationDto cashDto = new HoldingAllocationDto(null, null, AssetType.CASH.name(), null, "BT Cash",
                        details, null);

                dtoList.add(cashDto);
            }
        }
        return dtoList;
    }

    private List<HoldingAllocationDto> processTermDepositsAllocation(WrapAccountValuation valuation, ServiceErrors serviceErrors) {
        List<HoldingAllocationDto> dtoList = new ArrayList<>();
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (AssetType.TERM_DEPOSIT.equals(subAccount.getAssetType())) {
                TermDepositAccountValuation termDepositAccount = (TermDepositAccountValuation) subAccount;
                for (AccountHolding holding : termDepositAccount.getHoldings()) {
                    TermDepositHoldingImpl td = (TermDepositHoldingImpl) holding;
                    TermDepositPresentation termDepositPresentation = termDepositPresentationService.getTermDepositPresentation(
                            valuation.getAccountKey(), td.getAsset().getAssetId(), serviceErrors);

                    AllocationDetails details = new AllocationDetails(null, AssetSectorEnum.CASH.getDesc(),
                            AssetSectorEnum.CASH.getDesc(), AssetSectorEnum.CASH.getDesc(), td.getBalance(),
                            PortfolioUtils.getValuationAsPercent(td.getBalance(), valuation.getBalance()));

                    TermDepositAllocationDto tdDto = new TermDepositAllocationDto(td.getAsset().getAssetId(), td.getAsset()
                            .getAssetCode(), AssetType.CASH.name(), termDepositPresentation.getBrandName(),
                            termDepositPresentation.getBrandClass(), details, null, td.getMaturityDate(),
                            termDepositPresentation.getTerm(), termDepositPresentation.getPaymentFrequency());

                    dtoList.add(tdDto);
                }

            }
        }
        return dtoList;
    }

    private List<HoldingAllocationDto> processManagedPortfolioCashAllocation(WrapAccountValuation valuation) {
        List<HoldingAllocationDto> cashAllocationList = new ArrayList<>();
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (AssetType.MANAGED_PORTFOLIO.equals(subAccount.getAssetType())) {
                ManagedPortfolioAccountValuation mpVal = (ManagedPortfolioAccountValuation) subAccount;
                List<AccountHolding> holdings = mpVal.getHoldings();
                BigDecimal mpCashBalance = BigDecimal.ZERO;
                for (AccountHolding holding : holdings) {
                    Asset asset = holding.getAsset();
                    AssetType assetType = asset.getAssetType();
                    if (AssetType.CASH == assetType) {
                        // Balance = Market value + interest earned
                        mpCashBalance = mpCashBalance.add(holding.getBalance());
                    } else if (holding.getAccruedIncome() != null) {
                        // For non-cash: Includes Dividend and Distribution
                        // amount as well
                        mpCashBalance = mpCashBalance.add(holding.getAccruedIncome());
                    }
                }
                if (!mpCashBalance.equals(BigDecimal.ZERO)) {
                    AllocationDetails details = new AllocationDetails(null, AssetSectorEnum.CASH.getDesc(),
                            AssetSectorEnum.CASH.getDesc(), AssetSectorEnum.CASH.getDesc(), mpCashBalance, // changed
                                                                                                           // to
                                                                                                           // fix
                                                                                                           // DE728
                            PortfolioUtils.getValuationAsPercent(mpCashBalance, valuation.getBalance()));

                    Asset mpAsset = mpVal.getAsset();
                    HoldingAllocationDto cashHoldingDto = new HoldingAllocationDto(mpAsset.getAssetId(), mpAsset.getAssetCode(),
                            AssetType.CASH.name(), AssetType.MANAGED_PORTFOLIO.name(), mpAsset.getAssetName(), details, null);
                    cashAllocationList.add(cashHoldingDto);
                }
            }
        }
        return cashAllocationList;
    }

    private List<HoldingAllocationDto> processManagedPortfolioAllocation(WrapAccountValuation valuation) {
        List<HoldingAllocationDto> allocList = new ArrayList<>();
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            if (AssetType.MANAGED_PORTFOLIO.equals(subAccount.getAssetType())) {
                ManagedPortfolioAccountValuation mpVal = (ManagedPortfolioAccountValuation) subAccount;
                Asset mpAsset = mpVal.getAsset();

                List<AccountHolding> holdings = mpVal.getHoldings();
                for (AccountHolding holding : holdings) {
                    Asset asset = holding.getAsset();
                    AssetType assetType = asset.getAssetType();
                    if (AssetType.CASH != assetType) {
                        AllocationDetails details = new AllocationDetails(holding.getReferenceAsset() != null ? null
                                : holding.getUnits(), asset.getAssetClass().getDescription(), asset.getIndustrySector(),
                                asset.getIndustryType(),
                                holding.getMarketValue(), PortfolioUtils.getValuationAsPercent(holding.getMarketValue(),
                                        valuation.getBalance()));

                        // Details regarding the managed-portfolio where this
                        // holding is part of.
                        InvestmentAllocationDto invAllocDto = new InvestmentAllocationDto("Managed porfolio",
                                mpAsset.getAssetId(), mpAsset.getAssetCode(), mpAsset.getAssetType().name(),
                                mpAsset.getAssetName(), details);

                        HoldingAllocationDto holdingDto = new HoldingAllocationDto(asset.getAssetId(), asset.getAssetCode(),
                                asset.getAssetType().name(), AssetType.MANAGED_PORTFOLIO.name(), asset.getAssetName(), details,
                                Collections.singletonList(invAllocDto));
                        allocList.add(holdingDto);
                    }
                }
            }
        }
        return allocList;
    }

    private List<HoldingAllocationDto> processManagedFundAllocation(WrapAccountValuation valuation) {
        List<HoldingAllocationDto> allocList = new ArrayList<>();
        for (SubAccountValuation subAccount : valuation.getSubAccountValuations()) {
            AssetType assetType = subAccount.getAssetType();
            if (AssetType.CASH != assetType && AssetType.TERM_DEPOSIT != assetType && AssetType.MANAGED_PORTFOLIO != assetType) {
                for (AccountHolding holding : subAccount.getHoldings()) {
                    Asset asset = holding.getAsset();
                    AllocationDetails details = new AllocationDetails(holding.getReferenceAsset() != null ? null
                            : holding.getUnits(), asset.getAssetClass().getDescription(), asset.getIndustrySector(),
                            asset.getIndustryType(),
                            holding.getBalance(), PortfolioUtils.getValuationAsPercent(holding.getBalance(),
                                    valuation.getBalance()));

                    // Just a place holder
                    InvestmentAllocationDto invAllocDto = new InvestmentAllocationDto("Term deposit",
                            asset.getAssetId(), asset.getAssetCode(), asset.getAssetType().name(), holding.getHoldingKey()
                                    .getName(), details);

                    HoldingAllocationDto holdingDto = new HoldingAllocationDto(asset.getAssetId(), asset.getAssetCode(), asset
                            .getAssetType().name(), null, asset.getAssetName(), details, Collections.singletonList(invAllocDto));
                    allocList.add(holdingDto);
                }
            }
        }
        return allocList;
    }

    private List<HoldingAllocationDto> process(List<HoldingAllocationDto> allocDtoList) {
        List<HoldingAllocationDto> consolidatedList = new ArrayList<>();
        Map<String, List<InvestmentAllocationDto>> invAllocMap = new HashMap<>();
        Map<String, List<HoldingAllocationDto>> holdingAllocMap = new HashMap<>();

        for (HoldingAllocationDto holdingAlloc : allocDtoList) {
            String assetId = holdingAlloc.getAssetId();
            if (!invAllocMap.containsKey(assetId)) {
                invAllocMap.put(assetId, new ArrayList<InvestmentAllocationDto>());
            }
            // Investment allocation map.
            // Consists of allocation-breakdown of investment that consists of
            // this holding.
            // E.g. MP1(investment) with BHP(holding)
            invAllocMap.get(assetId).addAll(holdingAlloc.getInvestments());

            if (!holdingAllocMap.containsKey(assetId)) {
                holdingAllocMap.put(assetId, new ArrayList<HoldingAllocationDto>());
            }
            // Holding allocation map.
            holdingAllocMap.get(assetId).add(holdingAlloc);
        }

        // Consolidate
        for (Entry<String, List<HoldingAllocationDto>> entry : holdingAllocMap.entrySet()) {
            HoldingAllocationDto dto = entry.getValue().get(0);
            AllocationDetails detail = dto.getAllocationDetails();
            for (int i = 1; i < entry.getValue().size(); i++) {
                detail = detail.add(entry.getValue().get(i).getAllocationDetails());
            }

            List<InvestmentAllocationDto> invList = invAllocMap.get(dto.getAssetId());
            String assetName = dto.getAssetName();
            if (invList.size() == 1 && AssetType.MANAGED_FUND.name().equals(dto.getAssetType()) && dto.getHoldingType() == null) {
                // Asset name needs to be reset to the underlying holding name.
                assetName = invList.get(0).getAssetName();
                // Remove invAllocation for Independent-Managed-Fund.
                invList = null;
            }

            // Consolidated allocation details for specified holding.
            HoldingAllocationDto sumDto = new HoldingAllocationDto(dto.getAssetId(), dto.getAssetCode(), dto.getAssetType(),
                    null, assetName, detail, invList);
            consolidatedList.add(sumDto);
        }
        return consolidatedList;
    }
}
