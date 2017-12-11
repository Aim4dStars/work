package com.bt.nextgen.service.avaloq.regularinvestment;

import com.bt.nextgen.api.order.model.OrderGroupKey;
import com.btfin.panorama.core.security.encryption.EncodedString;
import com.bt.nextgen.service.AvaloqGatewayUtil;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.avaloq.movemoney.RecurringDepositDetailsImpl;
import com.bt.nextgen.service.integration.TransactionStatus;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.movemoney.DepositConverter;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.regularinvestment.RIPStatus;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentDelegateService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentIntegrationService;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentTransaction;
import com.btfin.abs.trxservice.inpay.v1_0.InpayReq;
import com.btfin.abs.trxservice.inpay.v1_0.InpayRsp;
import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("regularInvestmentDelegateService")
public class RegularInvestmentDelegateServiceImpl extends AbstractAvaloqIntegrationService implements
        RegularInvestmentDelegateService {

    @Autowired
    private RegularInvestmentIntegrationService ripService;

    @Autowired
    private RIPTransactionsIntegrationServiceFactory ripTransactionsIntegrationServiceFactory;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private DepositConverter depositConverter;

    @Override
    public RegularInvestment validateRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrorsDD,
            ServiceErrors serviceErrorsRIP) {
        RecurringDepositDetails directDebitResponse = null;
        if (regularInvestment.getDirectDebitDetails() != null) {
            directDebitResponse = this.validateRIPDeposit(regularInvestment.getDirectDebitDetails(), serviceErrorsDD);
        }

        RegularInvestmentImpl ripResponse = (RegularInvestmentImpl) ripService.validateRegularInvestment(regularInvestment,
                serviceErrorsRIP);
        if (ripResponse != null && directDebitResponse != null) {
            ripResponse.setDirectDebitDetails(directDebitResponse);
        }

        return ripResponse;
    }

    @Override
    public RegularInvestment submitRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrorsDD,
            ServiceErrors serviceErrorsRIP) {

        loadExistingRIP(regularInvestment, serviceErrorsDD, serviceErrorsRIP);

        RecurringDepositDetails directDebitResponse = null;
        String linkedDDRef = null;
        if (regularInvestment.getDirectDebitDetails() != null) {
            directDebitResponse = this.submitRIPDeposit(regularInvestment.getDirectDebitDetails(), serviceErrorsDD);
            // If there are service errors with direct debit, return the
            // response without proceeeding further
            if (directDebitResponse != null) {
                if (serviceErrorsDD.getErrorList().iterator().hasNext()) {
                    RegularInvestmentImpl directDebitErrorResponse = new RegularInvestmentImpl();
                    directDebitErrorResponse.setDirectDebitDetails(directDebitResponse);
                    return directDebitErrorResponse;
                }

                linkedDDRef = directDebitResponse.getReceiptNumber();
            }
        }
        RegularInvestmentImpl ripResponse = (RegularInvestmentImpl) ripService.submitRegularInvestment(regularInvestment,
                linkedDDRef, serviceErrorsRIP);
        // If there are service errors with RIP, stop the INPAY
        if (serviceErrorsRIP.getErrorList().iterator().hasNext()) {
            submitStopRIPDepositRequest(directDebitResponse, serviceErrorsDD);
        } else {
            if (directDebitResponse != null) {
                ripResponse.setDirectDebitDetails(directDebitResponse);
            }
        }
        return ripResponse;
    }

    private void loadExistingRIP(RegularInvestment regularInvestment, ServiceErrors serviceErrorsDD,
            ServiceErrors serviceErrorsRIP) {
        if (regularInvestment.getOrderGroupId() != null) {
            RegularInvestment existingRIP = loadRegularInvestment(regularInvestment.getAccountKey(),
                    regularInvestment.getOrderGroupId(), serviceErrorsDD, serviceErrorsRIP);
            if (existingRIP.getDirectDebitDetails() != null) {
                stopExistingDD(serviceErrorsDD, existingRIP);
            }
        }
    }

    /**
     * Stop an existing direct-debit associated with the specified regularInvestment. A STOP INPAY_REQ will be posted only if the
     * status of the regularInvestment is ACTIVE. A Cancelled, Failed or Suspended RIP would have already stopped in direct-debit.
     * 
     * @param serviceErrorsDD
     * @param existingRIP
     */
    protected void stopExistingDD(ServiceErrors serviceErrorsDD, RegularInvestment existingRIP) {
        RecurringDepositDetails dd = existingRIP.getDirectDebitDetails();
        if (dd != null && RIPStatus.ACTIVE == existingRIP.getRIPStatus()) {
            String existingDD = dd.getReceiptNumber();
            if (existingDD != null && !(existingDD.isEmpty())) {
                submitStopRIPDepositRequest(dd, serviceErrorsDD);
            }
        }
    }

    public RegularInvestment suspendRegularInvestment(OrderGroupKey key, ServiceErrors serviceErrors) {

        AccountKey accKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        RegularInvestment regInv = this.loadRegularInvestment(accKey, key.getOrderGroupId(), serviceErrors,
                new ServiceErrorsImpl());
        this.stopExistingDD(serviceErrors, regInv);

        RegularInvestmentImpl ripResponse = (RegularInvestmentImpl) ripService.suspendRegularInvestment(regInv, serviceErrors);
        return ripResponse;
    }

    public RegularInvestment cancelRegularInvestment(OrderGroupKey key, ServiceErrors serviceErrors) {

        AccountKey accKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        RegularInvestment regInv = this.loadRegularInvestment(accKey, key.getOrderGroupId(), serviceErrors,
                new ServiceErrorsImpl());
        // Stop regular-deposit
        this.stopExistingDD(serviceErrors, regInv);

        RegularInvestmentImpl ripResponse = (RegularInvestmentImpl) ripService.cancelRegularInvestment(regInv, serviceErrors);
        return ripResponse;
    }

    @Override
    public RegularInvestment resumeRegularInvestment(OrderGroupKey key, ServiceErrors serviceErrorsDD,
            ServiceErrors serviceErrorsRIP) {

        AccountKey accKey = AccountKey.valueOf(EncodedString.toPlainText(key.getAccountId()));
        RegularInvestment regInv = this.loadRegularInvestment(accKey, key.getOrderGroupId(), serviceErrorsDD, serviceErrorsRIP);

        RecurringDepositDetails directDebitResponse = null;
        String linkedDDRef = null;
        if (regInv.getDirectDebitDetails() != null) {
            RecurringDepositDetailsImpl dd = (RecurringDepositDetailsImpl) regInv.getDirectDebitDetails();
            dd.setTransactionDate(dd.getNextTransactionDate());
            directDebitResponse = submitRIPDeposit(dd, serviceErrorsDD);

            if (serviceErrorsDD.hasErrors()) {
                RegularInvestmentImpl errorResponse = new RegularInvestmentImpl();
                errorResponse.setDirectDebitDetails(directDebitResponse);
                return errorResponse;
            }

            linkedDDRef = directDebitResponse.getReceiptNumber();

            // Update the RIP with the new linked-account id.
            RecurringDepositDetailsImpl ddResult = (RecurringDepositDetailsImpl) regInv.getDirectDebitDetails();

            ddResult.setReceiptNumber(linkedDDRef);

            regInv.setDirectDebitDetails(ddResult);
        }
        RegularInvestmentImpl ripResponse = (RegularInvestmentImpl) ripService.resumeRegularInvestment(regInv, serviceErrorsRIP);
        if (serviceErrorsRIP.hasErrors()) {
            this.submitStopRIPDepositRequest(directDebitResponse, serviceErrorsDD);
        } else {
            ripResponse.setDirectDebitDetails(directDebitResponse);
        }

        return ripResponse;
    }

    @Override
    public List<RegularInvestmentTransaction> loadRegularInvestments(AccountKey accountKey, ServiceErrors serviceErrors,
            String mode) {
        return ripTransactionsIntegrationServiceFactory.getInstance(mode).loadRegularInvestments(accountKey, serviceErrors);
    }

    @Override
    public RegularInvestment loadRegularInvestment(AccountKey accountKey, String ripId, ServiceErrors serviceErrorsDD,
            ServiceErrors serviceErrorsRip) {
        RegularInvestment rip = ripService.loadRegularInvestment(accountKey, ripId, serviceErrorsRip);

        String linkedDDRef = null;
        if (rip != null && rip.getDirectDebitDetails() != null && rip.getDirectDebitDetails().getReceiptNumber() != null) {
            linkedDDRef = rip.getDirectDebitDetails().getReceiptNumber();
            RecurringDepositDetails dd = (RecurringDepositDetails) ripService.loadRecurringDeposit(linkedDDRef, serviceErrorsDD);
            if (dd != null) {
                dd.setNextTransactionDate(rip.getDirectDebitDetails().getNextTransactionDate());
                rip.setDirectDebitDetails(dd);
            }
        }
        return rip;
    }

    public RecurringDepositDetails validateRIPDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RecurringDepositDetails>("validateRIPDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        addRIPMediumId(depositConverter.toValidateRecurringDepositRequest(deposit, serviceErrors)),
                        AvaloqOperation.INPAY_REQ, serviceErrors);
                RecurringDepositDetails directDebitResponse = depositConverter.toValidateRecurringDepositResponse(inpayRsp,
                        serviceErrors);
                return directDebitResponse;
            }
        }.run();

    }

    public RecurringDepositDetails submitRIPDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RecurringDepositDetails>("submitRIPDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        addRIPMediumId(depositConverter.toSubmitRecurringDepositRequest(deposit, serviceErrors)),
                        AvaloqOperation.INPAY_REQ, serviceErrors);
                RecurringDepositDetails directDebitResponse = depositConverter.toSubmitRecurringDepositResponse(inpayRsp,
                        serviceErrors);
                return directDebitResponse;
            }
        }.run();
    }

    /**
     * Stop the specified deposit by posting a INPAY_REQ. Note that if the deposit is already stopped, this will generate an
     * exception from Avaloq.
     * 
     * @param deposit
     * @param serviceErrors
     * @return
     */
    protected TransactionStatus submitStopRIPDepositRequest(final RecurringDepositDetails deposit,
            final ServiceErrors serviceErrors) {
        // STOP Inpay for RIP that is ACTIVE.
        return new IntegrationSingleOperation<TransactionStatus>("stopDeposit", serviceErrors) {
            @Override
            public TransactionStatus performOperation() {
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        addRIPMediumId(depositConverter.toStopDepositRequest(deposit.getPositionId(), serviceErrors)),
                        AvaloqOperation.INPAY_REQ, serviceErrors);

                return depositConverter.toStopDepositResponse(inpayRsp, serviceErrors);
            }
        }.run();
    }

    private InpayReq addRIPMediumId(InpayReq inpayReq) {
        inpayReq.getData().setMediumId(AvaloqGatewayUtil.createExtlIdVal(Constants.AVALOQ_MEDIUM_CODE_RIP));
        return inpayReq;
    }

    @Override
    public RegularInvestment saveRegularInvestment(RegularInvestment regularInvestment, ServiceErrors serviceErrorsDD,
                                                   ServiceErrors serviceErrorsRIP) {

        loadExistingRIP(regularInvestment, serviceErrorsDD, serviceErrorsRIP);

        RecurringDepositDetails directDebitResponse = null;
        String linkedDDRef = null;
        if (regularInvestment.getDirectDebitDetails() != null) {
            directDebitResponse = this.saveRIPDeposit(regularInvestment.getDirectDebitDetails(), serviceErrorsDD);
            // If there are service errors with direct debit, return the
            // response without proceeeding further
            if (directDebitResponse != null) {
                if (serviceErrorsDD.getErrorList().iterator().hasNext()) {
                    RegularInvestmentImpl directDebitErrorResponse = new RegularInvestmentImpl();
                    directDebitErrorResponse.setDirectDebitDetails(directDebitResponse);
                    return directDebitErrorResponse;
                }

                linkedDDRef = directDebitResponse.getReceiptNumber();
            }
        }
        RegularInvestmentImpl ripResponse = (RegularInvestmentImpl) ripService.saveRegularInvestment(regularInvestment,
                linkedDDRef, serviceErrorsRIP);
        // If there are service errors with RIP, stop the INPAY
        if (serviceErrorsRIP.getErrorList().iterator().hasNext()) {
            submitStopRIPDepositRequest(directDebitResponse, serviceErrorsDD);
        } else {
            if (directDebitResponse != null) {
                ripResponse.setDirectDebitDetails(directDebitResponse);
            }
        }
        return ripResponse;
    }

    public RecurringDepositDetails saveRIPDeposit(final RecurringDepositDetails deposit, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RecurringDepositDetails>("saveRIPDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                InpayRsp inpayRsp = webserviceClient.sendToWebService(
                        addRIPMediumId(depositConverter.toSaveRecurringDepositRequest(deposit, serviceErrors)),
                        AvaloqOperation.INPAY_REQ, serviceErrors);
                RecurringDepositDetails directDebitResponse = depositConverter.toSubmitRecurringDepositResponse(inpayRsp,
                        serviceErrors);
                return directDebitResponse;
            }
        }.run();
    }


}
