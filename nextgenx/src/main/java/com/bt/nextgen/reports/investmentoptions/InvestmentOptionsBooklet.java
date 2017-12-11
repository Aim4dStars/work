package com.bt.nextgen.reports.investmentoptions;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.compare.ArgumentComparator;
import com.bt.nextgen.api.asset.controller.AssetApiController;
import com.bt.nextgen.api.asset.model.AssetDto;
import com.bt.nextgen.api.asset.model.ManagedFundAssetDto;
import com.bt.nextgen.api.asset.model.ManagedPortfolioAssetDto;
import com.bt.nextgen.api.asset.model.ShareAssetDto;
import com.bt.nextgen.api.asset.model.TermDepositAssetDtoV2;
import com.bt.nextgen.api.asset.service.AvailableAssetDtoService;
import com.bt.nextgen.api.product.v1.model.ProductCategory;
import com.bt.nextgen.api.product.v1.model.ProductDto;
import com.bt.nextgen.api.product.v1.service.ProductDtoService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.OperationType;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria.SearchOperation;
import com.bt.nextgen.core.exception.MissingResourceException;
import com.bt.nextgen.core.reporting.BaseReportV2;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.util.LambdaMatcher;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.fees.FeesType;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.asset.AssetClass;
import com.bt.nextgen.service.integration.asset.AssetStatus;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsFee;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.btfin.panorama.service.integration.asset.AssetType;
import com.btfin.panorama.service.integration.broker.Broker;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.jasperreports.engine.Renderable;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.notNullValue;

/**
 * The Class InvestmentOptionsBooklet loads all of the data required to generation the InvestmentOptionsBooklet jasper report.
 */
@SuppressWarnings({"squid:S1172", "squid:S1200"})
@Report("investmentOptionsBooklet")
public class InvestmentOptionsBooklet extends BaseReportV2 {

    private static final Logger logger = LoggerFactory.getLogger(InvestmentOptionsBooklet.class);

    @Autowired
    private AvailableAssetDtoService availableAssetDtoService;

    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsService;

    @Autowired
    private BrokerIntegrationService brokerService;

    @Autowired
    private CmsService cmsService;

    @Autowired
    @Qualifier("ProductDtoServiceV1")
    private ProductDtoService productDtoService;

    @Autowired
    @Qualifier("jsonObjectMapper")
    private ObjectMapper jsonObjectMapper;

    @Autowired
    private StaticIntegrationService staticService;

    private final SimpleDateFormat dateGeneratedStringFormat = new SimpleDateFormat("dd MMMM yyyy");

    /* Content keys */
    private static final String DIVERSIFIED_MANAGED_TABLE_DATA_KEY = "IOB.diversifiedManagedTableData";
    private static final String SECTOR_SPECIFIC_MANAGED_TABLE_DATA_KEY = "IOB.sectorSpecificManagedTableData";
    private static final String SECTOR_SPECIFIC_DIRECT_TABLE_DATA_KEY = "IOB.sectorSpecificDirectTableData";
    private static final String COVER_LOGO_KEY = "IOB.coverLogo";
    private static final String COVER_IMAGE_KEY = "IOB.coverImage";
    private static final String TOC_BACKGROUND_KEY = "IOB.tocBackground";
    private static final String BACKPAGE_LOGO_KEY = "IOB.backpageLogo";
    private static final String ICON_RIGHT_CHEVRON_KEY = "iconRightChevron";
    private static final String ICON_PHONE_KEY = "iconPhone";
    private static final String ICON_ENVELOPE_KEY = "iconEnvelope";
    private static final String ICON_LETTER_KEY = "iconLetter";
    private static final String ICON_MOBILE_KEY = "iconMobile";
    private static final String ICON_POSTBOX_KEY = "iconPostbox";

    private static final String NOT_AVAILABLE = "N/A";
    private static final String NO_IHL_MESSAGE = "no holding limit applicable";

    private static final String COMPACT = "COMP";
    private static final String PROFESSIONAL_SUPER_PRODUCT_NAME = "BT Panorama Super";
    private static final String COMPACT_SUPER_FILE_NAME = "Panorama Super Investment Options Booklet - Compact Menu";
    private static final String PROFESSIONAL_SUPER_FILE_NAME = "Panorama Super Investment Options Booklet - Full Menu";

    private static final String INVESTMENT_MANAGER_TEXT_REGEX = " \\(Investment Manager Position\\)";
    private static final String WESTPAC_FINANCIAL_SERVICES_KEY = "IM.WFSL";

    private static final String PERCENTAGE_SIGN = "%";
    private static final Collection<String> ALLOWED_STATUS = Arrays.asList(AssetStatus.OPEN.getDisplayName(), AssetStatus.OPEN_RED.getDisplayName());

    /**
     * Gets the date generated string.
     *
     * @param params the params
     * @return the date generated string
     */
    @ReportBean("dateGeneratedString")
    public String getDateGeneratedString(Map<String, Object> params) {
        return dateGeneratedStringFormat.format(new Date());
    }

    /**
     * Gets the managed portfolios as InvestmentOptionsBookletAssetData.  This will also retrieve and set additional IPS info.
     *
     * @param params the params
     * @return the managed portfolios
     * @throws IOException
     */
    @ReportBean("managedPortfolios")
    public List<InvestmentOptionsBookletAssetData> getManagedPortfolios(Map<String, Object> params) throws IOException {
        @SuppressWarnings("unchecked")
        final List<ManagedPortfolioAssetDto> managedPortfolioAssets = (List<ManagedPortfolioAssetDto>) getAssets(
                AssetType.MANAGED_PORTFOLIO, ManagedPortfolioAssetDto.class, params);
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final Map<IpsKey, InvestmentPolicyStatementInterface> ipsDetailsMap = ipsService.getInvestmentPolicyStatements(serviceErrors);

        final List<InvestmentOptionsBookletAssetData> iobAssets = new ArrayList<>();
        Boolean hasWfslManager = Boolean.FALSE;
        for (ManagedPortfolioAssetDto asset : managedPortfolioAssets) {
            final InvestmentPolicyStatementInterface ips = ipsDetailsMap.get(IpsKey.valueOf(asset.getIpsId()));
            String investmentManagerName = "";
            String assetClass = "";
            String weightedIcr = NOT_AVAILABLE;
            BigDecimal feeMeasureValue = BigDecimal.ZERO;
            if (ips != null) {
                Code assetClassCode = staticService.loadCode(CodeCategory.IPS_ASSET_CLASS, ips.getAssetClassId(), serviceErrors);
                assetClass = assetClassCode == null ? "" : assetClassCode.getName();
                if (CollectionUtils.isNotEmpty(ips.getFeeList())) {
                    feeMeasureValue = getFeeMeasure(ips.getFeeList());
                }

                final Broker investmentManager = brokerService.getBroker(BrokerKey.valueOf(ips.getInvestmentManagerPersonId()), serviceErrors);
                investmentManagerName = getInvestmentManagerName(investmentManager);
                hasWfslManager = isWfslManager(assetClass, investmentManager);

                if (ips.getWeightedIcr() != null) {
                    weightedIcr = ips.getWeightedIcr().stripTrailingZeros().setScale(2, RoundingMode.HALF_EVEN).toString() + PERCENTAGE_SIGN;
                }
            }

            iobAssets.add(new InvestmentOptionsBookletAssetData(asset.getAssetCode(), asset.getAssetName(), feeMeasureValue.toString() + PERCENTAGE_SIGN,
                    weightedIcr, asset.getRiskMeasure(), assetClass, null, asset.getGroupClass(), null, null,
                    investmentManagerName, hasWfslManager, null, null, null));
        }
        return Lambda.sort(iobAssets, Lambda.on(InvestmentOptionsBookletAssetData.class).getAssetClass());
    }

    /**
     * Gets the fee measure form the fee list associated with the IPS
     *
     * @param feeList - List of fee components for IPS
     * @return - fee measure
     */
    private BigDecimal getFeeMeasure(List<IpsFee> feeList) {
        BigDecimal feeMeasure = BigDecimal.ZERO;
        for (IpsFee fee : feeList) {
            if (FeesType.INVESTMENT_MANAGEMENT_FEE.equals(fee.getMasterBookKind()) && CollectionUtils.isNotEmpty(fee.getTariffList())) {
                feeMeasure = feeMeasure.add(Lambda.sumFrom(fee.getTariffList()).getTariffFactor());
            }
        }
        // change tariff factor to percentage
        return feeMeasure.multiply(BigDecimal.valueOf(100)).stripTrailingZeros().setScale(2, RoundingMode.HALF_EVEN);
    }

    /**
     * Gets the managed funds as InvestmentOptionsBookletAssetData.
     *
     * @param params the params
     * @return the managed funds
     */
    @ReportBean("managedFunds")
    @SuppressWarnings("unchecked")
    public List<InvestmentOptionsBookletAssetData> getManagedFunds(Map<String, Object> params) {

        List<ManagedFundAssetDto> managedFundAssets = (List<ManagedFundAssetDto>) getAssets(AssetType.MANAGED_FUND,
                ManagedFundAssetDto.class, params);
        List<InvestmentOptionsBookletAssetData> iobAssets = new ArrayList<>();
        for (ManagedFundAssetDto asset : managedFundAssets) {
            final String feeMeasure = asset.getIndirectCostRatioPercent() == null || BigDecimal.ZERO.equals(asset.getIndirectCostRatioPercent()) ?
                    "-" : asset.getIndirectCostRatioPercent().stripTrailingZeros().setScale(2, RoundingMode.HALF_EVEN).toString() + PERCENTAGE_SIGN;
            iobAssets.add(new InvestmentOptionsBookletAssetData(asset.getAssetCode(), asset.getAssetName(),
                    feeMeasure, null, asset.getRiskMeasure(), asset.getAssetClass(), asset.getFundManager(), asset.getGroupClass(), null,
                    null, null, null, asset.getAssetSubClass(), null, null));
        }

        return Lambda.sort(iobAssets, Lambda.on(InvestmentOptionsBookletAssetData.class), new ArgumentComparator<>(
                Lambda.on(InvestmentOptionsBookletAssetData.class).getAssetClass()));
    }

    /**
     * Gets the listed securities as InvestmentOptionsBookletAssetData.
     * IHL is set to 100 when there is 0 buffer
     * Investment buffer comes as IHL + buffer value
     *
     * @param params the params
     * @return the listed securities
     */
    @ReportBean("listedSecurities")
    public List<InvestmentOptionsBookletAssetData> getListedSecurities(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<ShareAssetDto> listedSecurityAssets = (List<ShareAssetDto>) getAssets(AssetType.SHARE, ShareAssetDto.class, params);
        List<InvestmentOptionsBookletAssetData> iobAssets = new ArrayList<>();
        for (ShareAssetDto asset : listedSecurityAssets) {
            final BigDecimal buffer = asset.getInvestmentHoldingLimitBuffer() != null ? asset.getInvestmentHoldingLimitBuffer() : BigDecimal.ZERO;
            final BigDecimal holdingLimit = asset.getInvestmentHoldingLimit() != null ? asset.getInvestmentHoldingLimit() : BigDecimal.ZERO;

            iobAssets.add(new InvestmentOptionsBookletAssetData(asset.getAssetCode(), asset.getAssetName(), null, null,
                    asset.getRiskMeasure(), asset.getAssetClass(), null, asset.getGroupClass(),
                    BigDecimal.ZERO.equals(buffer) ? NO_IHL_MESSAGE : holdingLimit.toString(),
                    buffer.subtract(holdingLimit).toString(),
                    null, null, null, null, null));
        }
        return Lambda.sort(iobAssets, Lambda.on(InvestmentOptionsBookletAssetData.class).getGroupClass());
    }

    /**
     * Gets the term deposits as InvestmentOptionsBookletAssetData.
     *
     * @param params the params
     * @return the listed securities
     */
    @ReportBean("termDeposits")
    public List<InvestmentOptionsBookletAssetData> getTermDeposits(Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<TermDepositAssetDtoV2> termDepositAssets = (List<TermDepositAssetDtoV2>) getAssets(AssetType.TERM_DEPOSIT,
                TermDepositAssetDtoV2.class, params);
        List<InvestmentOptionsBookletAssetData> iobAssets = new ArrayList<>();
        for (TermDepositAssetDtoV2 asset : termDepositAssets) {
            iobAssets.add(new InvestmentOptionsBookletAssetData(asset.getAssetCode(), asset.getAssetName(), null, null, null,
                    null, null, null, null, null, null, null, null, asset.getTerm().toString(), StringUtils.capitalize(asset.getInterestPaymentFrequency())));
        }
        return Lambda.sort(iobAssets, Lambda.on(InvestmentOptionsBookletAssetData.class).getAssetName());
    }

    /**
     * Gets the assets by product, currently hard coded to use any super product found.
     *
     * @param assetType  the asset type to retrieve
     * @param assetClass the asset class to check against
     * @return the assets
     */
    @SuppressWarnings("rawtypes")
    private Collection getAssets(final AssetType assetType, final Class<? extends AssetDto> assetClass,
                                 Map<String, Object> params) {
        final List<ApiSearchCriteria> criteria = new ArrayList<>();
        criteria.add(new ApiSearchCriteria(Attribute.ASSET_TYPE, SearchOperation.EQUALS, assetType.name(), OperationType.STRING));
        criteria.add(new ApiSearchCriteria(AssetApiController.PRODUCT_ID, SearchOperation.EQUALS,
                getSuperProduct(params).getKey().getProductId(), OperationType.STRING));
        final ServiceErrors serviceErrors = new FailFastErrorsImpl();
        final List<AssetDto> assetList = availableAssetDtoService.getFilteredValue(null, criteria, serviceErrors);
        return Lambda.filter(new LambdaMatcher<AssetDto>() {
            @Override
            protected boolean matchesSafely(AssetDto assetDto) {
                final Boolean hasValidStatus =  assetDto.getStatus() == null || ALLOWED_STATUS.contains(assetDto.getStatus());
                return assetDto.getClass().isAssignableFrom(assetClass) && hasValidStatus;
            }
        }, assetList);

    }

    /**
     * Gets the product short name to be used in the print when expressions.
     *
     * @param params the params of the report
     * @return the product short name for the report.
     */
    @ReportBean("productShortName")
    public String getProductShortName(Map<String, Object> params) {
        return getSuperProduct(params).getShortName();
    }

    /**
     * Returns whether the product is Compact Super.
     *
     * @param params the params of the report
     * @return true if the product short is the same as the Compact Super short name.
     */
    @ReportBean("productCompactSuper")
    public Boolean isProductCompactSuper(Map<String, Object> params) {
        return StringUtils.containsIgnoreCase(getSuperProduct(params).getShortName(), COMPACT);
    }

    /**
     * Returns whether the product is Professional Super.
     *
     * @param params the params of the report
     * @return true if the product short is the same as the Professional Super short name.
     */
    @ReportBean("productProfessionalSuper")
    public Boolean isProductProfessionalSuper(Map<String, Object> params) {
        return !isProductCompactSuper(params);
    }

    /**
     * If params contains a parameter 'product' which should be the product short name then that will be used to look up the super
     * product otherwise it gets the super product by looking up the user's related dealer group products and selecting the first
     * WHITE LABEL SUPER product with the name "BT Panorama Super".
     *
     * @return the super product
     */
    private ProductDto getSuperProduct(Map<String, Object> params) {
        final String productId = (String) params.get("product");
        final List<ProductDto> products = productDtoService.findAll(new FailFastErrorsImpl());
        ProductDto superProduct;
        if (productId == null) {
            superProduct = Lambda.selectFirst(products,
                    having(on(ProductDto.class).getProductLevel(), equalTo(ProductLevel.WHITE_LABEL.name()))
                            .and(having(on(ProductDto.class).getProductCategory(), equalTo(ProductCategory.SUPER))
                                    .and(having(on(ProductDto.class).getProductName(), equalToIgnoringCase(PROFESSIONAL_SUPER_PRODUCT_NAME)))));
        } else {
            superProduct = Lambda.selectFirst(products, having(on(ProductDto.class).getShortName(), equalToIgnoringCase(productId)));
        }
        if (superProduct == null) {
            logger.error("Super product not found");
            throw new MissingResourceException();
        }
        return superProduct;
    }

    /**
     * Gets the investment manager name.
     *
     * @return the investment manager name
     */
    private String getInvestmentManagerName(Broker investmentManager) {
        String investmentManagerName = NOT_AVAILABLE;
        if (investmentManager != null && investmentManager.getPositionName() != null) {
            investmentManagerName = investmentManager.getPositionName().replaceAll(INVESTMENT_MANAGER_TEXT_REGEX, "");
        }
        return StringUtils.trim(investmentManagerName);
    }

    /**
     * Determines if the investment manager is "Westpac Financial Services Ltd".
     *
     * @param assetClass        - Asset class of the MP
     * @param investmentManager - the investment manager broker
     */
    private Boolean isWfslManager(String assetClass, Broker investmentManager) {
        // Diversified MPs managed by Westpac need a disclaimer indicator.
        return investmentManager != null && assetClass.contains(AssetClass.DIVERSIFIED.getDescription()) &&
                WESTPAC_FINANCIAL_SERVICES_KEY.equals(investmentManager.getExternalBrokerKey().getId());
    }

    /**
     * Gets the diversified managed investment options table data.
     *
     * @param params the params
     * @return the diversified managed investment options table data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @ReportBean("diversifiedManagedInvestmentOptionsTableData")
    public List<InvestmentOptionsBookletTableData> getDiversifiedManagedInvestmentOptionsTableData(Map<String, Object> params)
            throws IOException {
        return getInvestmentOptionsTableData(DIVERSIFIED_MANAGED_TABLE_DATA_KEY);
    }

    /**
     * Gets the sector specific managed investment options table data.
     *
     * @param params the params
     * @return the sector specific managed investment options table data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @ReportBean("sectorSpecificManagedInvestmentOptionsTableData")
    public List<InvestmentOptionsBookletTableData> getSectorSpecificManagedInvestmentOptionsTableData(Map<String, Object> params)
            throws IOException {
        return getInvestmentOptionsTableData(SECTOR_SPECIFIC_MANAGED_TABLE_DATA_KEY);
    }

    /**
     * Gets the sector specific direct investment options table data.
     *
     * @param params the params
     * @return the sector specific direct investment options table data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @ReportBean("sectorSpecificDirectInvestmentOptionsTableData")
    public List<InvestmentOptionsBookletTableData> getSectorSpecificDirectInvestmentOptionsTableData(Map<String, Object> params)
            throws IOException {
        return getInvestmentOptionsTableData(SECTOR_SPECIFIC_DIRECT_TABLE_DATA_KEY);
    }

    /**
     * Gets the investment options table data.
     *
     * @param contentKey the content key
     * @return the investment options table data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private List<InvestmentOptionsBookletTableData> getInvestmentOptionsTableData(String contentKey) throws IOException {
        return jsonObjectMapper.readValue(cmsService.getContent(contentKey), new TypeReference<List<InvestmentOptionsBookletTableData>>() {
        });
    }

    /**
     * Gets the cover logo.
     *
     * @param params the params
     * @return the cover logo
     */
    @ReportBean("coverLogo")
    public Renderable getCoverLogo(Map<String, Object> params) {
        return getVectorImage(cmsService.getContent(COVER_LOGO_KEY));
    }

    /**
     * Gets the cover image. This is product dependent.
     *
     * @param params the params
     * @return the cover image
     */
    @ReportBean("coverImage")
    public Renderable getCoverImage(Map<String, Object> params) {
        return getRasterImage(cmsService.getContent(COVER_IMAGE_KEY));
    }

    /**
     * Gets the toc background. This is product dependent.
     *
     * @param params the params
     * @return the toc background
     */
    @ReportBean("tocBackground")
    public Renderable getTocBackground(Map<String, Object> params) {
        return getRasterImage(cmsService.getContent(TOC_BACKGROUND_KEY));
    }

    /**
     * Gets the icon right chevron.
     *
     * @param params the params
     * @return the icon right chevron
     */
    @ReportBean("iconRightChevron")
    public Renderable getIconRightChevron(Map<String, Object> params) {
        return getVectorImage(cmsService.getContent(ICON_RIGHT_CHEVRON_KEY));
    }

    /**
     * Gets the icon phone.
     *
     * @param params the params
     * @return the icon phone
     */
    @ReportBean("iconPhone")
    public Renderable getIconPhone(Map<String, Object> params) {
        return getVectorImage(cmsService.getContent(ICON_PHONE_KEY));
    }

    /**
     * Gets the icon envelope.
     *
     * @param params the params
     * @return the icon envelope
     */
    @ReportBean("iconEnvelope")
    public Renderable getIconEnvelope(Map<String, Object> params) {
        return getVectorImage(cmsService.getContent(ICON_ENVELOPE_KEY));
    }

    /**
     * Gets the icon letter.
     *
     * @param params the params
     * @return the icon letter
     */
    @ReportBean("iconLetter")
    public Renderable getIconLetter(Map<String, Object> params) {
        return getVectorImage(cmsService.getContent(ICON_LETTER_KEY));
    }

    /**
     * Gets the icon postbox.
     *
     * @param params the params
     * @return the icon postbox
     */
    @ReportBean("iconPostbox")
    public Renderable getIconPostbox(Map<String, Object> params) {
        return getVectorImage(cmsService.getContent(ICON_POSTBOX_KEY));
    }

    /**
     * Gets the icon mobile.
     *
     * @param params the params
     * @return the icon mobile
     */
    @ReportBean("iconMobile")
    public Renderable getIconMobile(Map<String, Object> params) {
        return getVectorImage(cmsService.getContent(ICON_MOBILE_KEY));
    }

    /**
     * Gets the backpage logo.
     *
     * @param params the params
     * @return the backpage logo
     */
    @ReportBean("backpageLogo")
    public Renderable getBackpageLogo(Map<String, Object> params) {
        return getVectorImage(cmsService.getContent(BACKPAGE_LOGO_KEY));
    }

    /**
     * Gets the diversified managed investment options table data.
     *
     * @param params the params
     * @return the diversified managed investment options table data
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @ReportBean("hasWfslManager")
    public Boolean hasWfslManager(Map<String, Object> params) throws IOException {
        final List<InvestmentOptionsBookletAssetData> portfolios = getManagedPortfolios(params);
        return Lambda.exists(portfolios, having(on(InvestmentOptionsBookletAssetData.class).getHasWfslManager(), equalTo(true)));
    }

    /**
     * Gets the data beans for the current report
     *
     * @param params
     * @param dataCollections
     * @return
     */
    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        return Collections.singletonList(getSuperProduct(params));
    }

    /**
     * Gets the filename for the downloaded document
     *
     * @param data
     * @return
     */
    @Override
    public String getReportFileName(Collection<?> data) {
        final ProductDto superProduct = Lambda.selectFirst(data, notNullValue());
        return superProduct != null && StringUtils.containsIgnoreCase(superProduct.getShortName(), COMPACT) ?
                COMPACT_SUPER_FILE_NAME : PROFESSIONAL_SUPER_FILE_NAME;
    }
}
