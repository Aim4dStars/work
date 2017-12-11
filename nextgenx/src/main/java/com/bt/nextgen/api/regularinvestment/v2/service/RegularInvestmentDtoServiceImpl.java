package com.bt.nextgen.api.regularinvestment.v2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.regularinvestment.v2.model.RIPAction;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentDelegateService;

@Service("RegularInvestmentDtoServiceV2")
@Transactional(value = "springJpaTransactionManager")
public class RegularInvestmentDtoServiceImpl extends RegularInvestmentBaseDtoServiceImpl implements RegularInvestmentDtoService {

    @Autowired
    private RegularInvestmentDelegateService regularInvestmentService;

    @Override
    public RegularInvestmentDto update(RegularInvestmentDto dto, ServiceErrors serviceErrors) {

        // Update the RIP based on the provided status.
        RegularInvestment updatedRegInv = null;
        ServiceErrors errorsDD = new ServiceErrorsImpl();
        ServiceErrors errorsRIP = new ServiceErrorsImpl();

        RIPAction action = RIPAction.getRIPAction(dto.getRipStatus());
        if (RIPAction.SUSPEND == action) {
            updatedRegInv = regularInvestmentService.suspendRegularInvestment(dto.getKey(), serviceErrors);
        } else if (RIPAction.CANCELLED == action) {
            updatedRegInv = regularInvestmentService.cancelRegularInvestment(dto.getKey(), serviceErrors);
        } else if (RIPAction.RESUME == action) {
            updatedRegInv = regularInvestmentService.resumeRegularInvestment(dto.getKey(), errorsRIP, errorsDD);
        }
        return this.toRegularInvestmentDto(updatedRegInv, dto, errorsDD, errorsRIP);
    }

    @Override
    public RegularInvestmentDto validate(RegularInvestmentDto invDto, ServiceErrors serviceErrors) {
        if (invDto.getOwner() == null) {
            invDto.setOwner(userProfileService.getAvaloqId());
        }
        ServiceErrors errorsDD = new ServiceErrorsImpl();
        ServiceErrors errorsRIP = new ServiceErrorsImpl();
        RegularInvestment regInv = regularInvestmentService.validateRegularInvestment(toRegularInvestment(invDto, serviceErrors),
                errorsDD, errorsRIP);

        return toRegularInvestmentDto(regInv, invDto, errorsDD, errorsRIP);

    }

    @Override
    public RegularInvestmentDto submit(RegularInvestmentDto dto, ServiceErrors serviceErrors) {
        ServiceErrors errorsDD = new ServiceErrorsImpl();
        ServiceErrors errorsRIP = new ServiceErrorsImpl();
        RegularInvestment regInv = regularInvestmentService.submitRegularInvestment(toRegularInvestment(dto, serviceErrors),
                errorsDD, errorsRIP);

        return toRegularInvestmentDto(regInv, dto, errorsDD, errorsRIP);
    }

    @Override
    public RegularInvestmentDto find(OrderGroupKey key, ServiceErrors serviceErrors) {
        ServiceErrors errorsDD = new ServiceErrorsImpl();
        ServiceErrors errorsRIP = new ServiceErrorsImpl();
        RegularInvestment rip = regularInvestmentService.loadRegularInvestment(
                AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId())), key.getOrderGroupId(), errorsDD, errorsRIP);
        if (rip != null)
            return toRegularInvestmentDto(rip, null, errorsDD, errorsRIP);
        return null;
    }

    @Override
    public RegularInvestmentDto create(RegularInvestmentDto dto, ServiceErrors serviceErrors) {
        ServiceErrors errorsDD = new ServiceErrorsImpl();
        ServiceErrors errorsRIP = new ServiceErrorsImpl();
        RegularInvestment regInv = regularInvestmentService.saveRegularInvestment(toRegularInvestment(dto, serviceErrors),
                errorsDD, errorsRIP);

        return toRegularInvestmentDto(regInv, dto, errorsDD, errorsRIP);
    }

}
