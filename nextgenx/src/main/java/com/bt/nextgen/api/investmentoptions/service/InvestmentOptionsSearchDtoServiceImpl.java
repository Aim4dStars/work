package com.bt.nextgen.api.investmentoptions.service;

import com.bt.nextgen.api.investmentoptions.model.InvestmentOptionsDto;
import com.bt.nextgen.api.investmentoptions.util.InvestmentOptionsDtoServiceHelper;
import com.bt.nextgen.api.util.SearchUtil;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.ips.IpsIdentifierImpl;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementIntegrationService;
import com.bt.nextgen.service.integration.ips.InvestmentPolicyStatementInterface;
import com.bt.nextgen.service.integration.ips.IpsKey;
import com.bt.nextgen.service.integration.ips.IpsProductAssociationInterface;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.select;
import static ch.lambdaj.Lambda.selectFirst;
import static com.bt.nextgen.api.investmentoptions.controller.InvestmentOptionsApiController.PRODUCT_ID;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

@Service
public class InvestmentOptionsSearchDtoServiceImpl implements InvestmentOptionsSearchDtoService {
    @Autowired
    private InvestmentPolicyStatementIntegrationService ipsService;

    @Autowired
    private InvestmentOptionsDtoServiceHelper investmentOptionsHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(InvestmentOptionsSearchDtoServiceImpl.class);

    /**
     * Searches and filters the investment options available to the user
     *
     * @param criteriaList  - List of filter criteria to filter the investment options
     * @param serviceErrors - Object to capture service errors
     * @return - List of filtered investment options
     */
    @Override
    public List<InvestmentOptionsDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        final List<InvestmentOptionsDto> investmentOptions = new ArrayList<>();
        final ApiSearchCriteria productCriteria = selectFirst(criteriaList, having(on(ApiSearchCriteria.class).getProperty(), equalTo(PRODUCT_ID)));
        final List<ApiSearchCriteria> otherCriteria = select(criteriaList, having(on(ApiSearchCriteria.class).getProperty(), not(equalTo(PRODUCT_ID))));
        final Map<ProductKey, Product> modelProductsMap = investmentOptionsHelper.getModelProducts(productCriteria, serviceErrors);

        if (MapUtils.isNotEmpty(modelProductsMap)) {
            final Map<IpsKey, IpsProductAssociationInterface> ipsProductsMap = ipsService.getAsscociatedProductAndIpsIds(serviceErrors);
            investmentOptions.addAll(filterResults(toInvestmentOptions(ipsProductsMap, modelProductsMap, serviceErrors), otherCriteria));
        }
        return investmentOptions;
    }

    /**
     * Converts the response from Avaloq to Dto object
     *
     * @param ipsProductsMap   - Map of IPS and associated productList
     * @param modelProductsMap - Map of all the available model products, indexed by product key
     * @param serviceErrors    - Service error  @return List of investment options Dto objects
     */
    private List<InvestmentOptionsDto> toInvestmentOptions(Map<IpsKey, IpsProductAssociationInterface> ipsProductsMap,
                                                           Map<ProductKey, Product> modelProductsMap, ServiceErrors serviceErrors) {
        final List<InvestmentOptionsDto> ipsList = new ArrayList<>();
        if (MapUtils.isNotEmpty(ipsProductsMap)) {
            for (Map.Entry<IpsKey, IpsProductAssociationInterface> ips : ipsProductsMap.entrySet()) {
                for (ProductKey productKey : ips.getValue().getAsscociatedProductList()) {
                    if (modelProductsMap.containsKey(productKey)) {
                        final InvestmentPolicyStatementInterface ipsDetail = ipsService.getIPSDetail(new IpsIdentifierImpl(ips.getKey()), serviceErrors);
                        if (ipsDetail != null) {
                            InvestmentOptionsDto investmentOption = new InvestmentOptionsDto();
                            investmentOption.setName(modelProductsMap.get(productKey).getProductName());
                            investmentOption.setCode(ipsDetail.getApirCode());

                            investmentOption.setMinAmount(ipsDetail.getMinInitInvstAmt());
                            investmentOption.setAssetClass(investmentOptionsHelper.getStaticValue(CodeCategory.IPS_ASSET_CLASS,
                                    ipsDetail.getAssetClassId(), serviceErrors));
                            investmentOption.setStyle(investmentOptionsHelper.getStaticValue(CodeCategory.IPS_INVESTMENT_STYLE,
                                    ipsDetail.getInvestmentStyleId(), serviceErrors));
                            investmentOption.setAssetCode(ipsDetail.getCode());

                            ipsList.add(investmentOption);
                        }
                    }
                }
            }
        }
        return ipsList;
    }

    /**
     * Filters the investment options based on the filter criterion
     *
     * @param investmentOptions - List of all investment options
     * @param criteriaList      - List of the filter criterion
     * @return - List of the filtered investment options
     */
    private List<InvestmentOptionsDto> filterResults(List<InvestmentOptionsDto> investmentOptions, List<ApiSearchCriteria> criteriaList) {
        String value;
        boolean isFilter = false;

        for (Iterator<InvestmentOptionsDto> iterator = investmentOptions.iterator(); iterator.hasNext(); ) {
            final InvestmentOptionsDto investmentOption = iterator.next();
            for (ApiSearchCriteria criteria : criteriaList) {
                try {
                    value = BeanUtils.getProperty(investmentOption, criteria.getProperty());
                    switch (criteria.getOperation()) {
                        case EQUALS:
                            isFilter = !criteria.getValue().equalsIgnoreCase(value);
                            break;
                        case STARTS_WITH:
                            isFilter = !SearchUtil.matches(SearchUtil.getPattern(criteria.getValue()), value);
                            break;
                    }
                    if (isFilter) {
                        iterator.remove();
                        break;
                    }
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    //No property found, don't filter out
                    LOGGER.info("Property not found: {}", criteria.getProperty());
                    break;
                }
            }
        }
        LOGGER.info("Filtered investment options size: {}", investmentOptions.size());
        return investmentOptions;
    }
}