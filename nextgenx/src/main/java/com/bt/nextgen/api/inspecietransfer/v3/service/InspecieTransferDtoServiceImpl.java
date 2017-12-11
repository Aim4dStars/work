package com.bt.nextgen.api.inspecietransfer.v3.service;

import com.bt.nextgen.api.inspecietransfer.v3.model.InspecieTransferDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupDetails;
import com.bt.nextgen.service.integration.transfer.transfergroup.TransferGroupIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("InspecieTransferDtoServiceV3")
@Transactional(value = "springJpaTransactionManager")
public class InspecieTransferDtoServiceImpl extends InspecieTransferBaseDtoServiceImpl implements InspecieTransferDtoService {

    @Autowired
    private TransferGroupIntegrationService transferService;

    @Override
    public InspecieTransferDto validate(InspecieTransferDto transferDto, ServiceErrors serviceErrors) {

        TransferGroupDetails transferDetails = transferService.validateTransfer(toTransferGroupDetails(transferDto),
                serviceErrors);

        return toTransferDto(transferDto.getKey().getAccountId(), transferDetails, serviceErrors);
    }

    @Override
    public InspecieTransferDto submit(InspecieTransferDto transferDto, ServiceErrors serviceErrors) {
        TransferGroupDetails transferDetails = transferService.submitTransfer(toTransferGroupDetails(transferDto), serviceErrors);

        return toTransferDto(transferDto.getKey().getAccountId(), transferDetails, serviceErrors);
    }
}
