package com.bt.nextgen.api.fees.v1.service;

import com.bt.nextgen.api.account.v3.model.AccountKey;
import com.bt.nextgen.api.fees.v1.model.AssetMappedAccountTransactionFeesDto;
import com.bt.nextgen.api.fees.v1.model.TransactionFeeDto;
import com.bt.nextgen.api.trading.v1.util.TradableAssetsDtoServiceHelper;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transactionfee.AvaloqTransactionFee;
import com.bt.nextgen.service.avaloq.transactionfee.CacheManagedAvaloqTransactionFeeIntegrationService;
import com.bt.nextgen.service.integration.broker.BrokerKey;
import com.bt.nextgen.service.integration.product.ProductKey;
import com.bt.nextgen.service.integration.transactionfee.ContainerType;
import com.bt.nextgen.service.integration.transactionfee.ExecutionType;
import com.btfin.panorama.service.integration.account.WrapAccountDetail;
import com.btfin.panorama.service.integration.asset.AssetType;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated Use V2
 */
@Deprecated
@Service
public class AssetMappedAccountTransactionFeesDtoServiceImpl implements TransactionFeeDtoService {

    @Autowired
    private CacheManagedAvaloqTransactionFeeIntegrationService transactionFeeService;

    @Autowired
    private TradableAssetsDtoServiceHelper tradableAssetsDtoServiceHelper;

    @Override
    public AssetMappedAccountTransactionFeesDto find(AccountKey accountKey, ServiceErrors serviceErrors) {
        WrapAccountDetail account = tradableAssetsDtoServiceHelper.loadAccount(accountKey.getAccountId(), serviceErrors);
        BrokerKey adviserKey = account.getAdviserKey();
        BrokerKey dealerKey = tradableAssetsDtoServiceHelper.loadBroker(account, serviceErrors);
        ProductKey productKey = account.getProductKey();

        String productId = productKey == null ? null : productKey.getId();
        String dealerGroupId = dealerKey == null ? null : dealerKey.getId();
        String adviserId = adviserKey == null ? null : adviserKey.getId();

        // get the direct fees
        AssetMappedAccountTransactionFeesDto fees = toAssetMappedTransactionFees(accountKey.getAccountId(),
                transactionFeeService.loadDirectTransactionFees(productId, dealerGroupId, adviserId, serviceErrors));

        // if the account has a ihin then override the share fees
        if (StringUtils.isNotEmpty(account.getIhin())) {
            List<AvaloqTransactionFee> ihinFees = transactionFeeService.loadTransactionFees(ExecutionType.DIRECT_MARKET_ACCESS,
                    ContainerType.IHIN, productId, dealerGroupId, adviserId, serviceErrors);
            for (AvaloqTransactionFee transactionFee : ihinFees) {
                if (transactionFee.getFeeType() != null && AssetType.SHARE.equals(transactionFee.getFeeType().getAssetType())) {
                    fees.getAssetTransactionFees().put(transactionFee.getFeeType().getAssetType().getDisplayName(),
                            toTransactionFeeDto(transactionFee));
                }
            }
        }

        return fees;
    }

    private AssetMappedAccountTransactionFeesDto toAssetMappedTransactionFees(String accountId,
            List<AvaloqTransactionFee> transactionFees) {
        Map<String, TransactionFeeDto> mappedTransactionFees = new HashMap<>();
        for (AvaloqTransactionFee transactionFee : transactionFees) {
            if (transactionFee.getFeeType() != null && transactionFee.getFeeType().getAssetType() != null) {
                mappedTransactionFees.put(transactionFee.getFeeType().getAssetType().getDisplayName(),
                        toTransactionFeeDto(transactionFee));
            }
        }
        return new AssetMappedAccountTransactionFeesDto(new AccountKey(accountId), mappedTransactionFees);

    }

    private TransactionFeeDto toTransactionFeeDto(AvaloqTransactionFee avaloqTransactionFee) {
        TransactionFeeDto transactionFee = new TransactionFeeDto(avaloqTransactionFee.getFixedAmount(),
                avaloqTransactionFee.getFactor(), avaloqTransactionFee.getMinimum(), avaloqTransactionFee.getMaximum());
        return transactionFee;
    }
}
