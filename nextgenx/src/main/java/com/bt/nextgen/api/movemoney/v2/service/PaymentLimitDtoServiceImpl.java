package com.bt.nextgen.api.movemoney.v2.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bt.nextgen.service.prm.service.PrmService;
import com.btfin.panorama.service.avaloq.pasttransaction.TransactionOrderType;
import com.btfin.panorama.service.avaloq.pasttransaction.TransactionType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.bt.nextgen.api.movemoney.v2.model.DailyLimitDto;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.service.ServiceError;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.avaloq.wrapaccount.WrapAccountIdentifierImpl;
import com.bt.nextgen.service.integration.CurrencyType;
import com.bt.nextgen.service.integration.account.AccountIntegrationService;
import com.bt.nextgen.service.integration.account.UpdateAccountDetailResponse;
import com.bt.nextgen.service.integration.account.UpdatePaymentLimitRequest;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetails;
import com.bt.nextgen.service.integration.payeedetails.PayeeDetailsIntegrationService;
import com.bt.nextgen.service.integration.payeedetails.PayeeLimit;
import com.btfin.panorama.service.integration.wrapaccount.WrapAccountIdentifier;
import com.bt.nextgen.web.validator.ValidationErrorCode;

@Service("PaymentLimitDtoServiceV2")
@SuppressWarnings("all")
public class PaymentLimitDtoServiceImpl implements PaymentLimitDtoService {

    @Autowired
    @Qualifier("avaloqAccountIntegrationService")
    private AccountIntegrationService avaloqAccountIntegrationServiceImpl;

    @Autowired
    private PayeeDetailsIntegrationService payeeDetailsIntegrationService;

    @Autowired
    public PrmService prmService;

    private DailyLimitDto toLimitDto(PayeeDetails payeeDetails) {
        DailyLimitDto dailyLimitDto = new DailyLimitDto();

        if(null!= payeeDetails.getPayeeLimits()) {
          for (PayeeLimit payeeLimit : payeeDetails.getPayeeLimits()) {
              if (null != payeeLimit.getRemainingLimit()) {
                  if (payeeLimit.getOrderType().getName().equalsIgnoreCase(TransactionOrderType.BPAY.toString()))
                      dailyLimitDto.setRemainingBpayLimit(new BigDecimal(deformatCurrency(payeeLimit.getRemainingLimit())));
                  else if (payeeLimit.getOrderType().getName().equalsIgnoreCase(TransactionOrderType.PAY_ANYONE.toString()))
                      dailyLimitDto.setRemainingPayAnyoneLimit(new BigDecimal(deformatCurrency(payeeLimit.getRemainingLimit())));
              }
              if (null!= payeeLimit.getOrderType() && payeeLimit.getOrderType().getName().equalsIgnoreCase(TransactionOrderType.BPAY.toString()))
                  dailyLimitDto.setBpayLimit(new BigDecimal((deformatCurrency((payeeLimit.getLimitAmount())))));
              else if (null!= payeeLimit.getOrderType() && payeeLimit.getOrderType().getName().equalsIgnoreCase(TransactionOrderType.PAY_ANYONE.toString()))
                  dailyLimitDto.setPayAnyoneLimit(new BigDecimal((deformatCurrency(payeeLimit.getLimitAmount()))));

          }
      }
        dailyLimitDto.setLinkedLimit(new BigDecimal(deformatCurrency(payeeDetails.getMaxDailyLimit())));
        dailyLimitDto.setRemainingLinkedLimit(new BigDecimal(deformatCurrency(payeeDetails.getMaxDailyLimit())));
        dailyLimitDto.setMaxLimit(new BigDecimal(payeeDetails.getMaxDailyLimit()));

        return dailyLimitDto;
    }

    public static String deformatCurrency(String amount) {
        if (StringUtils.isBlank(amount))
            return "0";
        return amount.replaceAll("[\\$\\,\\,]", "").trim();
    }

    @Override
    public DailyLimitDto submit(DailyLimitDto keyedObject, ServiceErrors serviceErrors) {

        return updateDailyLimit(keyedObject, serviceErrors);
    }

    public DailyLimitDto updateDailyLimit(DailyLimitDto keyedObject, ServiceErrors serviceErrors) {
        try {
            // Get Modification Seq Number
            WrapAccountIdentifier wrapAccountIdentifier = new WrapAccountIdentifierImpl();
            wrapAccountIdentifier.setBpId(keyedObject.getKey().getAccountId());
            PayeeDetails payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifier, serviceErrors);
            //to store Previous Payment Limit
            if(null != payeeDetails.getPayeeLimits()) {
                for (PayeeLimit payeeLimit : payeeDetails.getPayeeLimits()) {
                    if (null!=payeeLimit.getOrderType()&& keyedObject.getPayeeType().equals(payeeLimit.getOrderType().name())) {
                        keyedObject.setPreviousAmount(payeeLimit.getLimitAmount());
                    }
                }
            }

            UpdatePaymentLimitRequest updatePaymentLimitRequest = new BillerReqImpl();

            com.bt.nextgen.service.integration.account.AccountKey accountKey = com.bt.nextgen.service.integration.account.AccountKey
                    .valueOf(keyedObject.getKey().getAccountId());
            updatePaymentLimitRequest.setAccountKey(accountKey);
            updatePaymentLimitRequest.setAmount(keyedObject.getLimit());
            updatePaymentLimitRequest.setModificationIdentifier(payeeDetails.getModifierSeqNumber());
            updatePaymentLimitRequest.setCurrency(CurrencyType.AustralianDollar);

            if (keyedObject.getPayeeType().equalsIgnoreCase("BPAY"))
                updatePaymentLimitRequest.setBusinessTransactionOrderType(TransactionOrderType.BPAY);
            else if (keyedObject.getPayeeType().equalsIgnoreCase("PAY_ANYONE"))
                updatePaymentLimitRequest.setBusinessTransactionOrderType(TransactionOrderType.PAY_ANYONE);

            updatePaymentLimitRequest.setCurrency(CurrencyType.AustralianDollar);
            updatePaymentLimitRequest.setBusinessTransactionType(TransactionType.PAY);
            updatePaymentLimitRequest.setModificationIdentifier(payeeDetails.getModifierSeqNumber());
            UpdateAccountDetailResponse response = avaloqAccountIntegrationServiceImpl
                    .updatePaymentLimit(updatePaymentLimitRequest, serviceErrors);

            if (response.isUpdatedFlag()) {
                // Get latest Limit from PAY_DET Service
                wrapAccountIdentifier.setBpId(keyedObject.getKey().getAccountId());
                payeeDetailsIntegrationService.clearCache(wrapAccountIdentifier);
                payeeDetails = payeeDetailsIntegrationService.loadPayeeDetails(wrapAccountIdentifier, serviceErrors);
                //trigger ChangeLimit PrmEvent
                prmService.triggerPaymentLimitChangePrmEvent(keyedObject);
                keyedObject = toLimitDto(payeeDetails);
                keyedObject.setIsLimitUpdated(Boolean.TRUE.toString());
            }
        } catch (Exception ex) {
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, ValidationErrorCode.SYSTEM_UNAVAILABLE);
        }

        return this.handleErrors(serviceErrors, keyedObject);
    }

    public DailyLimitDto handleErrors(ServiceErrors errors, DailyLimitDto keyedObject) {

        Iterator<ServiceError> serviceError = errors.getErrorList().iterator();
        List<ServiceError> errorList = new ArrayList<>();
        while (serviceError.hasNext()) {
            ServiceError serror;
            serror = (ServiceError) serviceError.next();
            errorList.add(serror);
            keyedObject.setErrors(errorList);
        }
        return keyedObject;
    }
}
