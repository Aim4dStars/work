package com.bt.nextgen.reports.account;

import com.bt.nextgen.api.account.v3.model.AccountDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.bt.nextgen.api.inspecietransfer.v2.model.SettlementRecordDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.TransferAssetDtoImpl;
import com.bt.nextgen.api.inspecietransfer.v2.service.InspecieTransferDtoService;
import com.bt.nextgen.core.reporting.stereotype.Report;
import com.bt.nextgen.core.reporting.stereotype.ReportBean;
import com.bt.nextgen.core.reporting.stereotype.ReportInitializer;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.integration.transfer.TransferType;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.bt.nextgen.core.api.UriMappingConstants.*;

@Report("inspecieTransferReport")
@PreAuthorize("isAuthenticated() and hasPermission(null, 'View_Client_Orders')")
public class InspecieTransferReport extends AccountReport {

    @Autowired
    @Qualifier("avaloqAssetIntegrationService")
    private AssetIntegrationService assetService;

    @Autowired
    private InspecieTransferDtoService transferService;

    private InspecieTransferDto transferDto;

    @ReportInitializer
    public void init(Map<String, String> params) {

        String accountId = params.get(ACCOUNT_ID_URI_MAPPING);
        String transferId = params.get("transferId");

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        InspecieTransferKey transferKey = new InspecieTransferKey(accountId, transferId);
        InspecieTransferDto dto = transferService.find(transferKey, serviceErrors);

        this.transferDto = dto;
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("transferDto")
    public InspecieTransferDto getInspecieTransfer(Map<String, String> params) throws IOException {

        Map<String, Asset> assetMap = getAssetMap(this.transferDto);
        List<SettlementRecordDto> recDtoList = new ArrayList<>();
        for (SettlementRecordDto rec : transferDto.getSettlementRecords()) {
            TransferAssetDtoImpl ta = new TransferAssetDtoImpl(rec.getAssetId(), rec.getAssetCode(), rec.getQuantity(),
                    rec.getTransferStatus());
            ta.setAssetName(assetMap.get(rec.getAssetId()).getAssetName());
            ta.setTransferDate(rec.getTransferDate());
            ta.setAmount(rec.getAmount());
            ta.setQuantity(rec.getQuantity());
            recDtoList.add(ta);
        }

        this.transferDto.getSettlementRecords().clear();
        this.transferDto.getSettlementRecords().addAll(recDtoList);
        return this.transferDto;
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("transferType")
    public TransferType getTransferType(Map<String, String> params) throws IOException {
        return TransferType.forDisplay(transferDto.getTransferType());
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("investmentAccount")
    public AccountDto getInvestmentAccount(Map<String, String> params) {
        return this.getAccount(params).iterator().next();
    }

    @SuppressWarnings("squid:S1172")
    @ReportBean("reportType")
    public String getReportName(Map<String, String> params) {
        return "Transfer details";
    }

    /**
     * Loads transfer-assets from the given transferDto.
     * 
     * @param orderItems
     *            the order items
     * @return the asset map
     */
    protected Map<String, Asset> getAssetMap(InspecieTransferDto dto) {
        Collection<String> assetIds = new ArrayList<String>();
        for (SettlementRecordDto rec : dto.getSettlementRecords()) {
            assetIds.add(rec.getAssetId());
        }

        ServiceErrors serviceErrors = new FailFastErrorsImpl();
        return assetService.loadAssets(assetIds, serviceErrors);
    }
}
