package com.bt.nextgen.reports.account.transfer.inspecie;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.SponsorDetailsDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.service.InspecieTransferDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.reports.account.common.AccountReportV2;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

// PDF Report containing inspecie asset transfer assets, downloaded from Transfer Status screen by an Adviser
@Report("inspecieTransferReportV2")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_Client_Orders')")
public class InspecieTransferReport extends AccountReportV2 {

    private static final String ACCOUNT_ID = "account-id";
    private static final String TRANSFER_ID = "transferId";

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private InspecieTransferDtoService transferService;

    @Override
    public Collection<?> getData(Map<String, Object> params, Map<String, Object> dataCollections) {
        String accountId = (String) params.get(ACCOUNT_ID);
        String transferId = (String) params.get(TRANSFER_ID);

        if (StringUtils.isBlank(accountId) || StringUtils.isBlank(transferId)) {
            throw new IllegalArgumentException("Provide a valid account and transfer ID");
        }

        InspecieTransferKey key = new InspecieTransferKey(accountId, transferId);
        ServiceErrors serviceErrors = new FailFastErrorsImpl();

        InspecieTransferDto dto = transferService.find(key, serviceErrors);
        Map<String, Asset> assets = getAssetMap(dto, serviceErrors);

        InspecieTransferReportData reportData = toReportData(key, dto, assets);
        return Collections.singletonList(reportData);
    }

    private Map<String, Asset> getAssetMap(InspecieTransferDto dto, ServiceErrors serviceErrors) {
        Collection<String> assetIds = new ArrayList<String>();
        for (SettlementRecordDto rec : dto.getSettlementRecords()) {
            assetIds.add(rec.getAssetId());
        }

        return assetService.loadAssets(assetIds, serviceErrors);
    }

    private InspecieTransferReportData toReportData(InspecieTransferKey key, InspecieTransferDto dto, Map<String, Asset> assets) {
        TransferType transferType = TransferType.forDisplay(dto.getTransferType());
        SponsorDetailsDtoImpl sponsor = dto.getSponsorDetails();

        List<InspecieAssetReportData> inspecieAssets = new ArrayList<>();
        for (SettlementRecordDto record : dto.getSettlementRecords()) {
            Asset asset = assets.get(record.getAssetId());
            inspecieAssets.add(new InspecieAssetReportData(record, sponsor, asset));
        }

        return new InspecieTransferReportData(key.getTransferId(), transferType, inspecieAssets);
    }

    @Override
    public String getReportType(Map<String, Object> params, Map<String, Object> dataCollections) {
        return "Transfer details";
    }
}
