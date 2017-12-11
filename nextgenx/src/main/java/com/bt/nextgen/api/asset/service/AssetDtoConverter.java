package com.bt.nextgen.api.asset.service;

import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.asset.AssetAllocation;
import com.bt.nextgen.service.avaloq.ips.IpsIdentifierImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.bt.nextgen.service.integration.asset.ManagedFundAsset;
import com.bt.nextgen.service.integration.asset.ShareAsset;
import com.bt.nextgen.service.integration.asset.TermDepositAsset;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsIdentifier;
import com.bt.nextgen.service.integration.ips.IpsKey;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1200" })
@Component
public class AssetDtoConverter {
    @Autowired
    private CmsService cmsService;
    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    private ServiceErrors serviceErrors;

    private static final Logger logger = LoggerFactory.getLogger(AssetDtoConverter.class);

    public AssetDto toAssetDto(Asset asset, TermDepositAssetDetail termDepositAssetDetail, Map<String, BigDecimal> assetAllocations) {
        String tdBrand = null;
        if (asset.getAssetType() == AssetType.TERM_DEPOSIT) {
            tdBrand = getBrandForIssuer(termDepositAssetDetail == null ? asset.getBrand() : termDepositAssetDetail.getIssuer());
        }
        return toAssetDto(asset, termDepositAssetDetail, tdBrand, false, assetAllocations);
    }

    public AssetDto toAssetDto(Asset asset, TermDepositAssetDetail termDepositAssetDetail) {
        return toAssetDto(asset, termDepositAssetDetail, null);
    }

    private AssetDto toAssetDto(Asset asset, TermDepositAssetDetail termDepositAssetDetail, String tdBrand,
                                boolean includeRateless, Map<String, BigDecimal> assetAllocations) {
        if (AssetType.MANAGED_PORTFOLIO.equals(asset.getAssetType())
                || AssetType.TAILORED_PORTFOLIO.equals(asset.getAssetType())) {
            ManagedPortfolioAssetDto managedPortfolioAssetDto = new ManagedPortfolioAssetDto(asset, Boolean.FALSE);
            if (asset.getIpsId() != null) {
                IpsIdentifier identifier = new IpsIdentifierImpl();
                identifier.setIpsKey(IpsKey.valueOf(asset.getIpsId()));
                InvestmentPolicyStatementInterface ipsDetail = ipsIntegrationService.getIPSDetail(identifier, serviceErrors);
                if (ipsDetail != null) {
                    managedPortfolioAssetDto = new ManagedPortfolioAssetDto(asset,
                            ipsDetail.getTaxAssetDomicile() != null ? ipsDetail.getTaxAssetDomicile() : Boolean.FALSE);
                }
            }
            return managedPortfolioAssetDto;
        } else if (AssetType.TERM_DEPOSIT.equals(asset.getAssetType())) {
            return toTermDepositAsset((TermDepositAsset) asset, termDepositAssetDetail, tdBrand, includeRateless);
        } else if (AssetType.SHARE.equals(asset.getAssetType()) || AssetType.OPTION.equals(asset.getAssetType())
                || AssetType.BOND.equals(asset.getAssetType())) {
            return new ShareAssetDto((ShareAsset) asset);
        } else if (AssetType.MANAGED_FUND.equals(asset.getAssetType())) {
            return new ManagedFundAssetDto((ManagedFundAsset) asset, assetAllocations);
        } else if (AssetType.INDEX.equals(asset.getAssetType())) {
            return new AssetDto(asset, asset.getAssetName(), asset.getAssetType().name());
        }

        return null;
    }

    private TermDepositAssetDto toTermDepositAsset(TermDepositAsset asset, TermDepositAssetDetail detail, String brand,
            boolean includeRateless) {
        if (includeRateless && detail == null) {
            List<InterestRateDto> interestBands = Collections.emptyList();
            return new TermDepositAssetDto(asset, brand, asset.getBrand(),
                    asset.getTerm() == null ? null : asset.getTerm().getMonths(), asset.getMaturityDate(), null, null, null,
                    interestBands, asset.getIntrRate());
        } else if (detail != null) {
            return new TermDepositAssetDto(asset, brand, detail.getIssuer(), detail.getTerm().getMonths(),
                    asset.getMaturityDate(), detail.getPaymentFrequency().getDisplayName(),
                    detail.getInterestRates().first().getLowerLimit(), detail.getInterestRates().last().getUpperLimit(),
                    getInterestBands(detail), asset.getIntrRate());
        } else {
            logger.warn("Filtering term deposit asset {} as there is no matching rates", asset.getAssetId());
            return null;
        }
    }

    private List<InterestRateDto> getInterestBands(TermDepositAssetDetail detail) {
        List<InterestRateDto> interestBands = new ArrayList<>();
        for (InterestRate interestRate : detail.getInterestRates()) {
            interestBands.add(new InterestRateDto(interestRate.getRateAsPercentage(), interestRate.getLowerLimit(),
                    interestRate.getUpperLimit()));
        }
        return interestBands;
    }

    private String getBrandForIssuer(String issuer) {
        String brand = cmsService.getContent(Constants.TD_BRAND_PREFIX + issuer);
        if (brand == null) {
            brand = issuer;
        }
        return brand;
    }

    public List<AssetDto> toAssetDto(List<Asset> assets, Map<String, TermDepositAssetDetail> termDepositAssetDetails) {
        return toAssetDto(assets, termDepositAssetDetails, null);
    }

    public List<AssetDto> toAssetDto(Collection<Asset> assets, Map<String, TermDepositAssetDetail> termDepositAssetDetails,
                                     Map<AssetKey, AssetAllocation> assetAllocationMap) {
        List<AssetDto> productDtos = new ArrayList<>();
        if (assets != null) {
            Map<String, String> tdBrandMap = getBrandMap(assets, termDepositAssetDetails);
            for (Asset asset : assets) {
                AssetDto assetDto = toAssetDto(asset,
                        termDepositAssetDetails != null ? termDepositAssetDetails.get(asset.getAssetId()) : null,
                        tdBrandMap.get(asset.getAssetId()), false, getAssetAllocations(asset, assetAllocationMap));
                if (assetDto != null) {
                    // only add handled asset types
                    productDtos.add(assetDto);
                }
            }

            Collections.sort(productDtos, new Comparator<AssetDto>() {
                @Override
                public int compare(AssetDto a1, AssetDto a2) {
                    try {
                        return a1.getAssetName().compareToIgnoreCase(a2.getAssetName());
                    } catch (Exception err) {
                        logger.warn("Problem with asset a1 {} or a2 {}", a1, a2, err);
                        if (a1 != null)
                            logger.warn("a1 assetName is {}", a1.getAssetName());
                        if (a2 != null)
                            logger.warn("a2 assetName is {}", a2.getAssetName());
                    }
                    return 0;
                }
            });
        }

        return productDtos;
    }

    public Map<String, AssetDto> toAssetDto(Map<String, Asset> assets,
            Map<String, TermDepositAssetDetail> termDepositAssetDetails, boolean includeRateless) {
        Map<String, AssetDto> productDtos = new HashMap<>();
        if (assets != null) {
            Map<String, String> tdBrandMap = getBrandMap(assets.values(), termDepositAssetDetails);
            for (String assetId : assets.keySet()) {
                productDtos.put(assetId, toAssetDto(assets.get(assetId), termDepositAssetDetails.get(assetId),
                        tdBrandMap.get(assetId), includeRateless, null));
            }
        }

        return productDtos;
    }

    public Map<String, AssetDto> toAssetDto(Map<String, Asset> assets,
            Map<String, TermDepositAssetDetail> termDepositAssetDetails) {
        return toAssetDto(assets, termDepositAssetDetails, false);
    }

    private Map<String, String> getBrandMap(Collection<Asset> assets,
            Map<String, TermDepositAssetDetail> termDepositAssetDetails) {
        // issuer to brand map
        Map<String, String> issuerMap = new HashMap<String, String>();
        // asset to brand map
        Map<String, String> brandMap = new HashMap<String, String>();

        // maintain searched strings separately to the map entries as some may genuinely be null and we dont want to search for
        // multiple null values;
        Set<String> searched = new HashSet<>();

        for (Asset asset : assets) {
            if (asset.getAssetType() == AssetType.TERM_DEPOSIT) {
                final TermDepositAssetDetail tdDetail = termDepositAssetDetails != null ? termDepositAssetDetails.get(asset.getAssetId()): null;
                final String issuer = tdDetail == null ? asset.getBrand() : tdDetail.getIssuer();
                String brand = null;
                if (!searched.contains(issuer)) {
                    brand = getBrandForIssuer(issuer);
                    if (brand != null) {
                        issuerMap.put(issuer, brand);
                    }
                    searched.add(issuer);
                } else {
                    brand = issuerMap.get(issuer);
                }

                brandMap.put(asset.getAssetId(), brand);
            }
        }
        return brandMap;
    }

    private Map<String, BigDecimal> getAssetAllocations(Asset asset, Map<AssetKey, AssetAllocation> assetAllocationMap) {
        if (MapUtils.isNotEmpty(assetAllocationMap)) {
            final AssetAllocation assetAllocation = assetAllocationMap.get(AssetKey.valueOf(asset.getAssetId()));
            if (assetAllocation != null) {
                return createAllocationMap(assetAllocation.getAllocations());
            }
        }
        return null;
    }

    private Map<String, BigDecimal> createAllocationMap(Map<AssetClass, BigDecimal> allocations) {
        final Map<String, BigDecimal> allocationMap = new HashMap<>();
        for (Map.Entry<AssetClass, BigDecimal> entry : allocations.entrySet()) {
            allocationMap.put(entry.getKey().getDescription(), entry.getValue());
        }
        return allocationMap;
    }
}
