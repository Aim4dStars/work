package com.bt.nextgen.service.prm.service;


import com.bt.nextgen.api.movemoney.v2.model.DailyLimitDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import javax.servlet.http.HttpServletRequest;
/**
 * Created by L081012 on 5/02/2016.
 */
public interface PrmService  {
    void triggerTwoFactorPrmEvent(String CisKey);
    void triggerRegistrationPrmEvent();
    void triggerMobileChangeServiceOpsPrmEvent(ServiceOpsModel serviceOpsModel);
    void triggerChgPwdPrmEvent(ServiceErrors serviceErrors);
    void triggerIssuePwdServiceOpsPrmEvent(ServiceOpsModel serviceOpsModel);
    void triggerLogOffPrmEvent(HttpServletRequest httpServletRequest, ServiceErrors serviceErrors);
    void triggerAccessBlockPrmEvent(ServiceOpsModel serviceOpsMode, boolean isAccessBlockedl);
    void triggerPayeeEvents(PaymentDto paymentDto);
    void triggerPayeeEvents(ServiceErrors serviceErrors, com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto);
    void triggerAccessUnblockPrmEvent(ServiceOpsModel serviceOpsModel);
    void triggerForgotPasswordPrmEvent();
    void triggerPaymentLimitChangePrmEvent(DailyLimitDto keyedObject);
    void triggerPaymentLimitChangePrmEvent(com.bt.nextgen.api.account.v1.model.DailyLimitDto keyedObject);
}
