package com.bt.nextgen.service.avaloq.regularinvestment;

import com.btfin.panorama.service.avaloq.gateway.AvaloqGatewayHelperService;
import com.btfin.panorama.service.avaloq.gateway.AvaloqOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.AbstractAvaloqIntegrationService;
import com.bt.nextgen.service.integration.account.AccountKey;
import com.bt.nextgen.service.integration.movemoney.RecurringDepositDetails;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestment;
import com.bt.nextgen.service.integration.regularinvestment.RegularInvestmentIntegrationService;

@Service("avaloqRegularInvestmentIntegrationService")
public class AvaloqRegularInvestmentIntegrationServiceImpl extends AbstractAvaloqIntegrationService
        implements RegularInvestmentIntegrationService {

    @Autowired
    private RegularInvestmentConverter regularInvestmentConverter;

    @Autowired
    private AvaloqGatewayHelperService webserviceClient;

    @Autowired
    private RipDepositConverter ripDepositConverter;

    @Override
    public RegularInvestment validateRegularInvestment(final RegularInvestment regularInvestment,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RegularInvestment>("validateRegularInvestment", serviceErrors) {
            @Override
            public RegularInvestment performOperation() {
                String ripId = null;
                if (regularInvestment.getOrderGroupId() != null) {
                    ripId = regularInvestment.getOrderGroupId();
                }
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp ripRsp = webserviceClient.sendToWebService(
                        regularInvestmentConverter.toValidateRIPRequest(regularInvestment, serviceErrors),
                        AvaloqOperation.TRX_BDL_REQ, serviceErrors);

                return new RegularInvestmentImpl(regularInvestmentConverter.toValidateOrderResponse(ripId,
                        regularInvestment.getTransactionSeq(), regularInvestment.getAccountKey(), ripRsp, serviceErrors));
            }
        }.run();
    }

    @Override
    public RegularInvestment loadRegularInvestment(final AccountKey accountKey, final String ripId,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RegularInvestment>("loadRegularInvestment", serviceErrors) {
            @Override
            public RegularInvestment performOperation() {
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp ripRsp = webserviceClient.sendToWebService(
                        regularInvestmentConverter.toLoadRIPRequest(ripId, serviceErrors), AvaloqOperation.TRX_BDL_REQ,
                        serviceErrors);
                return regularInvestmentConverter.toLoadRIPResponse(ripId, null, accountKey, ripRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    public RegularInvestment submitRegularInvestment(final RegularInvestment regularInvestment, final String linkedDDRef,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RegularInvestment>("submitRegularInvestment", serviceErrors) {
            @Override
            public RegularInvestment performOperation() {
                String ripId = null;
                if (regularInvestment.getOrderGroupId() != null) {
                    ripId = regularInvestment.getOrderGroupId();
                }
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp ripRsp = webserviceClient.sendToWebService(
                        regularInvestmentConverter.toSubmitRIPRequest(regularInvestment, linkedDDRef, serviceErrors),
                        AvaloqOperation.TRX_BDL_REQ, serviceErrors);
                return new RegularInvestmentImpl(regularInvestmentConverter.toSubmitOrderResponse(ripId,
                        regularInvestment.getTransactionSeq(), regularInvestment.getAccountKey(), ripRsp, serviceErrors));
            }
        }.run();
    }

    @Override
    public RegularInvestment suspendRegularInvestment(final RegularInvestment regularInvestment,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RegularInvestment>("suspendRegularInvestment", serviceErrors) {
            @Override
            public RegularInvestment performOperation() {

                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp ripRsp = webserviceClient.sendToWebService(
                        regularInvestmentConverter.toSuspendRIPRequest(regularInvestment, serviceErrors),
                        AvaloqOperation.TRX_BDL_REQ, serviceErrors);
                String ripId = regularInvestment.getOrderGroupId();

                return regularInvestmentConverter.toGenericRIPResponse(ripId, regularInvestment.getTransactionSeq(),
                        regularInvestment.getAccountKey(), ripRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    public RegularInvestment resumeRegularInvestment(final RegularInvestment regularInvestment,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RegularInvestment>("resumeRegularInvestment", serviceErrors) {
            @Override
            public RegularInvestment performOperation() {
                String ripId = null;
                if (regularInvestment.getOrderGroupId() != null) {
                    ripId = regularInvestment.getOrderGroupId();
                }

                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp ripRsp = webserviceClient.sendToWebService(
                        regularInvestmentConverter.toResumeRIPRequest(regularInvestment, serviceErrors),
                        AvaloqOperation.TRX_BDL_REQ, serviceErrors);

                return regularInvestmentConverter.toGenericRIPResponse(ripId, regularInvestment.getTransactionSeq(),
                        regularInvestment.getAccountKey(), ripRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    public RegularInvestment cancelRegularInvestment(final RegularInvestment regularInvestment,
            final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RegularInvestment>("cancelRegularInvestment", serviceErrors) {
            @Override
            public RegularInvestment performOperation() {

                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp ripRsp = webserviceClient.sendToWebService(
                        regularInvestmentConverter.toCancelRIPRequest(regularInvestment, serviceErrors),
                        AvaloqOperation.TRX_BDL_REQ, serviceErrors);
                regularInvestmentConverter.processDeleteResponse(ripRsp, serviceErrors);

                return regularInvestmentConverter.toGenericRIPResponse(regularInvestment.getOrderGroupId(),
                        regularInvestment.getTransactionSeq(), regularInvestment.getAccountKey(), ripRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    public RecurringDepositDetails loadRecurringDeposit(final String linkedDDRef, final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RecurringDepositDetails>("loadRecurringDeposit", serviceErrors) {
            @Override
            public RecurringDepositDetails performOperation() {
                com.btfin.abs.trxservice.inpay.v1_0.InpayRsp ddRsp = webserviceClient.sendToWebService(
                        ripDepositConverter.toLoadDDRequest(linkedDDRef), AvaloqOperation.INPAY_REQ, serviceErrors);
                return ripDepositConverter.toLoadDDResponse(linkedDDRef, ddRsp, serviceErrors);
            }
        }.run();
    }

    @Override
    public RegularInvestment saveRegularInvestment(final RegularInvestment regularInvestment, final String linkedDDRef,
                                                   final ServiceErrors serviceErrors) {
        return new IntegrationSingleOperation<RegularInvestment>("submitRegularInvestment", serviceErrors) {
            @Override
            public RegularInvestment performOperation() {
                String ripId = null;
                if (regularInvestment.getOrderGroupId() != null) {
                    ripId = regularInvestment.getOrderGroupId();
                }
                com.btfin.abs.trxservice.trxbdl.v1_0.TrxBdlRsp ripRsp = webserviceClient.sendToWebService(
                        regularInvestmentConverter.toSaveRIPRequest(regularInvestment, linkedDDRef, serviceErrors),
                        AvaloqOperation.TRX_BDL_REQ, serviceErrors);
                return new RegularInvestmentImpl(regularInvestmentConverter.toSubmitOrderResponse(ripId,
                        regularInvestment.getTransactionSeq(), regularInvestment.getAccountKey(), ripRsp, serviceErrors));
            }
        }.run();
    }

}
