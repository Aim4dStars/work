package com.bt.nextgen.api.regularinvestment.v2.service;

import com.bt.nextgen.api.movemoney.v2.model.DepositDto;
import com.bt.nextgen.api.order.model.OrderGroupDto;
import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.bt.nextgen.api.order.model.OrderItemDto;
import com.bt.nextgen.api.regularinvestment.v2.model.InvestmentPeriodDto;
import com.bt.nextgen.api.regularinvestment.v2.model.RegularInvestmentDto;
import com.bt.nextgen.core.api.operation.ApiSearchCriteria;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.PayAnyoneAccountDetailsImpl;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.btfin.panorama.service.integration.account.WrapAccount;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentDelegateService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service("RegularInvestmentTransactionDtoServiceV2")
public class RegularInvestmentTransactionDtoServiceImpl extends RegularInvestmentBaseDtoServiceImpl implements
        RegularInvestmentTransactionDtoService {

    @Autowired
    private AccountHelper accHelperService;

    @Autowired
    private DepositDtoHelper depositDtoHelperService;

    @Autowired
    @Qualifier("regularInvestmentDelegateService")
    private RegularInvestmentDelegateService regularInvestmentService;

    @Override
    public List<RegularInvestmentDto> search(List<ApiSearchCriteria> criteriaList, ServiceErrors serviceErrors) {
        List<RegularInvestmentDto> ripDtos = new ArrayList<>();
        List<RegularInvestmentTransaction> rips = null;

        AccountKey accountKey = null;
        String mode = null;

        for (ApiSearchCriteria parameter : criteriaList) {
            if (Attribute.ACCOUNT_ID.equals(parameter.getProperty())) {
                accountKey = AccountKey.valueOf(EncodedString.toPlainText(parameter.getValue()));
            } else if ("serviceType".equalsIgnoreCase(parameter.getProperty()) && "cache".equalsIgnoreCase(parameter.getValue())) {
                mode = "cache";
            } else {
                throw new IllegalArgumentException("Unsupported search");
            }
        }

        rips = regularInvestmentService.loadRegularInvestments(accountKey, serviceErrors, mode);

        // convert to DTO
        if (!rips.isEmpty()) {
            for (RegularInvestmentTransaction rip : rips) {
                ripDtos.add(toRegularInvestmentDto(rip, toRecurringDepositDetails(rip), serviceErrors));
            }

        }
        return ripDtos;

    }

    private RecurringDepositDetails toRecurringDepositDetails(RegularInvestmentTransaction rip) {
        RecurringDepositDetailsImpl dd = null;

        if (rip.getRefDocId() != null) {
            dd = new RecurringDepositDetailsImpl();
            dd.setReceiptNumber(rip.getRefDocId());
            dd.setRecurringFrequency(rip.getDDFrequency());
            dd.setDepositAmount(rip.getDDAmount());
            if (rip.getDDFirstExecDate() != null) {
                dd.setTransactionDate(rip.getDDFirstExecDate());
                dd.setDepositDate(rip.getDDFirstExecDate());
                dd.setStartDate(rip.getDDFirstExecDate());
            }
            PayAnyoneAccountDetailsImpl payerDetails = new PayAnyoneAccountDetailsImpl();
            payerDetails.setAccount(rip.getPayerAccountId());
            dd.setPayAnyoneAccountDetails(payerDetails);
            if (rip.getDDLastExecDate() != null) {
                dd.setEndDate(rip.getDDLastExecDate());
            }
            if (rip.getDDNextExecDate() != null) {
                dd.setNextTransactionDate(rip.getDDNextExecDate());
            }
        }
        return dd;
    }

    protected RegularInvestmentDto toRegularInvestmentDto(RegularInvestmentTransaction regularInvestment,
            RecurringDepositDetails directDebit, ServiceErrors serviceErrorsRIP) {
        if (regularInvestment != null) {
            DepositDto depositDto = depositDtoHelperService.constructDepositDto(regularInvestment, directDebit, new DepositDto());

            OrderGroupDto orderDto = null;

            orderDto = toOrderGroupDto(regularInvestment, serviceErrorsRIP);

            String ripFrequency = null;
            if (regularInvestment.getRipFrequency() != null) {
                ripFrequency = regularInvestment.getRipFrequency().name();
            }
            if (directDebit != null) {
                RegularInvestmentDto invDto = new RegularInvestmentDto(orderDto, depositDto, new InvestmentPeriodDto(
                        regularInvestment.getRipFirstExecDate(), regularInvestment.getDDLastExecDate(),
                        regularInvestment.getDDNextExecDate(), ripFrequency), regularInvestment.getRipAmount(),
                        getCurrExecStatus(regularInvestment), getRIPStatus(regularInvestment),
                        accHelperService.getBankAccountDetails(regularInvestment.getAccountKey(), serviceErrorsRIP));

                return invDto;
            } else {
                RegularInvestmentDto invDto = new RegularInvestmentDto(orderDto, depositDto, new InvestmentPeriodDto(
                        regularInvestment.getRipFirstExecDate(), regularInvestment.getRipLastExecDate(),
                        regularInvestment.getRipNextExecDate(), ripFrequency), regularInvestment.getRipAmount(),
                        getCurrExecStatus(regularInvestment), getRIPStatus(regularInvestment),
                        accHelperService.getBankAccountDetails(regularInvestment.getAccountKey(), serviceErrorsRIP));

                return invDto;
            }

        } else {
            return null;
        }
    }

    private String getRIPStatus(RegularInvestmentTransaction regularInvestment) {
        if (regularInvestment.getRipStatus() == null)
            return null;

        RIPStatus status = regularInvestment.getRipStatus();
        // QC11539 We are only interested in the exec-status if the RIP is
        // active or in progress
        if ((RIPStatus.ACTIVE == status || RIPStatus.IN_PROGRESS == status)
                && RIPStatus.FAILED == regularInvestment.getCurrExecStatus()
                || RIPStatus.COMPLETED_PARTIALLY == regularInvestment.getCurrExecStatus()) {
            return RIPStatus.FAILED.getDisplayName();
        }

        return status.getDisplayName();
    }

    private String getCurrExecStatus(RegularInvestmentTransaction regularInvestment) {
        if (regularInvestment.getCurrExecStatus() == null)
            return null;
        else if (regularInvestment.getCurrExecStatus() != null
                && (regularInvestment.getCurrExecStatus().equals(RIPStatus.FAILED) || regularInvestment.getCurrExecStatus()
                        .equals(RIPStatus.COMPLETED_PARTIALLY))) {
            return RIPStatus.FAILED.getDisplayName();

        } else {
            return regularInvestment.getCurrExecStatus().getDisplayName();
        }
    }

    public OrderGroupDto toOrderGroupDto(RegularInvestmentTransaction regularInvestment, ServiceErrors serviceErrors) {
        String accountName = null;
        if (regularInvestment.getAccountKey() != null) {
            WrapAccount account = accountIntegrationService
                    .loadWrapAccountWithoutContainers(
                    AccountKey.valueOf(regularInvestment.getAccountKey()), serviceErrors);
            accountName = account != null ? account.getAccountName() : "";
        }
        AccountKey accountKey = AccountKey.valueOf(regularInvestment.getAccountKey());
        List<OrderItemDto> orders = Collections.emptyList();
        OrderGroupDto orderGroupDto = new OrderGroupDto(regularInvestment.getOrderGroupId() == null ? null : new OrderGroupKey(
                EncodedString.fromPlainText(accountKey.getId()).toString(), regularInvestment.getOrderGroupId()),
                regularInvestment.getLastUpdateDate(), null, orders, null, regularInvestment.getOwner(), regularInvestment.getOwnerName(), regularInvestment.getDescription(),
                accountName, new com.bt.nextgen.api.account.v3.model.AccountKey(EncodedString.fromPlainText(accountKey.getId())
                        .toString()));

        if (regularInvestment.getRipStatus() != null) {
            orderGroupDto.setStatus(regularInvestment.getRipStatus().getDisplayName());
        }

        return orderGroupDto;

    }
}
