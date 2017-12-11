package com.bt.nextgen.api.account.v1.util;
import com.bt.nextgen.api.account.v1.model.transitions.TransitionAssetDto;
import com.bt.nextgen.api.account.v2.model.AccountKey;
import com.bt.nextgen.service.integration.account.TransitionSettlements;
import com.bt.nextgen.service.integration.account.TransitionSettlementsHolder;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.product.Product;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by L069552 on 12/10/2015.
 */
@Deprecated
public final class AssetTransferDtoConverter {

    final public static String REVERSED = "REVERSED";
    final public static String DISCARDED="DISCARDED";
    final public static String SETTLE_DISCARDED="SETTLE_DISCARDED";

    private AssetTransferDtoConverter(){

    }

    public static List<TransitionAssetDto> fetchAssetTransferDtos( AccountKey accountKey,WrapAccount wrapAccount,Product product,Map<String, Asset> assetMap,
                                                                   TransitionSettlementsHolder transitionSettlementsHolder){

        List<TransitionSettlements> transitionSettlements =null != transitionSettlementsHolder ? transitionSettlementsHolder.getTransitionSettlements():null;
        Map<String,Asset> filteredTransitionAssets = filterAssetIds(transitionSettlements,assetMap);

        if(transitionSettlements != null && !transitionSettlements.isEmpty()) {
            return populateAssetTransferDtos(accountKey, filteredTransitionAssets, transitionSettlements, wrapAccount, product);
        }else{


            TransitionAssetDto transitionAssetDto = new TransitionAssetDto(accountKey);
            transitionAssetDto.setAccountName(null != wrapAccount ? wrapAccount.getAccountName() : "");
            transitionAssetDto.setProductName(null != product ? product.getProductName():"");
            transitionAssetDto.setAccountNumber(null != wrapAccount ? wrapAccount.getAccountNumber():"");
            transitionAssetDto.setAccountType(null != wrapAccount ? wrapAccount.getAccountStructureType().name():"");
            List<TransitionAssetDto> listTransitionsAssets = new ArrayList<>();
            listTransitionsAssets.add(transitionAssetDto);

            return listTransitionsAssets;
        }

    }



    private static Map<String, Asset> filterAssetIds(List<TransitionSettlements> transitionSettlements, Map<String, Asset> assetMap) {

        Map<String,Asset> filteredTransitionAssets = new HashMap<>();
        if(null != transitionSettlements  &&  !transitionSettlements.isEmpty()){

            for(TransitionSettlements transitionSettlement :transitionSettlements){

                if(null != transitionSettlement && null != assetMap &&
                        assetMap.get(transitionSettlement.getAssetKey().getId()) != null){

                    filteredTransitionAssets.put(transitionSettlement.getAssetKey().getId(),assetMap.get(transitionSettlement.getAssetKey().getId()));
                }
            }
        }
        return filteredTransitionAssets;
    }

    private static List<TransitionAssetDto> populateAssetTransferDtos(AccountKey accountKey,Map<String, Asset> transitionAssetMap, List<TransitionSettlements> transitionSettlementHolder,
                                                                      WrapAccount wrapAccount,Product product){

        List<TransitionAssetDto> listTransitionAssetDto = new ArrayList<>();

        for(TransitionSettlements transitionSettlement : transitionSettlementHolder){

            if(!REVERSED.equalsIgnoreCase(transitionSettlement.getTransitionWorkflowStatus().name())
                    && !DISCARDED.equalsIgnoreCase(transitionSettlement.getTransitionWorkflowStatus().name())
                    && !SETTLE_DISCARDED.equalsIgnoreCase(transitionSettlement.getTransitionWorkflowStatus().name())){

                TransitionAssetDto transitionAssetDto = new TransitionAssetDto(accountKey);
                transitionAssetDto.setAssetCluster(transitionAssetMap.get(transitionSettlement.getAssetKey().getId()).getAssetCluster().name());
                transitionAssetDto.setAssetName(transitionAssetMap.get(transitionSettlement.getAssetKey().getId()).getAssetName());
                transitionAssetDto.setAssetCode(transitionAssetMap.get(transitionSettlement.getAssetKey().getId()).getAssetCode());
                transitionAssetDto.setConsiderationAmt(transitionSettlement.getAmount());
                transitionAssetDto.setSubmittedTimestamp(transitionSettlement.getTransitionDate());
                transitionAssetDto.setOrderId(transitionSettlement.getOrderNumber());
                AssetTransferEnum assetTransferEnum = AssetTransferEnum.valueOf(transitionSettlement.getTransitionWorkflowStatus().name());
                transitionAssetDto.setTransitionStatus(assetTransferEnum.value());
                transitionAssetDto.setAccountName(null != wrapAccount ? wrapAccount.getAccountName() : "");
                transitionAssetDto.setProductName(product.getProductName());
                transitionAssetDto.setAccountNumber(null != wrapAccount ? wrapAccount.getAccountNumber():"");
                transitionAssetDto.setAccountType(null != wrapAccount ? wrapAccount.getAccountStructureType().name():"");
                transitionAssetDto.setQuantity(new BigDecimal(transitionSettlement.getQuantity()));
                listTransitionAssetDto.add(transitionAssetDto);

            }
        }


        return listTransitionAssetDto;
    }

}
