package com.bt.nextgen.api.inspecietransfer.v2.service;

import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferDto;
import com.bt.nextgen.api.inspecietransfer.v2.model.InspecieTransferKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.transfer.TransferDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.transfer.InspecieTransferIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @deprecated Use V3
 */
@Deprecated
@Service("InspecieTransferDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class InspecieTransferDtoServiceImpl extends InspecieTransferBaseDtoServiceImpl implements InspecieTransferDtoService {

    @Autowired
    private InspecieTransferIntegrationService inspecieTransferIntegrationService;

    @Override
    public InspecieTransferDto find(InspecieTransferKey key, ServiceErrors serviceErrors) {
        TransferDetailsImpl transferDetails = (TransferDetailsImpl) inspecieTransferIntegrationService.loadTransferDetails(
                key.getTransferId(), AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId())), serviceErrors);
        return toTransferDto(transferDetails);
    }

    @Override
    public InspecieTransferDto validate(InspecieTransferDto transferDto, ServiceErrors serviceErrors) {
        TransferDetailsImpl transferDetails = (TransferDetailsImpl) inspecieTransferIntegrationService.validateTransfer(
                toTransferDetails(transferDto), serviceErrors);
        return toTransferDto(transferDetails);
    }

    @Override
    public InspecieTransferDto submit(InspecieTransferDto transferDto, ServiceErrors serviceErrors) {
        TransferDetailsImpl transferDetails = (TransferDetailsImpl) inspecieTransferIntegrationService.submitTransfer(
                toTransferDetails(transferDto), serviceErrors);
        return toTransferDto(transferDetails);
    }
}
