package com.bt.nextgen.api.inspecietransfer.v2.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ch.lambdaj.Lambda;
import ch.lambdaj.function.convert.Converter;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.model.TransferAssetDtoImpl;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.transfer.InspecieAsset;
import com.bt.nextgen.service.integration.transfer.TransferItem;

/**
 * @deprecated Use V3
 */
@Deprecated
@Service("TransferAssetHelperV2")
@Transactional(value = "springJpaTransactionManager")
public class TransferAssetHelper {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private CmsService cmsService;

    public List<SettlementRecordDto> toTransferAssetsDto(List<InspecieAsset> transferAssets) {
        // retrieve all asset-ids.
        final List<String> assetIds = Lambda.convert(transferAssets, new Converter<InspecieAsset, String>() {
            @Override
            public String convert(InspecieAsset transferAsset) {
                return transferAsset.getAssetId();
            }
        });

        // retrieve corresponding asset from assetService.
        final Map<String, Asset> results = loadAssets(assetIds);

        // convert to settleRecordDto
        return Lambda.convert(transferAssets, new Converter<InspecieAsset, SettlementRecordDto>() {
            @Override
            public SettlementRecordDtoImpl convert(InspecieAsset transferAsset) {
                String assetCode = results.get(transferAsset.getAssetId()).getAssetCode();
                return TransferAssetConverter.toDto(transferAsset, assetCode);
            }
        });
    }

    public List<SettlementRecordDto> toTransferItemDto(List<TransferItem> transferItems) {
        // retrieve all asset-ids.
        final List<String> assetIds = Lambda.convert(transferItems, new Converter<TransferItem, String>() {
            @Override
            public String convert(TransferItem transferItem) {
                return transferItem.getAssetId();
            }
        });

        // retrieve corresponding asset from assetService.
        final Map<String, Asset> results = loadAssets(assetIds);

        // convert to settleRecordDto
        return Lambda.convert(transferItems, new Converter<TransferItem, SettlementRecordDto>() {
            @Override
            public SettlementRecordDto convert(TransferItem transferItem) {
                String assetId = transferItem.getAssetId();
                String assetCode = results.get(assetId).getAssetCode();
                String status = transferItem.getTransferStatus() == null ? null : transferItem.getTransferStatus().name();

                TransferAssetDtoImpl dto = new TransferAssetDtoImpl(assetId, assetCode, transferItem.getQuantity(), status);
                dto.setTransferId(EncodedString.fromPlainText(transferItem.getSettlementId()).toString());
                dto.setAmount(transferItem.getAmount());
                dto.setTransferDate(transferItem.getTransactionDateTime());
                dto.setAssetName(results.get(assetId).getAssetName());
                return dto;
            }
        });
    }

    public Map<String, Asset> loadAssets(List<String> assetIds) {
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        return assetService.loadAssets(assetIds, serviceErrors);

    }

    public boolean chkRevenueAssetIdentifier(String[] fields, List<String> assetIds) {
        Map<String, Asset> assetMap = loadAssets(assetIds);
        Iterator<?> iter = assetMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Asset> entry = (Entry<String, Asset>) iter.next();
            Asset assetVal = entry.getValue();
            if (assetVal.getAssetCode().equals(fields[0]) && assetVal.getRevenueAssetIndicator() == null) {
                return fields[4] != null && !fields[4].trim().isEmpty();
            }
        }
        return true;
    }

    public Map<String, SettlementRecordDto> constructAssetDtoMap(List<SettlementRecordDto> transferAssets) {
        Map<String, SettlementRecordDto> assetCodeKeyMap = new HashMap<>();
        Map<String, SettlementRecordDto> assetCodeKeyMap1 = new HashMap<>();

        if (!transferAssets.isEmpty()) {
            assetCodeKeyMap = Lambda.index(transferAssets, Lambda.on(SettlementRecordDtoImpl.class).getAssetCode().toLowerCase());
        }
        for (Map.Entry<String, SettlementRecordDto> entry : assetCodeKeyMap.entrySet()) {
            assetCodeKeyMap1.put(entry.getKey().toLowerCase(), entry.getValue());
        }
        return assetCodeKeyMap1;
    }

    private Map<String, BigDecimal> constructAssetFileMap(List<String[]> lines) {
        Map<String, BigDecimal> assetQuantityMap = new HashMap<>();

        for (String[] line : lines) {
            BigDecimal quantity = new BigDecimal(line[2].replace(",", ""));
            String assetCode = line[0].trim().toLowerCase();
            if (assetQuantityMap.containsKey(assetCode)) {
                assetQuantityMap.put(assetCode, assetQuantityMap.get(assetCode).add(quantity));
            } else {
                assetQuantityMap.put(assetCode, quantity);
            }
        }

        return assetQuantityMap;
    }

    private boolean isQuantityMatching(Map<String, SettlementRecordDto> assetCodeKeyMap,
            Map<String, BigDecimal> assetQuantityMap, List<DomainApiErrorDto> errors) {
        boolean result = true;

        final Set<Map.Entry<String, SettlementRecordDto>> entries = assetCodeKeyMap.entrySet();

        for (Map.Entry<String, SettlementRecordDto> entry : entries) {

            String assetCode = entry.getKey();
            if (!entry.getValue().getQuantity().equals(assetQuantityMap.get(assetCode))) {
                String[] params = new String[1];
                params[0] = assetCode.toUpperCase();
                addError(errors, "Err.IP-0522", params);
                result = false;
            }
        }
        return result;

    }

    private boolean areAssetCodesMatching(Map<String, SettlementRecordDto> assetCodeKeyMap,
            Map<String, BigDecimal> assetQuantityMap, List<DomainApiErrorDto> errors) {
        boolean result = true;
        // h1 - h2
        Set<String> diff1 = new HashSet<String>(assetCodeKeyMap.keySet());
        Set<String> diff2 = new HashSet<String>(assetQuantityMap.keySet());
        diff1.removeAll(assetQuantityMap.keySet());
        Iterator<String> iter = diff1.iterator();
        while (iter.hasNext()) {
            String[] params = new String[1];
            params[0] = iter.next().toUpperCase();
            addError(errors, "Err.IP-0520", params);
            result = false;

        }

        // h2 - h1

        diff2.removeAll(assetCodeKeyMap.keySet());
        Iterator<String> iter1 = diff2.iterator();

        while (iter1.hasNext()) {
            String[] params = new String[1];
            params[0] = iter1.next().toUpperCase();
            addError(errors, "Err.IP-0521", params);
            result = false;
        }

        return result;

    }

    public void assetRelatedCumulativeValidations(List<String[]> taxParcels, InspecieTransferDtoImpl transferDto,
            List<DomainApiErrorDto> errors) {
        Map<String, SettlementRecordDto> assetCodeDtoMap = constructAssetDtoMap(transferDto.getSettlementRecords());
        Map<String, BigDecimal> assetCodeFileMap = constructAssetFileMap(taxParcels);
        areAssetCodesMatching(assetCodeDtoMap, assetCodeFileMap, errors);
        isQuantityMatching(assetCodeDtoMap, assetCodeFileMap, errors);

    }

    public void addError(List<DomainApiErrorDto> errors, String errorCode, String[] params) {
        errors.add(new DomainApiErrorDto(errorCode, null, null, cmsService.getDynamicContent(errorCode, params), ErrorType.ERROR));
    }
}