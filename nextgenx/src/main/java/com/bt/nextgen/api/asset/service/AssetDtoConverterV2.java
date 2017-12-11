package com.bt.nextgen.api.asset.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.InterestRateDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.asset.CacheManagedTermDepositAssetRateIntegrationService;
import com.bt.nextgen.service.avaloq.asset.TermDepositInterestRateUtil;
import com.bt.nextgen.service.integration.termdeposit.TermDepositInterestRate;
import com.bt.nextgen.termdeposit.service.AssetIssuerKey;
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
import org.apache.commons.collections.CollectionUtils;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1200",
        "checkstyle:com.puppycrawl.tools.checkstyle.checks.coding.OverloadMethodsDeclarationOrderCheck" })
@Component
public class AssetDtoConverterV2 {
    @Autowired
    private CmsService cmsService;
    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsIntegrationService;

    private ServiceErrors serviceErrors;

    private static final Logger logger = LoggerFactory.getLogger(AssetDtoConverter.class);

    public AssetDto toAssetDto(Asset asset, SortedSet<TermDepositInterestRate> termDepositInterestRates, Map<String, BigDecimal> assetAllocations) {
        String tdBrand = null;
        if (asset.getAssetType() == AssetType.TERM_DEPOSIT) {
            tdBrand = getBrandForIssuer(CollectionUtils.isEmpty(termDepositInterestRates) ? asset.getBrand() : termDepositInterestRates.first().getIssuerId());
        }
        return toAssetDto(asset, termDepositInterestRates, tdBrand, false, assetAllocations);

    }

    public AssetDto toAssetDto(Asset asset,SortedSet<TermDepositInterestRate> termDepositInterestRates) {
        return toAssetDto(asset, termDepositInterestRates, null);
    }

    private AssetDto toAssetDto(Asset asset, SortedSet<TermDepositInterestRate> termDepositInterestRates, String tdBrand,
                                boolean includeRateless, Map<String, BigDecimal> assetAllocations) {
        AssetDto assetDto = null;
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
            assetDto =  managedPortfolioAssetDto;
        } else if (AssetType.TERM_DEPOSIT.equals(asset.getAssetType())) {
            assetDto = toTermDepositAsset((TermDepositAsset) asset, termDepositInterestRates, tdBrand, includeRateless);
        } else if (AssetType.SHARE.equals(asset.getAssetType()) || AssetType.OPTION.equals(asset.getAssetType())
                || AssetType.BOND.equals(asset.getAssetType())) {
            assetDto = new ShareAssetDto((ShareAsset) asset);
        } else if (AssetType.MANAGED_FUND.equals(asset.getAssetType())) {
            assetDto = new ManagedFundAssetDto((ManagedFundAsset) asset, assetAllocations);
        } else if (AssetType.INDEX.equals(asset.getAssetType())) {
            assetDto =  new AssetDto(asset, asset.getAssetName(), asset.getAssetType().name());
        }

        return assetDto;
    }

    private TermDepositAssetDtoV2 toTermDepositAsset(TermDepositAsset asset, SortedSet<TermDepositInterestRate> details, String brand,
                                                   boolean includeRateless) {
        if (includeRateless && details == null) {
            List<InterestRateDto> interestBands = Collections.emptyList();
            return new TermDepositAssetDtoV2(asset, brand, asset.getBrand(),
                    asset.getTerm() == null ? null : asset.getTerm().getMonths(), asset.getMaturityDate(), null, null, null,
                    interestBands, asset.getIntrRate());
        } else if (details != null) {

            return new TermDepositAssetDtoV2(asset, brand, details.first().getIssuerId(), details.first().getTerm().getMonths(),
                    asset.getMaturityDate(), details.first().getPaymentFrequency().getDisplayName(),
                    details.first().getLowerLimit(), details.last().getUpperLimit(),
                    getInterestBands(details), asset.getIntrRate());
        } else {
            logger.warn("Filtering term deposit asset {} as there is no matching rates", asset.getAssetId());
            return null;
        }
    }

    private List<InterestRateDto> getInterestBands(SortedSet<TermDepositInterestRate> details) {
        List<InterestRateDto> interestBands = new ArrayList<>();
        for (TermDepositInterestRate interestRate : details) {
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

    public List<AssetDto> toAssetDto(List<Asset> assets, List<TermDepositInterestRate> termDepositAssetDetails) {
        return toAssetDto(assets, termDepositAssetDetails, null);
    }

    public List<AssetDto> toAssetDto(Collection<Asset> assets, List<TermDepositInterestRate> termDepositAssetDetails,
                                     Map<AssetKey, AssetAllocation> assetAllocationMap) {
        List<AssetDto> productDtos = new ArrayList<>();
        Map<String,SortedSet<TermDepositInterestRate>> termDeposits = new HashMap<>();
        Map<String,String> assetIssuerMap = new HashMap<>();

        if(CollectionUtils.isNotEmpty(termDepositAssetDetails)){
            termDeposits = TermDepositInterestRateUtil.getTermDepositRatesByAssetId(termDepositAssetDetails);
            if(!termDeposits.isEmpty()){
                Iterator<Map.Entry<String,SortedSet<TermDepositInterestRate>>> entry = termDeposits.entrySet().iterator();
                while(entry.hasNext()){
                    Map.Entry<String,SortedSet<TermDepositInterestRate>> listEntry = entry.next();
                    String assetId = listEntry.getKey();
                    SortedSet<TermDepositInterestRate> termDepositInterestRates = listEntry.getValue();
                    assetIssuerMap.put(assetId,termDepositInterestRates.first().getIssuerId());
                }

            }
        }

        if (assets != null) {
            Map<String, String> tdBrandMap = getBrandMap(assets, assetIssuerMap);
            for (Asset asset : assets) {
                AssetDto assetDto = toAssetDto(asset,
                        termDeposits.get(asset.getAssetId()) != null ? termDeposits.get(asset.getAssetId()) : null,
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
                                            List<TermDepositInterestRate> termDepositAssetDetails, boolean includeRateless) {
        Map<String, AssetDto> productDtos = new HashMap<>();
        Map<String,String> assetIssuerMap = new HashMap<>();
        Map<String,SortedSet<TermDepositInterestRate>> termDeposits = new HashMap<>();

        if(CollectionUtils.isNotEmpty(termDepositAssetDetails)){
            termDeposits = TermDepositInterestRateUtil.getTermDepositRatesByAssetId(termDepositAssetDetails);
            if(!termDeposits.isEmpty()){
                Iterator<Map.Entry<String,SortedSet<TermDepositInterestRate>>> entry = termDeposits.entrySet().iterator();
                while(entry.hasNext()){
                    Map.Entry<String,SortedSet<TermDepositInterestRate>> listEntry = entry.next();
                    String assetId = listEntry.getKey();
                    SortedSet<TermDepositInterestRate> termDepositInterestRates = listEntry.getValue();
                    assetIssuerMap.put(assetId,termDepositInterestRates.first().getIssuerId());
                }

            }
        }
        if (assets != null) {
            Map<String, String> tdBrandMap = getBrandMap(assets.values(), assetIssuerMap);
            for (String assetId : assets.keySet()) {
                productDtos.put(assetId, toAssetDto(assets.get(assetId), termDeposits.get(assetId),
                        tdBrandMap.get(assetId), includeRateless, null));
            }
        }

        return productDtos;
    }

    public Map<String, AssetDto> toAssetDto(Map<String, Asset> assets,
                  List<TermDepositInterestRate> termDepositInterestRates) {
        return toAssetDto(assets, termDepositInterestRates, false);
    }

    private Map<String, String> getBrandMap(Collection<Asset> assets,
                                            Map<String,String> assetIssuerKeys) {
        // issuer to brand map
        Map<String, String> issuerMap = new HashMap<String, String>();
        // asset to brand map
        Map<String, String> brandMap = new HashMap<String, String>();

        // maintain searched strings separately to the map entries as some may genuinely be null and we dont want to search for
        // multiple null values;
        Set<String> searched = new HashSet<>();

        for (final Asset asset : assets) {
            if (asset.getAssetType() == AssetType.TERM_DEPOSIT) {
                final String issuer = (assetIssuerKeys == null || assetIssuerKeys.isEmpty())? asset.getBrand() : assetIssuerKeys.get(asset.getAssetId());
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

        return null;
    }
}
