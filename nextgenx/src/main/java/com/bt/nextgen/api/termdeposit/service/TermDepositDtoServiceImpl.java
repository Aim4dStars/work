package com.bt.nextgen.api.termdeposit.service;

import com.bt.nextgen.api.termdeposit.model.TermDepositDetailDto;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.bankdate.BankDateIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDepositIntegrationService;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrx;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxRequest;
import com.bt.nextgen.service.integration.termdeposit.TermDepositTrxRequestImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional(value = "springJpaTransactionManager")
public class TermDepositDtoServiceImpl implements TermDepositDtoService {
    @Autowired
    private TermDepositIntegrationService avaloqTermDepositIntegrationService;

    @Autowired
    private BankDateIntegrationService bankDateIntegrationService;

    @Override
    public TermDepositDetailDto validate(TermDepositDetailDto termDepositDto, ServiceErrors serviceErrors) {
        TermDepositTrxRequest validateTDBreakReq = new TermDepositTrxRequestImpl();
        validateTDBreakReq.setAsset(EncodedString.toPlainText(termDepositDto.getTdAccountId()));
        validateTDBreakReq.setPortfolio(EncodedString.toPlainText(termDepositDto.getKey().getAccountId()));
        TermDepositTrx termDeposit = avaloqTermDepositIntegrationService.validateBreakTermDeposit(validateTDBreakReq,
                serviceErrors);
        TermDepositDetailDto validateTermDepositDto = toTDBreakDto(termDepositDto.getTdAccountId(), termDeposit, serviceErrors);
        handleErrors(serviceErrors, validateTermDepositDto);
        return validateTermDepositDto;
    }

    protected TermDepositDetailDto toTDBreakDto(String accountId, TermDepositTrx termDeposit, ServiceErrors serviceErrors) {
        TermDepositDetailDto tdBreakDto = new TermDepositDetailDto();
        tdBreakDto.setTdAccountId(accountId);
        tdBreakDto.setOpenDate(termDeposit.getOpenDate()); //openDate
        tdBreakDto.setInvestmentAmount(termDeposit.getWidrwPrpl());//principal
        tdBreakDto.setDaysLeft(termDeposit.getDaysUntilMaturity());//Days until Maturity
        tdBreakDto.setPercentageTermElapsed(termDeposit.getPercentTermElapsed());//% of term elapsed
        tdBreakDto.setMaturityDate(termDeposit.getMaturityDate());//Maturity date
        tdBreakDto.setInterestPaid(termDeposit.getInterestPaid());//Interest paid
        tdBreakDto.setInterestAccrued(termDeposit.getInterestAccrued());//Interest accrued since last payment
        tdBreakDto.setInterestRate(termDeposit.getInterestRate());//Interest rate
        tdBreakDto.setWithdrawNet(termDeposit.getWithdrawNet());//Total principal interest paid at withdrawal
        tdBreakDto.setAdjustedInterestAmt((termDeposit.getWithdrawInterestPaid() != null && termDeposit.getInterestPaid() != null)
                ? termDeposit.getWithdrawInterestPaid().subtract(termDeposit.getInterestPaid()) : BigDecimal.ZERO);//Adjusted Interest
        tdBreakDto.setWithdrawDate(termDeposit.getWithdrawDate());//Date term deposit will withdraw
        tdBreakDto.setAdjustedInterestRate(termDeposit.getAdjustedInterestRate());//Adjusted Interest rate
        tdBreakDto.setAvaloqDate(bankDateIntegrationService.getBankDate(serviceErrors).toString());
        tdBreakDto.setNoticeEndDate(termDeposit.getNoticeEndDate());
        return tdBreakDto;
    }

    @Override
    public TermDepositDetailDto submit(TermDepositDetailDto termDepositDto, ServiceErrors serviceErrors) {
        TermDepositDetailDto submitTermDepositDto = new TermDepositDetailDto();
        TermDepositTrxRequest submitTDBreakReq = new TermDepositTrxRequestImpl();
        submitTDBreakReq.setAsset(EncodedString.toPlainText(termDepositDto.getTdAccountId()));
        submitTDBreakReq.setPortfolio(EncodedString.toPlainText(termDepositDto.getKey().getAccountId()));
        TermDepositTrx termDepositTrx = avaloqTermDepositIntegrationService.submitBreakTermDeposit(submitTDBreakReq,
                serviceErrors);
        submitTermDepositDto = toTDBreakDto(termDepositDto.getTdAccountId(), termDepositTrx, serviceErrors);
        handleErrors(serviceErrors, submitTermDepositDto);
        return submitTermDepositDto;
    }

    public TermDepositDetailDto handleErrors(ServiceErrors errors, TermDepositDetailDto termDepositDto) {
        Iterator<ServiceError> serviceError = errors.getErrorList().iterator();
        List<ServiceError> errorList = new ArrayList<>();
        while (serviceError.hasNext()) {
            ServiceError serror;
            serror = serviceError.next();
            errorList.add(serror);
            termDepositDto.setErrors(errorList);
        }
        return termDepositDto;
    }
}
