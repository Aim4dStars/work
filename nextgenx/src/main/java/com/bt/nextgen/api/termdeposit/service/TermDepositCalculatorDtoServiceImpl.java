package com.bt.nextgen.api.termdeposit.service;

import ch.lambdaj.Lambda;
import com.bt.nextgen.api.termdeposit.model.Badge;
import com.bt.nextgen.api.termdeposit.model.Brand;
import com.bt.nextgen.api.termdeposit.model.TermDepositAssetRate;
import com.bt.nextgen.api.termdeposit.model.TermDepositAssetRateImpl;
import com.bt.nextgen.api.termdeposit.model.TermDepositBankRates;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorAccountKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDealerKey;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorDto;
import com.bt.nextgen.api.termdeposit.model.TermDepositCalculatorKey;
import com.bt.nextgen.api.termdeposit.model.TermRate;
import com.bt.nextgen.api.termdeposit.util.TermDepositAssetRateUtil;
import com.bt.nextgen.api.util.TermDepositUtil;
import com.bt.nextgen.cms.service.CmsService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.core.type.ConsistentEncodedString;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AvaloqUtils;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.broker.BrokerHelperService;
import com.bt.nextgen.service.avaloq.product.ProductLevel;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.PaymentFrequency;
import com.bt.nextgen.service.integration.asset.Term;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail;
import com.bt.nextgen.service.integration.asset.TermDepositAssetDetail.InterestRate;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.btfin.panorama.service.integration.broker.Broker;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductIntegrationService;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.termdeposit.web.model.TermDepositRateModel;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.apache.commons.collections.CollectionUtils;
import org.hamcrest.Matchers;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.bt.nextgen.core.web.Format.asCurrency;

@Service
public class TermDepositCalculatorDtoServiceImpl implements TermDepositCalculatorDtoService {
    private static final Logger logger = LoggerFactory.getLogger(TermDepositCalculatorDtoServiceImpl.class);

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;



    @Autowired
    private CmsService cmsService;

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private ProductIntegrationService productIntegrationService;

    @Autowired
    private  TermDepositCalculatorUtils termDepositCalculatorUtils;


    @Autowired
    private BrokerHelperService brokerHelperService;

    private static final BigDecimal BIGDECIMAL_DEFAULT = new BigDecimal("0.0");
    private static final int YEAR = 12;
    private static final String DIRECT = "direct";

    /**
     * Method to get the whole calculator on the basis of TermDepositCalculatorKey(badge, amount, dealer-group) passed along with
     * the list of badge/products.
     * 
     * @param key
     *            : TermDepositCalculatorKey(badge, amount, dealer-group)
     * @param serviceErrors
     * @return
     */
    @Override
    public TermDepositCalculatorDto find(final TermDepositCalculatorKey key, final ServiceErrors serviceErrors) {
        logger.info("TermDepositCalculatorDtoServiceImpl.find() for key: {}", key);
        TermDepositCalculatorDto termDepositCalculatorDto = new TermDepositCalculatorDto();

        final WrapAccount account = termDepositCalculatorUtils.getAccount(key, serviceErrors);
        final BrokerKey brokerKey = termDepositCalculatorUtils.getBrokerKey(key, account, serviceErrors);

        // Get the badge/products for a particular dealer group (ADVISOR_PRODUCTS("BTFG$UI_APL_LIST.ALL#PROD") & PRODUCTS("BTFG$UI_PROD_LIST.ALL#PROD_DET")).
        final List<Product> products = termDepositCalculatorUtils.getProducts(key, brokerKey, account, serviceErrors);
        //

        try {
            logger.info("TermDepositCalculatorDtoServiceImpl.find() Number of products found: {} for dealer-group: {}",
                    products.size(), brokerKey);
            final Map<ProductKey, List<Asset>> productAssets = new LinkedHashMap<>(); // Map to hold the badge/product as key and list of assets as values.
            final List<Asset> allProductAssets = new ArrayList<>();

            final Set<Badge> badges = new TreeSet<>(); // keep all badges for the dealer group.

            // Method to populate the product-assets & badges.
            final Badge selectedBadge = populateProductsAndAssets(products, productAssets, allProductAssets, badges, key,
                    brokerKey, serviceErrors);


            final ProductKey productKey = ProductKey.valueOf(ConsistentEncodedString.toPlainText(selectedBadge.getProductId()));
            logger.info("TermDepositCalculatorDtoServiceImpl.find() badgeName: {}, productKey:{}", selectedBadge.getName(),
                    productKey.getId());

            final DateTime bankDate = termDepositCalculatorUtils.getBankDate();

            // Load all the rates from TD_RATES("BTFG$UI_FIDD_RATE_LIST.ALL") for the dealer group supplied.
            //assetRates has <assetID, assetDetail>
            final Map<String, TermDepositAssetDetail> assetRates = assetService
                    .loadTermDepositRates(BrokerKey.valueOf(brokerKey.getId()), bankDate, allProductAssets, serviceErrors);
            logger.info("TermDepositCalculatorDtoServiceImpl.find() Total number of assets loaded:{}", assetRates.size());

            final Map<String, TermDepositAssetDetail> productAssetRates = new LinkedHashMap<>(); // Map containing assetID as the key and value as asset details including rates(TD_RATES("BTFG$UI_FIDD_RATE_LIST.ALL")).
            final List<Brand> brands = new ArrayList<>(); // List of brand

            // Populate the above mentioned map of assets & list of brands, as there is no separate service to get the brand.
            populateProductAssetRatesAndExtractBanks(productAssetRates, brands, productAssets, productKey, assetRates);

            // Filter the actual related rates for the supplied amount, i.e to fetch the relevant tier.
            final Map<String, TermDepositAssetRate> filteredAssetRates = filterTermDepositRates(productAssetRates,
                    new BigDecimal(key.getAmount()));

            // Creating the calculator model, i.e the 3-D matrix to contain the rated on the basis of brands & terms.
            termDepositCalculatorDto = toTermDepositCalculatorDto(brands, filteredAssetRates, new BigDecimal(key.getAmount()));

            // Set the badge detail for the UI.
            List<Badge> badgeList = new ArrayList<>(badges);
            Collections.sort(badgeList);
            termDepositCalculatorDto.setBadges(badgeList);
            termDepositCalculatorDto.setBadge(selectedBadge);
            logger.info("TermDepositCalculatorDtoServiceImpl.find() badgeName: {}, badgeId:{}", selectedBadge.getName(),
                    productKey.getId());
        } catch (final Exception ex) {
            logger.error("Error loading term deposit calculator {}", ex);
        }
        return termDepositCalculatorDto;
    }



    @SuppressWarnings("squid:S00107")
    /**
     * Method to fetch the assets on the basis of delaer-group & relevant products.
     * 
     * @param products
     * @param productAssets
     * @param allProductAssets
     * @param badges
     * @param selectedBadge
     * @param key
     * @param brokerKey
     * @param serviceErrors
     */
    protected Badge populateProductsAndAssets(final List<Product> products, final Map<ProductKey, List<Asset>> productAssets,
            final List<Asset> allProductAssets, final Set<Badge> badges, final TermDepositCalculatorKey key,
            final BrokerKey brokerKey, final ServiceErrors serviceErrors) {
        Badge selectedBadge = null;
        for (final Product product : products) // Iterate through each badge/product and fetch the assets linked to that badge/product.
        {
            // Get the actual child product which is direct & whose product-type is 'Model' from the internal reference hierarchy(parent-child) of PRODUCTS("BTFG$UI_PROD_LIST.ALL#PROD_DET")
            final Product directModelProduct = getDirectModelProduct(product, serviceErrors);
            if (directModelProduct != null) {
                logger.debug(
                        "TermDepositCalculatorDtoServiceImpl.find() Clild product which is direct & Model Product for {} is {}",
                        product.getProductKey().getId(), directModelProduct.getProductKey().getId());
                // Call to fetch the list of assets attached to a badge/product & dealer-group (AVAILABLE_ASSETS("BTFG$UI_AAL_LIST.ALL#ASSET")).
                final List<Asset> assets = assetService.loadAvailableAssets(brokerKey, directModelProduct.getProductKey(),
                        serviceErrors);
                logger.info(
                        "TermDepositCalculatorDtoServiceImpl.find() Number of assets found for DealerGroup:{} & Product:{} is:{}",
                        brokerKey, directModelProduct.getProductKey().getId(), assets.size());
                if (CollectionUtils.isNotEmpty(assets)) {
                    productAssets.put(product.getProductKey(), assets);
                    allProductAssets.addAll(assets);
                    Badge badge = new Badge(ConsistentEncodedString.fromPlainText(product.getProductKey().getId()).toString(),
                            product.getProductName());
                    badges.add(badge);
                    // To check that badge is supplied from UI or not.
                    if (key.getBadge() != null) {
                        if (key.getBadge().equals(product.getProductKey())) {
                            selectedBadge = badge;
                        }
                    } else {
                        if ((product.getProductName() != null
                                && "BT Panorama Investments".equals(product.getProductName())) || selectedBadge == null) {
                            // set the default to investment product. not nice but it's the best way for now.
                            selectedBadge = badge;
                        }
                    }
                }
            }
        }

        return selectedBadge;
    }

    /**
     * Method to get the actual product whis is direct & Model product type from the hierarchical Parent-child relation from
     * PRODUCTS("BTFG$UI_PROD_LIST.ALL#PROD_DET").
     * 
     * @param baseProduct
     * @param serviceErrors
     * @return
     */
    private Product getDirectModelProduct(final Product baseProduct, final ServiceErrors serviceErrors) {
        if (baseProduct.getProductLevel() == ProductLevel.MODEL && baseProduct.isDirect()) {
            return baseProduct;
        } else {
            final Map<ProductKey, Product> parentProducts = getParentKeyProducts(
                    productIntegrationService.loadProducts(serviceErrors));
            ProductKey productKey = baseProduct.getProductKey();
            while (productKey != null) {
                final Product product = productIntegrationService.getProductDetail(productKey, serviceErrors);
                if (product != null && product.getProductLevel().equals(Constants.MODEL_INTL_ID) && product.isDirect()) {
                    return product;
                }
                final Product childProduct = parentProducts.get(productKey);
                if (childProduct != null && childProduct.getProductLevel().toString().equals(Constants.MODEL_INTL_ID)
                        && childProduct.isDirect()) {
                    return childProduct;
                } else {
                    if (childProduct != null) {
                        productKey = childProduct.getProductKey();
                    } else {
                        logger.info("Not able to fetch the child product, returning null.");
                        productKey = null;
                        break;
                    }
                }
            }
        }
        logger.info("Not able to fetch the child product, returning null.");
        return null;
    }

    /**
     * Utility method to convert the list products in to a map having product parent ID(prod_parent_id) as key and product as
     * value. Saves the multiple iterstion to the whole list to retrieve the child of a product.
     * 
     * @param products
     * @return Map: key-parent product, value-product itself.
     */
    private Map<ProductKey, Product> getParentKeyProducts(final Collection<Product> products) {
        final Map<ProductKey, Product> parentKeyProducts = new HashMap<>();
        for (final Product product : products) {
            final ProductKey parentProductKey = product.getParentProductKey();
            if (parentProductKey != null && product.isDirect()) // ROOT products are not included & only direct product is
                                                                // included.
                parentKeyProducts.put(parentProductKey, product);
        }

        return parentKeyProducts;

    }

    /**
     * Method to select the appropriate term deposit asset on the basis of amount passed. Filters out the rate which are not in
     * the range for given amount.
     * 
     * @param productAssetRates
     * @param amount
     * @return
     */
    protected static Map<String, TermDepositAssetRate> filterTermDepositRates(
            final Map<String, TermDepositAssetDetail> productAssetRates, final BigDecimal amount) {
        logger.info("TermDepositCalculatorDtoServiceImpl.filterTermDepositRates(): For amount-{}", amount);
        logger.info("All Rates Size for this asset-{}", productAssetRates.size());
        final Map<String, TermDepositAssetRate> filteredAssets = new LinkedHashMap<>();
        logger.info("Assets List Size for this DG-{}", productAssetRates.size());
        for (final TermDepositAssetDetail termDepositAssetDetail : productAssetRates.values()) {
            final String key = termDepositAssetDetail.getAssetId();
            logger.debug("Asset Key-{}", key);
            if (termDepositAssetDetail != null) {
                final TreeSet<InterestRate> ratePool = termDepositAssetDetail.getInterestRates();
                final List<TermRate> tempRatePool = new ArrayList<>();
                for (Iterator<InterestRate> iterator = ratePool.iterator(); iterator.hasNext();) {
                    InterestRate termRate = iterator.next();
                    // if there's another tier above this one then reduce the upper limit by 1 so they don't overlap
                    BigDecimal upperLimit = termRate.getUpperLimit();
                    if (iterator.hasNext()) {
                        upperLimit = upperLimit.subtract(BigDecimal.valueOf(0.001));
                    }
                    if (rangeCheck(termRate.getLowerLimit(), upperLimit, amount)) {
                        logger.debug("Rate Value-{}", termRate.getRate());
                        final TermRate tempTermRate = new TermRate();

                        tempTermRate.setAssetIrcId(termRate.getIrcId());
                        tempTermRate.setRate(termRate.getRate());
                        tempTermRate.setMinInvest(termRate.getLowerLimit());
                        tempTermRate.setMaxInvest(termRate.getUpperLimit());
                        tempTermRate.setPriority(termRate.getPriority());

                        tempRatePool.add(tempTermRate);
                    }
                }

                final TermDepositAssetRate tempTermDepositAsset = new TermDepositAssetRateImpl();
                tempTermDepositAsset.setRatePool(tempRatePool);
                tempTermDepositAsset.setAssetId(termDepositAssetDetail.getAssetId());
                tempTermDepositAsset.setPaymentFrequency(termDepositAssetDetail.getPaymentFrequency().toString());
                tempTermDepositAsset.setBrand(new Brand(termDepositAssetDetail.getIssuer()));
                tempTermDepositAsset.setTerm(termDepositAssetDetail.getTerm());

                filteredAssets.put(key, tempTermDepositAsset);
            }
        }
        logger.info("{} Term Deposit assets found for amount-{}", filteredAssets.size(), amount);
        logger.info("Asset KeySet-{}", filteredAssets.keySet());

        return filteredAssets;
    }

    /**
     * Method to check whether the amount passed exist in the range for term deposit asset.
     * 
     * @param minInvest
     * @param maxInvest
     * @param amount
     * @return
     */
    public static boolean rangeCheck(final BigDecimal minInvest, final BigDecimal maxInvest, final BigDecimal amount) {
        final com.google.common.collect.Range<BigDecimal> range = com.google.common.collect.Range.closed(minInvest, maxInvest);
        return range.contains(amount);
    }

    /**
     * Method to parse the asset map created for the amount to invest and converting it to model-list for each brand, along with
     * the brand related data from CMS.
     * 
     * @param assetMap
     * @param amount
     * @param brands
     * @return Bank -> Terms -> if(term == 3M) -> Rate + Interest at Maturity, Total Interest Earned, Total Amount at Maturity
     *         if(term == 6M) -> Rate + Interest at Maturity, Total Interest Earned, Total Amount at Maturity if(term == 1Y) ->
     *         Rate + Monthly(Interest per Month, Total Interest Earned, Total Amount at Maturity) & At Maturity(Interest at
     *         Maturity, Total Interest Earned, Total Amount at Maturity) if(term == 3Y) -> Rate + Monthly(Interest per Month,
     *         Total Interest Earned, Total Amount at Maturity) & Yearly(Interest per Year, Total Interest Earned, Total Amount at
     *         Maturity) if(term == 5Y) -> Rate + Monthly(Interest per Month, Total Interest Earned, Total Amount at Maturity) &
     *         Yearly(Interest per Year, Total Interest Earned, Total Amount at Maturity)
     */
    private TermDepositCalculatorDto toTermDepositCalculatorDto(final List<Brand> brands,
            final Map<String, TermDepositAssetRate> assetMap, final BigDecimal amount) {
        logger.info("TermDepositCalculatorDtoServiceImpl.toTermDepositCalculatorDto(): Creating model for amount {}", amount);
        final TermDepositCalculatorDto termDepositCalculatorDto = new TermDepositCalculatorDto();
        final Map<Brand, TermDepositBankRates> banksMap = new TreeMap<>();
        TermDepositBankRates model = null;
        for (final Brand brand : brands) {
            logger.info("TermDepositCalculatorDtoServiceImpl.toTermDepositCalculatorDto(): Bank Id: {}", brand.getId());
            model = new TermDepositBankRates();
            banksMap.put(brand, model);
            // model.setBrandClass(cmsService.getContent(bank.getId() + "_class"));
            // model.setBrandName(cmsService.getContent(brand.getId() + "_name"));
            model.setBrandId(brand.getId());
            populateRatesForBrands(assetMap, brand, model, amount);
            model.setTermMap(new TreeMap<>(model.getTermMap()));// Sort
        }
        getBestRates(banksMap);
        termDepositCalculatorDto.setTermDepositBankRates(new ArrayList<>(banksMap.values()));
        return termDepositCalculatorDto;
    }

    private void populateRatesForBrands(final Map<String, TermDepositAssetRate> assetMap, final Brand brand,
            final TermDepositBankRates model, final BigDecimal amount) {
        for (final TermDepositAssetRate termDepositAsset : assetMap.values()) {
            if (brand.getId().equalsIgnoreCase(termDepositAsset.getBrand().getId())) {
                final TermDepositRateModel mRate = getRateModelForAsset(termDepositAsset, model, amount);
                model.getTermMap().put(termDepositAsset.getTerm(), mRate);
            }
        }
    }

    private TermDepositRateModel getRateModelForAsset(final TermDepositAssetRate termDepositAsset,
            final TermDepositBankRates model, final BigDecimal amount) {
        logger.info("Asset Id: {}, Asset Name: {}, Frequency: {}, Rate Pool: {}", termDepositAsset.getAssetId(),
                termDepositAsset.getAssetName(), termDepositAsset.getPaymentFrequency(), termDepositAsset.getRatePool().size());
        TermDepositRateModel mRate;
        final TermDepositRateModel termMap = model.getTermMap().get(termDepositAsset.getTerm());
        mRate = (termMap != null) ? model.getTermMap().get(termDepositAsset.getTerm()) : new TermDepositRateModel();

        final Term term = termDepositAsset.getTerm();
        final BigDecimal yearlyRate = termDepositAsset.getYearlyRate();

        if (yearlyRate.compareTo(BIGDECIMAL_DEFAULT) != 0) {
            commonTermValues(mRate, yearlyRate, amount, term);
        }
        if (term.getMonths() < YEAR) {
            monthlyTermValues(mRate, yearlyRate, amount, term, termDepositAsset.getAssetId());
        }
        if (term.getMonths() >= YEAR) {
            yearlyTermValues(mRate, amount, term, termDepositAsset);
        }
        return mRate;
    }

    private void commonTermValues(final TermDepositRateModel mRate, final BigDecimal yearlyRate, final BigDecimal amount,
            final Term term) {
        mRate.setInterestPerTerm(AvaloqUtils.asAvaloqRate(yearlyRate));
        mRate.setInterestRatePerYear(asCurrency(TermDepositAssetRateUtil.getTotalInterestEarned(yearlyRate, amount, 12)));
        mRate.setTotalInterestEarnedYearly(
                asCurrency(TermDepositAssetRateUtil.getTotalInterestEarned(yearlyRate, amount, term.getMonths())));
        mRate.setMaturityValueYearly(
                asCurrency(TermDepositAssetRateUtil.getTotalAmountAtMaturity(yearlyRate, amount, term.getMonths())));
    }

    private void monthlyTermValues(final TermDepositRateModel mRate, final BigDecimal yearlyRate, final BigDecimal amount,
            final Term term, final String assetId) {
        mRate.setMaturityId(assetId);
        mRate.setTotalInterestEarnedMonthly(
                asCurrency(TermDepositAssetRateUtil.getTotalInterestEarned(yearlyRate, amount, term.getMonths())));
        mRate.setMaturityValueMonthly(
                asCurrency(TermDepositAssetRateUtil.getTotalAmountAtMaturity(yearlyRate, amount, term.getMonths())));
    }

    private void yearlyTermValues(final TermDepositRateModel mRate, final BigDecimal amount, final Term term,
            final TermDepositAssetRate termDepositAsset) {
        final String assetId = termDepositAsset.getAssetId();
        final BigDecimal monthlyRate = termDepositAsset.getMonthlyRate();
        if (term.getMonths() == YEAR
                && termDepositAsset.getPaymentFrequency().equalsIgnoreCase(PaymentFrequency.AT_MATURITY.toString())) // For 1 year term, interest paid is at-maturity not yearly.
        {
            mRate.setMaturityId(assetId);
        } else if (termDepositAsset.getPaymentFrequency().equalsIgnoreCase(PaymentFrequency.MONTHLY.toString())) {
            mRate.setMonthlyId(assetId); // For 1, 3 & 5 years term, interest paid is monthly.
        } else {
            mRate.setYearlyId(assetId); // For 3 & 5 years term, interest paid is yearly.
        }

        // For Monthly
        // BigDecimal monthlyRate = termDepositAsset.getMonthlyRate();
        if (monthlyRate.compareTo(BIGDECIMAL_DEFAULT) != 0) {
            mRate.setTermDepositMonthlyIterest(AvaloqUtils.asAvaloqRate(monthlyRate));// rate : Monthly : Interest Rate per month
            mRate.setInterestRatePerMonth(asCurrency(TermDepositAssetRateUtil.getInterestPerMonth(monthlyRate, amount))); // amount : Monthly : Interest  per  month
            mRate.setTotalInterestEarnedMonthly(
                    asCurrency(TermDepositAssetRateUtil.getTotalInterestEarned(monthlyRate, amount, term.getMonths()))); // amount  : Monthly : Total interest earned
            mRate.setMaturityValueMonthly(
                    asCurrency(TermDepositAssetRateUtil.getTotalAmountAtMaturity(monthlyRate, amount, term.getMonths()))); // amount: Monthly: Total at maturity
        }
    }

    /**
     * Method to set the best rate for each term and the highest rate for the 6 months term.
     * 
     * @param banksMap
     */
    private static void getBestRates(final Map<Brand, TermDepositBankRates> banksMap) {
        final Map<Term, BigDecimal> bestTermMap = new LinkedHashMap<>();
        final Map<Term, BigDecimal> termMap = new LinkedHashMap<>();
        boolean firstIteration = true;
        // Iterating to find the best rates for each term.
        for (final TermDepositBankRates model : banksMap.values()) {
            if (firstIteration) {
                for (final Term term : model.getTermMap().keySet()) {
                    termMap.put(term,
                            new BigDecimal(AvaloqUtils.deformatRate(model.getTermMap().get(term).getInterestPerTerm())));
                    bestTermMap.putAll(termMap);
                }
                firstIteration = false;
            } else {
                for (final Term term : model.getTermMap().keySet()) {
                    bestTermMap.put(term,
                            compareRate(
                                    new BigDecimal(AvaloqUtils.deformatRate(model.getTermMap().get(term).getInterestPerTerm())),
                                    termMap.get(term)));
                }
                termMap.putAll(bestTermMap);
            }
        }
        // Iterating again to set the best rate in term flag & highest rate in 6-months term for each model.
        for (final TermDepositBankRates model : banksMap.values()) {
            for (final Term term : model.getTermMap().keySet()) {
                if (new BigDecimal(AvaloqUtils.deformatRate(model.getTermMap().get(term).getInterestPerTerm()))
                        .compareTo(bestTermMap.get(term)) == 0) {
                    logger.info("Best Rate for the term {} months = {}", term.getMonths(), bestTermMap.get(term));
                    model.getTermMap().get(term).setBestRateFlag(true);
                    if (term.getMonths() == 6) {
                        model.getTermMap().get(term).setHighestRateFlag(true);

                    }
                }
            }
        }
    }

    protected static BigDecimal compareRate(final BigDecimal bestRate, final BigDecimal currentRate) {
        if (currentRate != null && currentRate.compareTo(bestRate) > 0) {
            return currentRate;
        }
        return bestRate;
    }

    /**
     * Populate the map containing key as asset-ID & asset detail as values for the specific badge/product. Collect the
     * brands/banks/issuer from the list of fidd rate list response.
     * 
     * @param productAssetRates
     *            : Assets corresponding to a badge/product.
     * @param brands
     *            : brands/banks/issuer falls under badge/product & dealer group combo.
     * @param productAssets
     *            : Map containing product/badge & asset listed for them.
     * @param product
     *            : The badge/product for whom data is populated.
     * @param assetRates
     *            : All rates for each asset listed in TD_RATES("BTFG$UI_FIDD_RATE_LIST.ALL").
     */
    private void populateProductAssetRatesAndExtractBanks(final Map<String, TermDepositAssetDetail> productAssetRates,
            final List<Brand> brands, final Map<ProductKey, List<Asset>> productAssets, final ProductKey product,
            final Map<String, TermDepositAssetDetail> assetRates) {
        final List<Asset> assets = productAssets.get(product);
        if (assets != null)
            for (final Asset asset : assets) {
                final String key = asset.getAssetId();
                if (assetRates.get(key) != null)
                    brands.add(new Brand(assetRates.get(key).getIssuer()));

                final TermDepositAssetDetail termDepositAssetDetail = assetRates.get(key);
                if (termDepositAssetDetail != null) {
                    productAssetRates.put(key, termDepositAssetDetail);
                }
            }
    }

    @Override
    public String getTermDepositRatesAsCsv(final String brand, final String type, final String encodedProductId) {
        final String generatedDate = "#{generatedDate}";
        BrokerKey brokerKey = null;
        final ServiceErrors serviceErrors = new ServiceErrorsImpl();
        if (null != type && DIRECT.equalsIgnoreCase(type)) {
            final Collection<Broker> brokerList = brokerHelperService.getDealerGroupsforInvestor(serviceErrors);
            if (CollectionUtils.isNotEmpty(brokerList)) {
                for (final Broker brokers : brokerList) {
                    if (brokers.isDirectInvestment()) {
                        brokerKey = brokers.getDealerKey();
                        break;
                    }
                }
            }
        } else {
            brokerKey = userProfileService.getDealerGroupBroker().getDealerKey();
        }

        final String productId = decodeProductId(encodedProductId);

        final List<Product> products = getProductList(brokerKey, productId, serviceErrors);

        final List<Asset> assets = getAllAssetsOfProducts(brokerKey, products, serviceErrors);
        final Map<String, TermDepositAssetDetail> assetRates = assetService.loadTermDepositRates(brokerKey, termDepositCalculatorUtils.getBankDate(), assets,
                serviceErrors);
        String csv = TermDepositCalculatorCsvUtils.getTermDepositRatesCsv(brand, assetRates, cmsService);
        csv = csv.replace(generatedDate, TermDepositUtil.getCurrentDate());
        return csv;
    }

    /**
     * Determines the decoded productId.
     * 
     * The encodedProductId is provided to the client as either a consistently encoded string or a inconsistently encoded string.
     *
     * @param encodedProductId
     *            the encoded product id
     * @return the decoded productId or the original string if decoding fails.
     */
    private String decodeProductId(final String encodedProductId) {
        try {
            return ConsistentEncodedString.toPlainText(encodedProductId);
        } catch (final EncryptionOperationNotPossibleException e) {
            try {
                logger.debug("Encoded productId: {}, is not consitently encoded.", encodedProductId, e);
                return EncodedString.toPlainText(encodedProductId);
            } catch (final EncryptionOperationNotPossibleException e1) {
                logger.debug("Encoded productId: {}, is not encoded.", encodedProductId, e1);
                return encodedProductId;
            }
        }
    }

    /**
     * Returns the list of products for the provided broker, optionally filtered by the provided productId.
     *
     * @param brokerKey
     *            the dealer group key
     * @param productId
     *            the product to filter by (optional)
     * @param serviceErrors
     *            the service errors
     * @return the list of products
     */
    private List<Product> getProductList(final BrokerKey brokerKey, final String productId, final ServiceErrors serviceErrors) {
        final List<Product> products = productIntegrationService.getDealerGroupProductList(brokerKey, serviceErrors);
        if (productId != null) {
            final ProductKey productKey = ProductKey.valueOf(productId);
            return Lambda.select(products, Lambda.having(Lambda.on(Product.class).getProductKey(), Matchers.equalTo(productKey)));
        }
        return products;
    }

    private List<Asset> getAllAssetsOfProducts(final BrokerKey brokerKey, final List<Product> products,
            final ServiceErrors serviceErrors) {
        final List<Asset> allProductAssets = new ArrayList<>();
        for (final Product product : products) // Iterate through each badge/product and fetch the assets linked to that badge/product.
        {
            final Product directModelProduct = getDirectModelProduct(product, serviceErrors);
            if (directModelProduct != null) {
                logger.debug(
                        "TermDepositCalculatorDtoServiceImpl.find() Clild product which is direct & Model Product for {} is {}",
                        product.getProductKey().getId(), directModelProduct.getProductKey().getId());
                final List<Asset> assets = assetService.loadAvailableAssets(brokerKey, directModelProduct.getProductKey(),
                        serviceErrors);
                allProductAssets.addAll(assets);
            }
        }
        return allProductAssets;
    }

}
