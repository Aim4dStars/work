package com.bt.nextgen.api.smsf.service;

import com.btfin.panorama.core.security.encryption.EncodedString;
import com.btfin.panorama.core.util.StringUtil;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.service.ServiceError;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PositionIdentifierImpl;
import com.bt.nextgen.service.integration.PositionIdentifier;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.account.ContainerKey;
import com.bt.nextgen.service.integration.account.ContainerType;
import com.btfin.panorama.service.integration.account.SubAccount;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.externalasset.model.ExternalAsset;
import com.bt.nextgen.service.integration.accountingsoftware.model.SoftwareFeedStatus;
import com.bt.nextgen.service.integration.externalasset.service.ExternalAssetIntegrationService;
import com.bt.nextgen.service.integration.externalasset.model.OffPlatformExternalAssetImpl;
import com.bt.nextgen.service.integration.externalasset.model.OnPlatformExternalAssetImpl;
import com.bt.nextgen.api.smsf.constants.AssetClass;
import com.bt.nextgen.api.smsf.constants.AssetType;
import com.bt.nextgen.api.smsf.constants.PropertyType;
import com.bt.nextgen.api.smsf.model.AssetDto;
import com.bt.nextgen.api.smsf.model.ExternalAssetDto;
import com.bt.nextgen.api.smsf.model.ExternalAssetTrxnDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Smsf external assets - save/update service implementation
 */

@SuppressWarnings({"squid:S1068", "findbugs:URF_UNREAD_FIELD", "squid:S1200", "squid:ClassCyclomaticComplexity", "squid:MethodCyclomaticComplexity"})
@Service
public class SaveExternalAssetsServiceImpl implements SaveExternalAssetsService
{
    private static final Logger logger = LoggerFactory.getLogger(SaveExternalAssetsServiceImpl.class);

	private static final int ASSET_NAME_MAX_LENGTH = 200;

    @Autowired
    private ExternalAssetIntegrationService externalAssetIntegrationService;

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService accountService;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetIntegrationService;

    private void setAssetAttributes(ExternalAssetDto externalAssetDto, ExternalAsset externalAsset) {
        externalAsset.setValueDate(ApiFormatter.parseDate(externalAssetDto.getValueDate()));
        externalAsset.setMaturityDate(ApiFormatter.parseDate(externalAssetDto.getMaturityDate()));
        externalAsset.setPositionName(externalAssetDto.getPositionName());

        if (StringUtils.isNotBlank(externalAssetDto.getMarketValue())) {
            externalAsset.setMarketValue(new BigDecimal(externalAssetDto.getMarketValue()));
        }
    }

    /**
     * Convert external assets Dto to domain object
     * @param externalAssetTrxnDto
     * @return
     */
    private List<ExternalAsset> convertDtoToDomain(ExternalAssetTrxnDto externalAssetTrxnDto) {

        List<ExternalAsset> externalAssets = new ArrayList<>();

        for (AssetDto assetDto : externalAssetTrxnDto.getAssetDtos()) {
            ExternalAssetDto externalAssetDto = (ExternalAssetDto) assetDto;
            ExternalAsset externalAsset = null;


            if (StringUtils.isNotBlank(externalAssetDto.getQuantity()) && "0".equals(externalAssetDto.getQuantity())) { //Deleting existing asset
                externalAsset = new OffPlatformExternalAssetImpl();
                if (StringUtils.isNotBlank(externalAssetDto.getPositionId())) {
                    PositionIdentifier positionIdentifier = new PositionIdentifierImpl(externalAssetDto.getPositionId());
                    externalAsset.setPositionIdentifier(positionIdentifier);
                }
                externalAsset.setQuantity(new BigDecimal(externalAssetDto.getQuantity()));
            }else {

                if (StringUtils.isNotBlank(externalAssetDto.getAssetId())) { // IN panorama asset
                    externalAsset = new OnPlatformExternalAssetImpl();
                    ((OnPlatformExternalAssetImpl) externalAsset).setAssetKey(AssetKey.valueOf(externalAssetDto.getAssetId()));
                } else {
                    //OFF Panorama asset
                    externalAsset = new OffPlatformExternalAssetImpl();
                    if (StringUtils.isNotBlank(externalAssetDto.getPropertyType())) {
                        ((OffPlatformExternalAssetImpl) externalAsset).setPropertyType(PropertyType.getByCode(externalAssetDto.getPropertyType().toLowerCase()));
                    }
                    ((OffPlatformExternalAssetImpl) externalAsset).setPositionCode(externalAssetDto.getPositionCode());

                    externalAsset.setAssetType(AssetType.getByCode(assetDto.getAssetType())); //off panorama new asset, assettype is required by avaloq
                    externalAsset.setAssetClass(AssetClass.getByCode(assetDto.getAssetClassId())); //off panorama new asset, assetClass is required by avaloq
                    setAssetAttributes(externalAssetDto, externalAsset);
                }
                if (StringUtils.isNotBlank(externalAssetDto.getPositionId())) { //Updating existing asset
                    PositionIdentifier positionIdentifier = new PositionIdentifierImpl(externalAssetDto.getPositionId());
                    externalAsset.setPositionIdentifier(positionIdentifier);
                    externalAsset.setAssetType(null); //existing asset, assettype is not required by avaloq
                    externalAsset.setAssetClass(null); //existing asset, assetClass is not required by avaloq

                    if (StringUtils.isNotBlank(externalAssetDto.getAssetId())) {  //ON panorama existing asset, attributes required by avaloq
                        setAssetAttributes(externalAssetDto, externalAsset);
                    }
                }
                if (StringUtils.isNotBlank(externalAssetDto.getQuantity())) {
                    externalAsset.setQuantity(new BigDecimal(externalAssetDto.getQuantity()));
                }
                externalAsset.setSource(externalAssetDto.getSource());
            }
            externalAssets.add(externalAsset);
        }

        return externalAssets;
    }

    /**
     * Validation - String null check validation
     * @param fieldname
     * @param input
     * @param errors
     */

    private void blankStringCheck(String fieldname, String input, List <ServiceError> errors) {

        if (!StringUtils.isNotBlank(input)) {
            ServiceErrorImpl serviceError = new ServiceErrorImpl();
            serviceError.setMessage("Invalid " + fieldname);
            serviceError.setId(fieldname);
            errors.add(serviceError);
        }
    }

    /**
     * Validation - String alphanumeric check validation
     * @param fieldname
     * @param input
     * @param errors
     */
    private void alphaNumericStringcheck(String fieldname, String input, List <ServiceError> errors) {

        if (!StringUtils.isAlphanumeric(input)) {
            ServiceErrorImpl serviceError = new ServiceErrorImpl();
            serviceError.setMessage("Only alpha numeric character's allowed " + fieldname);
            serviceError.setId(fieldname);
            errors.add(serviceError);
        }
    }


    private void isNumeric(String fieldName, String input, List <ServiceError> errors) {

        if (!StringUtils.isNumeric(input)) {
            ServiceErrorImpl serviceError = new ServiceErrorImpl();
            serviceError.setMessage("Only numeric character's allowed " + fieldName);
            serviceError.setId(fieldName);
            errors.add(serviceError);
        }
    }


    private boolean isBigDecimal( String fieldName, String value, List <ServiceError> errors) {
        boolean result = false;
        ServiceErrorImpl serviceError = new ServiceErrorImpl();

        String intPart = StringUtil.getBeforeDecmialValue(value);
        String decimalPart = StringUtil.getAfterDecmialValue(value);

        if (!StringUtils.isNumeric(intPart) || !StringUtils.isNumeric(decimalPart)) {
            serviceError.setMessage("Only numeric character's allowed " + fieldName);
            serviceError.setId(fieldName);
            errors.add(serviceError);
            result = false;
        }
        result = decimalPart.length() > 3 ? false : true;
        if (result) {
            if (!StringUtils.isNotBlank(serviceError.getMessage())) {
                serviceError.setMessage( serviceError.getMessage()+", decimal places greater than 3");

            } else {
                serviceError.setMessage("decimal places greater than 3");
            }
        }
        return result;
    }


    /**
     * Verify asset is on platform or off platform
     * Asset Id presents in the panorama
     * @param assetId
     * @param assetMap
     * @return boolean
     */
    private boolean isOnPlatform( String assetId, Map<String, Asset> assetMap) {
        boolean result = false;
        if (StringUtils.isNotBlank(assetId)) {
            Asset asset = assetMap.get(assetId);
            if (asset != null && asset.getAssetId().equals(assetId)) {
                result = true;
            }
        }
        return result;
    }

    private void assetCodeFormatCheck(String fieldname, String input, List <ServiceError> errors) {

        ServiceErrorImpl serviceError = new ServiceErrorImpl();
        if (StringUtils.isAlphanumeric(input)) {
            if (input.length()!=9 || !StringUtils.isAlpha(input.substring(0,3)+input.substring(7,9)) || !StringUtils.isNumeric(input.substring(3,7))) {
                serviceError.setMessage(" not in proper format XXXX111XX");
                serviceError.setId(fieldname);
                errors.add(serviceError);
            }
        }else{
            serviceError.setMessage("Only alpha numeric character's allowed " + fieldname);
            serviceError.setId(fieldname);
            errors.add(serviceError);
        }
    }

    private void checkPropertyType( String propertyType, List <ServiceError> errors)
    {
        ServiceErrorImpl serviceError = new ServiceErrorImpl();
        if (StringUtils.isBlank(propertyType))
        {
            serviceError.setMessage("propertyType required for direct property asset");
            errors.add(serviceError);
        }
    }

    /**
     * Validation - String length validation
     * @param fieldName
     * @param input
     * @param errors
     */
    private void checkLength(String fieldName, String input, List <ServiceError> errors)
    {
        if (StringUtils.isNotBlank(input) && input.trim().length() > ASSET_NAME_MAX_LENGTH)
        {
                ServiceErrorImpl serviceError = new ServiceErrorImpl();
                serviceError.setMessage(fieldName + " exceeds maximum length permitted");
                serviceError.setId(fieldName);
                errors.add(serviceError);
        }
    }

    private void validateAssetCode(AssetDto asset,  List <ServiceError> errors) {

        ExternalAssetDto externalAssetDto = (ExternalAssetDto) asset;

        //Managed Portfolio .. format 3-4-2 validationn for on and off both
        if (asset.getAssetType().equals(AssetType.MANAGED_FUND.getCode()) || asset.getAssetType().equals(AssetType.MANAGED_PORTFOLIO.getCode())) {
            assetCodeFormatCheck("assetCode", externalAssetDto.getPositionCode(), errors);
        }

        //assetcode - listed security 6 - alphanumeric , international 12 - alphanumeric // if not CASH, TD and DP validate assetCode
        if (!(asset.getAssetType().equals(AssetType.CASH.getCode())) && !(asset.getAssetType().equals(AssetType.TERM_DEPOSIT.getCode()))
                && !(asset.getAssetType().equals(AssetType.DIRECT_PROPERTY.getCode()))) {

            if (((asset.getAssetType().equals(AssetType.INTERNATIONAL_LISTED_SECURITIES.getCode())) || asset.getAssetType().equals(AssetType.OTHER_ASSET.getCode()))
                    && StringUtils.isEmpty(externalAssetDto.getPositionCode())){
                    return; //validation not required for empty positioncode, code is option in ILS and Other
            }
            alphaNumericStringcheck("assetCode", externalAssetDto.getPositionCode(), errors);
        }
    }

    /**
     * validate external assets for errors - length, decimal for qty, and existing asset id if any
     * @param externalAssetTrxnDto
     * @param serviceErrors
     * @return
     */
    private boolean validateExternalAssets(ExternalAssetTrxnDto externalAssetTrxnDto, ServiceErrors serviceErrors) {
        boolean result = false;

        List <ServiceError> errors = new ArrayList<>();

        blankStringCheck("accountId", externalAssetTrxnDto.getKey().getAccountId(), errors);
        //blankStringCheck("containerId", externalAssetTrxnDto.getContainer(), errors);

        final Map<String, Asset> assetMap = assetIntegrationService.loadExternalAssets(serviceErrors);

        for(AssetDto asset : externalAssetTrxnDto.getAssetDtos()){
            ExternalAssetDto externalAssetDto = (ExternalAssetDto) asset;
            boolean onPlatformAsset = false;

            if (externalAssetDto.getQuantity()!=null && "0".equals(externalAssetDto.getQuantity())) {
                blankStringCheck("positionId", externalAssetDto.getPositionId(), errors);

            } else {
                onPlatformAsset = isOnPlatform(asset.getAssetId(), assetMap);
                blankStringCheck("AssetType", externalAssetDto.getAssetType(), errors);

                if (!onPlatformAsset) {
                    //Adding new non panorama asset

                    blankStringCheck("assetClass", asset.getAssetClassId(), errors);
                    //TODO validate assetClass for various assetTypes?
                    validateAssetCode(asset, errors);
                    isBigDecimal("MarketValue", externalAssetDto.getMarketValue(), errors);
                    //checkValuationDate(externalAssetDto.getValueDate(), errors);
                    //Quantity optional in OTHER
                    if (asset.getAssetType().equals(AssetType.OTHER_ASSET.getCode()) &&
                            StringUtils.isNotBlank(externalAssetDto.getPositionCode()) ) {
                            alphaNumericStringcheck("assetCode", externalAssetDto.getPositionCode(), errors);
                    }
                    if (asset.getAssetType().equals(AssetType.DIRECT_PROPERTY.getCode())) {
                        checkPropertyType(externalAssetDto.getPropertyType(), errors);
                    }
                }
                validateAssetQuantity(externalAssetDto, asset.getAssetType(), errors);

                blankStringCheck("AssetName", externalAssetDto.getPositionName(), errors);
                checkLength("AssetName", externalAssetDto.getPositionName(), errors);
                checkLength("source", externalAssetDto.getSource(), errors);
            }
        }

        if (CollectionUtils.isNotEmpty(errors)) {
            serviceErrors.addErrors(errors);
            result = true;
        }
        return result;
    }

    private void validateAssetQuantity(ExternalAssetDto externalAssetDto, String assetType, List <ServiceError> errors) {

        if (assetType.equals(AssetType.OTHER_ASSET.getCode())){
                if (StringUtils.isNotBlank(externalAssetDto.getQuantity())) {
                    isBigDecimal("Quantity", externalAssetDto.getQuantity(), errors);
                }else {
                    externalAssetDto.setQuantity("1");
                }
        }
        //quantity doesn't exists direct property, cash, TD, MP
        if (assetType.equals(AssetType.DIRECT_PROPERTY.getCode()) || assetType.equals(AssetType.CASH.getCode()) ||
                assetType.equals(AssetType.MANAGED_PORTFOLIO.getCode()) || assetType.equals(AssetType.TERM_DEPOSIT.getCode())) {
            externalAssetDto.setQuantity("1");
        }
        //Quantity required LS, ILS, MF-- OTHERS -> optional
        if (assetType.equals(AssetType.AUSTRALIAN_LISTED_SECURITIES.getCode()) || assetType.equals(AssetType.INTERNATIONAL_LISTED_SECURITIES.getCode())) {
            isNumeric("Quantity", externalAssetDto.getQuantity(), errors);
        }
        if (assetType.equals(AssetType.MANAGED_FUND.getCode())) {
            //check quantity decimal
            isBigDecimal("Quantity", externalAssetDto.getQuantity(), errors);
        }
    }

    private SubAccount getExternalAssetsContainerId(WrapAccountDetail account, ServiceErrors serviceErrors) {
        String containerId = null;
        SubAccount extAssetSubAccount = null;

        for(SubAccount subAccount : account.getSubAccounts())
        {
            if (subAccount.getSubAccountType() != null && subAccount.getSubAccountType().equals(ContainerType.EXTERNAL_ASSET)){
                containerId = subAccount.getSubAccountKey().getId();
                extAssetSubAccount = subAccount;
            }
        }

        if (containerId==null || extAssetSubAccount == null){
            List <ServiceError> errors = new ArrayList<>();
            ServiceErrorImpl serviceError = new ServiceErrorImpl();
            serviceError.setMessage(account.getAccountNumber() +" doesn't contains external assets container");
            serviceError.setId(account.getAccountNumber());
            errors.add(serviceError);
            if (CollectionUtils.isNotEmpty(errors)) {
                serviceErrors.addErrors(errors);
            }
        }

        return extAssetSubAccount;
    }

    /**
     * Submit - save external assets by invoking save service call
     * @param externalAssetTrxnDto
     * @param serviceErrors
     * @return
     */
    @Override
    public ExternalAssetTrxnDto submit(ExternalAssetTrxnDto externalAssetTrxnDto, ServiceErrors serviceErrors) {

        TransactionStatus transactionStatus = null;
        AccountKey accountKey = AccountKey.valueOf(EncodedString.toPlainText(externalAssetTrxnDto.getKey().getAccountId()));
        ContainerKey containerKey = ContainerKey.valueOf(externalAssetTrxnDto.getContainer());


        if (!validateExternalAssets(externalAssetTrxnDto, serviceErrors))
        {
            List<ExternalAsset> externalAssets = convertDtoToDomain(externalAssetTrxnDto);

            WrapAccountDetail account = accountService.loadWrapAccountDetail(accountKey, serviceErrors);

            accountKey = AccountKey.valueOf(account.getAccountNumber());
            SubAccount extAssetSubAccount = getExternalAssetsContainerId(account, serviceErrors);
            containerKey = ContainerKey.valueOf(extAssetSubAccount.getSubAccountKey().getId());

            //DateTime bankDate = bankDateIntegrationService.getBankDate(serviceErrors);
            if (extAssetSubAccount.getExternalAssetsFeedState().equals(SoftwareFeedStatus.MANUAL.getValue())) {
                transactionStatus = externalAssetIntegrationService.saveOrUpdateExternalAssets(accountKey, containerKey, externalAssets, null);
            }
        }

        externalAssetTrxnDto.setAssetDtos(null);
        if (transactionStatus!=null) {
            externalAssetTrxnDto.setTransactionStatus(transactionStatus.getStatus());
        }

        return externalAssetTrxnDto;
    }



}
