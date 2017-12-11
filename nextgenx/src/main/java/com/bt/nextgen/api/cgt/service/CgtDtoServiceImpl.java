package com.bt.nextgen.api.cgt.service;

import com.bt.nextgen.api.cgt.model.CgtGroupDto;
import com.bt.nextgen.api.cgt.model.CgtMpSecurityDto;
import com.bt.nextgen.api.cgt.model.CgtSecurity;
import com.bt.nextgen.api.cgt.model.CgtSecurityDto;
import com.bt.nextgen.service.avaloq.cgt.ManagedPortfolioCgtImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.cgt.CgtBaseData;
import com.bt.nextgen.service.integration.cgt.InvestmentCgt;
import com.bt.nextgen.service.integration.cgt.WrapCgtData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(value = "springJpaTransactionManager")
abstract class CgtDtoServiceImpl {
    protected static final String CRITERIA_EFFECTIVE_DATE = "effectiveDate";

    protected static final String REALISED_CGT_DATA = "realised";
    protected static final String UNREALISED_CGT_DATA = "unrealised";

    protected enum VIEW_BY {
        ASSET_TYPE,
        SECURITY
    }

    /**
     * Retrieve a Map where the specified assetList is grouped based on the specified groupByKey. For managed-portfolio, the key
     * would be the managed-portfolio-id, and the values will be all the corresponding assets within the assetList.
     *
     * @param securities
     *            Collection of all underlying investments within a portfolio
     * @param groupByKey
     *            'ASSET_TYPE' or 'SECURITY'
     * @return
     */
    protected Map<String, List<CgtSecurity>> groupById(List<CgtSecurity> securities, String groupByKey) {
        if (securities == null || securities.isEmpty()) {
            return null;
        }

        Map<String, List<CgtSecurity>> securitiesMap = new HashMap<String, List<CgtSecurity>>();
        for (CgtSecurity securityDto : securities) {
            String id = securityDto.getSecurityCode();
            if (VIEW_BY.ASSET_TYPE.name().equals(groupByKey) && securityDto instanceof CgtMpSecurityDto) {
                id = ((CgtMpSecurityDto) securityDto).getParentInvId();
            }

            if (!securitiesMap.containsKey(id)) {
                securitiesMap.put(id, new ArrayList<CgtSecurity>());
            }
            securitiesMap.get(id).add(securityDto);
        }
        return securitiesMap;
    }

    /**
     * Create an instance of CgtGroupDto based on the assetList specified. This method will automatically retrieved the correct
     * group-code or id based on the groupByKey specified.
     *
     * @param securities
     * @param groupByKey
     * @return
     */
    protected CgtGroupDto getCgtGroupDto(List<CgtSecurity> securities, String groupByKey) {
        if (securities == null || securities.isEmpty()) {
            return null;
        }

        // Use details from asset in group as group details
        CgtSecurity securityDto = securities.get(0);

        String groupId = securityDto.getSecurityCode();
        String groupCode = groupId;
        String groupName = securityDto.getSecurityName();
        String groupType = securityDto.getSecurityType();

        if (VIEW_BY.ASSET_TYPE.name().equals(groupByKey)) {

            if (securityDto instanceof CgtMpSecurityDto) {
                groupId = ((CgtMpSecurityDto) securityDto).getParentInvId();
                groupCode = ((CgtMpSecurityDto) securityDto).getParentInvCode();
                groupName = ((CgtMpSecurityDto) securityDto).getParentInvName();
                groupType = ((CgtMpSecurityDto) securityDto).getParentInvType();
            } else {
                groupId = securityDto.getSecurityType();
            }
        }

        CgtGroupDto groupDto = buildCgtGroupDto(groupId, groupCode, groupName, groupType, securities);
        return groupDto;

    }

    private CgtGroupDto buildCgtGroupDto(String groupId, String groupCode, String groupName, String groupType,
            List<CgtSecurity> securities) {
        int quantity = 0;
        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal taxAmount = BigDecimal.ZERO;
        BigDecimal grossGain = null;
        BigDecimal costBase = BigDecimal.ZERO;
        BigDecimal indexedCostBase = BigDecimal.ZERO;
        BigDecimal reducedCostBase = BigDecimal.ZERO;
        BigDecimal costBaseGain = null;

        for (CgtSecurity asset : securities) {
            quantity += asset.getQuantity();
            amount = amount.add(getSafeValue(asset.getAmount()));
            taxAmount = taxAmount.add(getSafeValue(asset.getTaxAmount()));
            if (asset.getGrossGain() != null) {
                if (grossGain != null) {
                    grossGain = grossGain.add(asset.getGrossGain());
                } else {
                    grossGain = asset.getGrossGain();
                }
            }

            costBase = costBase.add(getSafeValue(asset.getCostBase()));
            indexedCostBase = indexedCostBase.add(getSafeValue(asset.getIndexedCostBase()));
            reducedCostBase = reducedCostBase.add(getSafeValue(asset.getReducedCostBase()));
            if (asset.getCostBaseGain() != null) {
				if (costBaseGain != null) {
					costBaseGain = costBaseGain.add(asset.getCostBaseGain());
				} else {
					costBaseGain = asset.getCostBaseGain();
				}
            }
        }

        CgtGroupDto groupDto = new CgtGroupDto(groupId, groupCode, groupName, groupType, amount, quantity, taxAmount, grossGain,
                costBase, indexedCostBase, reducedCostBase, securities, costBaseGain);
        return groupDto;

    }

    private BigDecimal getSafeValue(BigDecimal value) {
        if (value != null) {
            return value;
        }
        return BigDecimal.ZERO;
    }

    protected List<CgtSecurity> getCgtSecurities(WrapCgtData cgtData, String cgtDataType) {
        if (cgtData == null) {
            return new ArrayList<>();
        }
        List<InvestmentCgt> cgtDataList = getCgtDataList(cgtData, cgtDataType);

        List<CgtSecurity> securities = new ArrayList<>();

        if (!(cgtDataList.isEmpty())) {
            for (InvestmentCgt investmentCgt : cgtDataList) {
                if (investmentCgt instanceof ManagedPortfolioCgtImpl) {
                    ManagedPortfolioCgtImpl mCgt = (ManagedPortfolioCgtImpl) investmentCgt;
                    Asset mpHolding = mCgt.getInvestment();
                    for (InvestmentCgt hCgt : mCgt.getInvestmentCgtList()) {
                        securities.addAll(getCgtData(mpHolding, hCgt, cgtDataType));
                    }
                } else {
                    securities.addAll(getCgtData(null, investmentCgt, cgtDataType));
                }
            }
        }

        return securities;
    }

    private List<InvestmentCgt> getCgtDataList(WrapCgtData cgtData, String cgtDataType) {
        List<InvestmentCgt> cgtDataList = new ArrayList<>();
        if (cgtDataType.equals(UNREALISED_CGT_DATA) && (cgtData.getUnrealisedCgtData() != null)) {
            cgtDataList = cgtData.getUnrealisedCgtData();
        } else if (cgtDataType.equals(REALISED_CGT_DATA) && (cgtData.getCgtData() != null)) {
            cgtDataList = cgtData.getCgtData();
        }
        return cgtDataList;
    }

    protected List<CgtSecurity> getCgtData(Asset parentInvestment, InvestmentCgt investmentCgt, String cgtDataType) {
        String securityCode = null;
        String securityName = null;
        String securityType = null;
        Asset security = investmentCgt.getInvestment();
        List<CgtSecurity> securities = new ArrayList<>();

        if (security != null) {
            securityCode = security.getAssetCode();
            securityName = security.getAssetName();
            securityType = security.getAssetType().name();
        }

        if (parentInvestment != null) {
            buildCgtMpSecurity(investmentCgt, securityCode, securityName, securityType, parentInvestment, securities, cgtDataType);
        } else {
            buildCgtSecurity(investmentCgt, securityCode, securityName, securityType, securities, cgtDataType);

        }

        return securities;
    }

    private void buildCgtSecurity(InvestmentCgt investmentCgt, String securityCode, String securityName, String securityType,
            List<CgtSecurity> securities, String cgtDataType) {
        for (CgtBaseData cgtBaseData : investmentCgt.getCgtData()) {
            CgtSecurity securityDto = new CgtSecurityDto(securityCode, securityName, securityType);
            securityDto.setAmount(cgtBaseData.getNetProceed());
            securityDto.setQuantity(Integer.valueOf(cgtBaseData.getQuantity().intValue()));
            if (cgtDataType.equals(UNREALISED_CGT_DATA)) {
                securityDto.setDate(cgtBaseData.getPriceDate());
                securityDto.setDaysHeld(getDaysHeld(cgtBaseData.getHoldingPeriod()));
            } else {
                securityDto.setDate(cgtBaseData.getSellDate());
            }

            securityDto.setTaxDate(cgtBaseData.getTaxDate());
            securityDto.setTaxAmount(cgtBaseData.getTaxCost());
            securityDto.setCostBase(cgtBaseData.getTaxCostBase());
            securityDto.setGrossGain(cgtBaseData.getTaxGain());
            securityDto.setIndexedCostBase(cgtBaseData.getTaxIndexedCostBase());
            securityDto.setReducedCostBase(cgtBaseData.getTaxReducedCostBase());
            securityDto.setCostBaseGain(cgtBaseData.getCostBaseGain());
            securities.add(securityDto);
        }
    }

    private void buildCgtMpSecurity(InvestmentCgt investmentCgt, String securityCode, String securityName, String securityType,
            Asset parentInvestment, List<CgtSecurity> securities, String cgtDataType) {
        for (CgtBaseData cgtBaseData : investmentCgt.getCgtData()) {
            CgtSecurity mpSecurityDto = new CgtMpSecurityDto(securityCode, securityName, securityType,
                    parentInvestment.getAssetId(), parentInvestment.getAssetCode(), parentInvestment.getAssetName(),
                    parentInvestment.getAssetType().name());
            mpSecurityDto.setAmount(cgtBaseData.getNetProceed());
            mpSecurityDto.setQuantity(Integer.valueOf(cgtBaseData.getQuantity().intValue()));
            if (cgtDataType.equals(UNREALISED_CGT_DATA)) {
                mpSecurityDto.setDate(cgtBaseData.getPriceDate());
                mpSecurityDto.setDaysHeld(getDaysHeld(cgtBaseData.getHoldingPeriod()));
            } else {
                mpSecurityDto.setDate(cgtBaseData.getSellDate());
            }
            mpSecurityDto.setTaxDate(cgtBaseData.getTaxDate());
            mpSecurityDto.setTaxAmount(cgtBaseData.getTaxCost());
            mpSecurityDto.setCostBase(cgtBaseData.getTaxCostBase());
            mpSecurityDto.setGrossGain(cgtBaseData.getTaxGain());
            mpSecurityDto.setIndexedCostBase(cgtBaseData.getTaxIndexedCostBase());
            mpSecurityDto.setReducedCostBase(cgtBaseData.getTaxReducedCostBase());
            mpSecurityDto.setCostBaseGain(cgtBaseData.getCostBaseGain());
            securities.add(mpSecurityDto);
        }
    }

    private Integer getDaysHeld(BigDecimal holdingPeriod) {
        int daysHeld = Integer.valueOf(holdingPeriod.intValue());
        return daysHeld < 0 ? 0 : daysHeld;
    }

}
