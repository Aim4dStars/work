package com.bt.nextgen.api.account.v1.util;


import com.bt.nextgen.api.account.v1.model.transitions.TransitionAccountDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.integration.account.*;
import com.bt.nextgen.service.integration.product.Product;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.btfin.panorama.service.integration.account.WrapAccount;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by L069552 on 18/09/2015.
 */
/** This method converts the Domain level Account Objects to Dto and returns back to the Service Layer */
@Deprecated
public class TransitionAccountDtoConverter {

    private Map<AccountKey, WrapAccount> accountMap;
    private Map<String, AccountBalance> accountBalanceMap;
    private Map<ProductKey, Product> productMap;
    private List<TransitionAccountBPDetail> transitionAccountBPDetails;



    /**
     *Constructor which takes the accountService,BalanceService and ProductService for populating the list of Transition Accounts for the logged in Adviser
     * @param accountMap
     * @param productMap
     *
     */
    public TransitionAccountDtoConverter(Map<com.bt.nextgen.service.integration.account.AccountKey, WrapAccount> accountMap, Map<String, AccountBalance> accountBalanceMap,
                                         Map<ProductKey, Product> productMap,List<TransitionAccountBPDetail> transitionAccountDetails
    )
    {
        this.accountMap = accountMap;
        this.accountBalanceMap = accountBalanceMap;
        this.productMap = productMap;
        this.transitionAccountBPDetails = transitionAccountDetails;

    }

    /**
     * Utility method which converts the domain level account objects to respective Dto
     * @return
     */
    public Map<com.bt.nextgen.service.integration.account.AccountKey, TransitionAccountDto> convert()
    {
        Map<com.bt.nextgen.service.integration.account.AccountKey, TransitionAccountDto> accountDtoMap = new HashMap<>();

        for(TransitionAccountBPDetail transitionAccountBPDetail : transitionAccountBPDetails){

            com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey.valueOf(transitionAccountBPDetail.getAccountKey().getId());

            if(accountMap !=null && accountMap.get(accountKey) != null){

                WrapAccount wrapAccount = accountMap.get(accountKey);
                AccountBalance accountBalance = accountBalanceMap != null ? accountBalanceMap.get(transitionAccountBPDetail.getAccountKey().getId()) :null;
                Product product = productMap != null ? productMap.get(wrapAccount.getProductKey()) :null;
                TransitionAccountDto transitionAccountDto = toTransitionAccountDto(wrapAccount, accountBalance, product,transitionAccountBPDetail);
                accountDtoMap.put(wrapAccount.getAccountKey(),transitionAccountDto);

            }
        }
        return accountDtoMap;

    }

    protected TransitionAccountDto toTransitionAccountDto(WrapAccount wrapAccount, AccountBalance accountBalance, Product product,
                                                          TransitionAccountBPDetail transitionAccountBPDetail)
    {
        TransitionAccountDto transitionAccountDto = new TransitionAccountDto(new com.bt.nextgen.api.account.v2.model.AccountKey(EncodedString.fromPlainText(wrapAccount.getAccountKey().getId()).toString()));
        transitionAccountDto.setAccountId(wrapAccount.getAccountKey().getId());
        transitionAccountDto.setAccountName(wrapAccount.getAccountName());
        transitionAccountDto.setAccountNumber(wrapAccount.getAccountNumber());
        transitionAccountDto.setAccountStatus(wrapAccount.getAccountStatus().getStatusDescription());
        transitionAccountDto.setAvailableCash(accountBalance != null ? accountBalance.getAvailableCash() :new BigDecimal(0));
        transitionAccountDto.setPortfolioValue(accountBalance != null
                &&  accountBalance.getPortfolioValue()!= null ? accountBalance.getPortfolioValue().setScale(2,BigDecimal.ROUND_UP) :new BigDecimal(0));
        transitionAccountDto.setProduct(product != null ? product.getProductName():"");
        transitionAccountDto.setAccountType(wrapAccount.getAccountStructureType().name());
        transitionAccountDto.setExpectedCash(transitionAccountBPDetail.getExpectedCashAmount());
        transitionAccountDto.setExpectedAssetValue(transitionAccountBPDetail.getExpectedAssetAmount());
        transitionAccountDto.setTransitionStatus(transitionAccountBPDetail.getTransitionStatus().getStatusDescription());
        transitionAccountDto.setTransferType(transitionAccountBPDetail.getTransferType());

        return  transitionAccountDto;

    }



}
