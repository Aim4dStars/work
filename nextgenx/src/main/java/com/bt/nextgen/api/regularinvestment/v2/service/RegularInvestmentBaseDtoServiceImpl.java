package com.bt.nextgen.api.regularinvestment.v2.service;

import com.bt.nextgen.api.account.v2.model.BankAccountDto;
import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.movemoney.v2.util.DepositUtil;
import com.bt.nextgen.api.order.service.OrderGroupBaseDtoServiceImpl;
import com.bt.nextgen.api.regularinvestment.v2.model.InvestmentPeriodDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto;
import com.bt.nextgen.core.api.model.DomainApiErrorDto.ErrorType;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.regularinvestment.RIPScheduleImpl;
import com.bt.nextgen.service.avaloq.regularinvestment.RegularInvestmentImpl;
import com.bt.nextgen.service.integration.regularinvestment.RIPRecurringFrequency;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service("RegularInvestmentBaseDtoServiceV2")
public class RegularInvestmentBaseDtoServiceImpl extends OrderGroupBaseDtoServiceImpl {

    @Autowired
    private AccountHelper accHelperService;

    public RegularInvestment toRegularInvestment(RegularInvestmentDto invDto, ServiceErrors serviceErrors) {
        RegularInvestmentImpl regInv = null;
        if (invDto != null) {

            regInv = new RegularInvestmentImpl(this.toOrderGroup(invDto, serviceErrors));

            regInv.setRIPSchedule(new RIPScheduleImpl(invDto.getInvestmentStartDate(), invDto.getInvestmentEndDate(),
                    RIPRecurringFrequency.valueOf(invDto.getFrequency())));

            regInv.setRIPStatus(RIPStatus.forDisplay(invDto.getRipStatus()));
            regInv.setDirectDebitDetails(accHelperService.getRecurringDepositDetails(invDto, serviceErrors));
        }

        return regInv;
    }

    public RegularInvestmentDto toRegularInvestmentDto(RegularInvestment regularInvestment, RegularInvestmentDto ripDto,
            ServiceErrors serviceErrorsDD, ServiceErrors serviceErrorsRIP) {
        if (regularInvestment == null)
            return null;

        if (regularInvestment.getDirectDebitDetails() == null) {
            return createRIPDto(regularInvestment, serviceErrorsRIP, null, ripDto);
        } else {
            return processRipDeposit(regularInvestment, ripDto, serviceErrorsDD, serviceErrorsRIP);
        }

    }

    private RegularInvestmentDto processRipDeposit(RegularInvestment regularInvestment, RegularInvestmentDto ripDto,
            ServiceErrors serviceErrorsDD, ServiceErrors serviceErrorsRIP) {
        DepositDto depositDto = new DepositDto();
        depositDto = DepositUtil.toDepositDto(regularInvestment.getDirectDebitDetails(), depositDto);

        String accId = regularInvestment.getAccountKey().getId();
        BankAccountDto bankDto = accHelperService.getBankAccountDetails(accId, serviceErrorsDD);

        depositDto.setToPayeeDto(RIPPayeeUtil.getBankPayeeDto(bankDto));
        depositDto.setFromPayDto(
                RIPPayeeUtil.getFromPayDto(regularInvestment, accHelperService.getPayeeDetails(accId, serviceErrorsDD)));
        if (regularInvestment.getDirectDebitDetails().getRecurringFrequency() != null) {
            depositDto.setFrequency(regularInvestment.getDirectDebitDetails().getRecurringFrequency().name());
        }

        if (serviceErrorsDD != null) {
            List<ServiceError> errorList = new ArrayList<>();
            addErrorsToDepositDto(depositDto, serviceErrorsDD.getErrorList().iterator(), errorList);

            String status = ripDto != null ? ripDto.getStatus() : null;

            if (!(errorList.isEmpty()) && status != null && ("submit").equals(status)) {

                // Vetting exception encountered within Direct-Debit when
                // submitting a new RIP.
                return new RegularInvestmentDto(new RegularInvestmentDto(), depositDto,
                        new InvestmentPeriodDto(ripDto.getInvestmentStartDate(), ripDto.getInvestmentEndDate(), null,
                                ripDto.getFrequency()),
                        ripDto.getRipStatus(), bankDto);
            }
        }
        return createRIPDto(regularInvestment, serviceErrorsRIP, depositDto, ripDto);
    }

    private void addErrorsToDepositDto(DepositDto depositDto, Iterator<ServiceError> serviceError, List<ServiceError> errorList) {
        List<DomainApiErrorDto> apiErrorList = new ArrayList<>();
        while (serviceError.hasNext()) {
            ServiceError serror = (ServiceError) serviceError.next();
            errorList.add(serror);
            DomainApiErrorDto error = new DomainApiErrorDto(serror.getId(), null, serror.getReason(), serror.getMessage(),
                    ErrorType.ERROR);
            apiErrorList.add(error);
        }
        depositDto.setErrors(apiErrorList);
    }

    private RegularInvestmentDto createRIPDto(RegularInvestment regularInvestment, ServiceErrors serviceErrorsRIP,
            DepositDto depositDto, RegularInvestmentDto ripDto) {
        InvestmentPeriodDto investmentPeriodDto = new InvestmentPeriodDto(regularInvestment.getRIPSchedule());

        if (depositDto != null) {
            DateTime firstExecDate = null;
            String frequency = null;
            if (regularInvestment.getRIPSchedule() != null) {
                firstExecDate = regularInvestment.getRIPSchedule().getFirstExecDate();
                frequency = regularInvestment.getRIPSchedule().getRecurringFrequency().name();
            }

            investmentPeriodDto = new InvestmentPeriodDto(firstExecDate,
                    regularInvestment.getDirectDebitDetails().getEndDate() != null
                            ? new DateTime(regularInvestment.getDirectDebitDetails().getEndDate()) : null,
                    regularInvestment.getDirectDebitDetails().getNextTransactionDate() != null
                            ? regularInvestment.getDirectDebitDetails().getNextTransactionDate() : null,
                    frequency);

        }
        if (ripDto == null) {
            return new RegularInvestmentDto(toOrderGroupDto(regularInvestment, serviceErrorsRIP), depositDto,
                    investmentPeriodDto, getRipStatus(regularInvestment), accHelperService.getBankAccountDetails(
                            regularInvestment.getAccountKey().getId(), serviceErrorsRIP));
        }
        return new RegularInvestmentDto(toOrderGroupDto(regularInvestment, serviceErrorsRIP), depositDto,
                    investmentPeriodDto, getRipStatus(regularInvestment), ripDto.getCashAccountDto());
    }

    private String getRipStatus(RegularInvestment regularInvestment) {
        String status = null;
        if (regularInvestment.getRIPStatus() != null) {
            status = regularInvestment.getRIPStatus().getDisplayName();
        }
        return status;
    }

}
