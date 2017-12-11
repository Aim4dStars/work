package com.bt.nextgen.service.prm.service;

import com.bt.nextgen.api.movemoney.v2.model.DailyLimitDto;
import com.bt.nextgen.api.movemoney.v2.model.PayeeDto;
import com.bt.nextgen.api.movemoney.v2.model.PaymentDto;
import com.bt.nextgen.core.security.api.service.PermissionBaseDtoService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.domain.IndividualDetailImpl;
import com.bt.nextgen.service.integration.client.ClientIntegrationService;
import com.bt.nextgen.service.integration.domain.AddressMedium;
import com.bt.nextgen.service.integration.domain.Phone;
import com.bt.nextgen.service.integration.userinformation.ClientKey;
import com.bt.nextgen.service.prm.pojo.PrmDto;
import com.bt.nextgen.service.prm.pojo.PrmEventType;
import com.bt.nextgen.service.prm.util.PrmUtil;
import com.bt.nextgen.service.security.converter.HttpRequestConverter;
import com.bt.nextgen.service.security.model.HttpRequestParams;
import com.bt.nextgen.serviceops.model.ServiceOpsModel;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.bt.nextgen.payments.domain.PayeeType;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

/**
 * Created by L081012-Rishi Gupta on 5/02/2016.
 */

@Service
@SuppressWarnings({ "squid:MethodCyclomaticComplexity", "squid:S1200" })
public class PrmServiceImpl implements PrmService {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    private static final String RISK_MSG_TYPE = "0110";
    private static final String TRAN_CODE = "Avaloq";
    private static final String BANK_ID = "WBC";
    private static final String CHANNEL_ID = "AVL";
    private static final String TWOFA_DEVICE_TYPE = "SMS";
    private static final String TWOFA_DEVICE_STATUS = "ACTIVE";
    private static final BigInteger MSG_RCRD_SRC_ID = BigInteger.valueOf(17);
    private static final String BLOCK_OPERATOR = "Blocked By Operator";
    private static final String TRUE = "True";
    private static final String FALSE = "False";


    private enum InitiatorType {
        STAFF("STAFF"), CUSTOMER("CUSTOMER");
        private final String value;

        private InitiatorType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return this.value;
        }
    }

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    @Qualifier("avaloqClientIntegrationService")
    private ClientIntegrationService clientIntegrationService;

    @Autowired
    private PrmGESBConnectService prmConnectService;

    @Autowired
    private PermissionBaseDtoService permissionBaseService;

    /*
    * This event is triggered when user confirms mobile number at first time login.
    * */
    @Override
    public void triggerTwoFactorPrmEvent(String CisKey) {
        logger.info("Started triggering 2FA SMS Active PRM event");
        try {
            PrmDto prmDto = getBasicDto();
            prmDto.setEventType(PrmEventType.ACTIVE);
            prmDto.setUtilString8(populateDeviceNumber());
            prmDto.setUtilString7(TWOFA_DEVICE_TYPE);
            prmDto.setUtilString9(TWOFA_DEVICE_STATUS);
            if(null != CisKey) {
                prmDto.setCustomerCisKey(CisKey);
                prmDto.setProfileOwner(BANK_ID+CisKey);
            }
            logger.info("Enter triggerTwoFactorPrmEvent()");
            // check to confirm if CIS key is available for customer else prm event is not triggered
            if ( null != prmDto.getCustomerCisKey() ) {
                prmConnectService.submitRequest(prmDto);
                logger.info("2FA SMS Active PRM event triggered successfully ");
            }else{
                logger.info("Customer CIS Key Is Null Hence not triggering 2FA SMS Active PRM event");
            }
            logger.info("Exit triggerTwoFactorPrmEvent()");
        } catch (Exception ae) {
            logger.error("Error in sending PRM 2fa event : ", ae);
        }
    }

    /*
    * This event is triggered when user confirms mobile number at first time login.
    * */
    @Override
    public void triggerRegistrationPrmEvent() {
        logger.info("Started triggering Registration PRM event");
        try {
            PrmDto prmDto = getBasicDto();
            prmDto.setEventType(PrmEventType.REGISTRATION);
            logger.info("Enter RegistrationPrmEvent()");
            // check to confirm if CIS key is available for customer else prm event is not triggered
            if ( null != prmDto.getCustomerCisKey() ) {
                prmConnectService.submitRequest(prmDto);
                logger.info("Registration PRM event triggered successfully ");
            } else {
                logger.info("Customer CIS Key Is Null Hence not triggering Registration PRM event");
            }
            logger.info("Exit RegistrationPrmEvent()");
        } catch (Exception ae) {
            logger.error("Error in sending PRM Registration event : ", ae);
        }
    }

    /*
    * This event is triggered when primary mobile number is changed from ServiceOPS desktop.
    * */
    @Override
    public void triggerMobileChangeServiceOpsPrmEvent(ServiceOpsModel serviceOpsModel) {
        logger.info("Started triggering Non-value 2FA Mobile Number Change");

        try {
            PrmDto prmDto = buildPrmDto();

            if (null != serviceOpsModel) {
                prmDto.setEventType(PrmEventType.MOBILECHANGE);
                if (StringUtils.isEmpty(serviceOpsModel.getUserId())) {
                    prmDto.setUserId(serviceOpsModel.getUserId());
                }
                prmDto.setUtilString8(serviceOpsModel.getMobileNumber());
                String cisKey = serviceOpsModel.getCisId();
                prmDto.setCustomerCisKey(cisKey);
                prmDto.setEventInitiatorType(getInitiatorType());
                prmDto.setProfileOwner(BANK_ID + cisKey);
            }
            logger.info("Enter triggerMobileChangeServiceOpsPrmEvent()");
            // check to confirm if CIS key is available for customer else prm event is not triggered
            if ( null != prmDto.getCustomerCisKey() ) {
                prmConnectService.submitRequest(prmDto);
                logger.info("Non-value 2FA Mobile Number Change PRM event triggered successfully");
            } else {
                logger.info("Customer CIS Key Is Null Hence not triggering Non-value 2FA Mobile Number Change PRM event");
            }
            logger.info("Exit triggerMobileChangeServiceOpsPrmEvent()");
        } catch (Exception ae) {
            logger.error("Error in sending PRM service ops 2fa event : ", ae);
        }
    }

    /**
     * Add Payee Event
     * Update Payee Event
     * Delete Payee Event
     * <p>
     * Payee => ( BSB, Linked, Anyone )
     *
     * Picking nickname instead of accountName for add Payee event for payee type BPAY
     **/
    @Override
    public void triggerPayeeEvents(PaymentDto paymentDto) {
        if (permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")) {
            try {
                logger.info("Started triggering payee PRM event", paymentDto);
                PrmDto prmDto = getBasicDto();
                PayeeType payeeType = null;
                PayeeDto payeeDto = paymentDto.getToPayeeDto();
                if (payeeDto != null) {
                    if (!StringUtils.isEmpty(payeeDto.getAccountId())) {
                        prmDto.setUtilString6(payeeDto.getAccountId());
                    }
                    if (!StringUtils.isEmpty(payeeDto.getCode())) {
                        prmDto.setUtilString5(payeeDto.getCode());
                    }
                    if (!StringUtils.isEmpty(payeeDto.getAccountName())) {
                        prmDto.setUtilString4(payeeDto.getAccountName());
                    }
                }
                if (null != payeeDto.getPayeeType()) {
                    payeeType = PayeeType.valueOf(paymentDto.getToPayeeDto().getPayeeType());
                    if (null != paymentDto.getOpType() && !paymentDto.getOpType().isEmpty()) {
                        setPayeeData(prmDto, payeeDto, payeeType);
                        switch (paymentDto.getOpType()) {
                            case Attribute.ADD:
                                if (payeeType.equals(PayeeType.BPAY) && !StringUtils.isEmpty(payeeDto.getNickname())) {
                                    prmDto.setUtilString4(payeeDto.getNickname());
                                }
                                prmDto.setEventType(setAddPayeeEventType(payeeType));
                                break;
                            case Attribute.UPDATE:
                                prmDto.setEventType(setUpdatePayeeEventType(payeeType));
                                break;
                            case Attribute.DELETE:
                                prmDto.setEventType(setDeletePayeeEventType(payeeType));
                                break;
                        }
                    }
                }
                logger.info("Enter Payee Event triggerPayeeEvents()");
                // check to confirm if CIS key is available for customer else prm event is not triggered
                if ( null != prmDto.getCustomerCisKey() ) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info("triggerPayeeEvents() PRM event successfully triggered");
                } else {
                    logger.info("Customer CIS Key Is Null Hence not triggering triggerPayeeEvents() event");
                }
                logger.info("Exit Payee Event triggerPayeeEvents()");
            } catch (Exception ae) {
                logger.error("Error in sending PRM payee event : ", ae);
            }
        }
    }

    private void setPayeeData(PrmDto prmDto, PayeeDto payeeDto, PayeeType payeeType) {
        if (payeeType.equals(PayeeType.BPAY)) {
            prmDto.setUtilString6(payeeDto.getCode());
            prmDto.setUtilString5("");
            prmDto.setUtilString10("BPAY");
        } else if (payeeType.equals(PayeeType.PAY_ANYONE)) {
            prmDto.setUtilString10("THIRD PARTY TRANSFER");
        } else if (payeeType.equals(PayeeType.LINKED)) {
            prmDto.setUtilString10("");
        }
    }

    private PrmEventType setAddPayeeEventType(PayeeType payeeType) {
        PrmEventType prmEventType = null;
        if (payeeType.equals(PayeeType.BPAY)) {
            prmEventType = PrmEventType.ADDBPAY;
        } else if (payeeType.equals(PayeeType.PAY_ANYONE)) {
            prmEventType = PrmEventType.ADDACCOUNT;
        } else if (payeeType.equals(PayeeType.LINKED)) {
            prmEventType = PrmEventType.ADDLINKED;
        }
        return prmEventType;
    }

    private PrmEventType setUpdatePayeeEventType(PayeeType payeeType) {
        PrmEventType prmEventType = null;
        if (payeeType.equals(PayeeType.BPAY)) {
            prmEventType = PrmEventType.UPDATEBPAY;
        } else if (payeeType.equals(PayeeType.PAY_ANYONE)) {
            prmEventType = PrmEventType.UPDATEACCOUNT;
        } else if (payeeType.equals(PayeeType.LINKED)) {
            prmEventType = PrmEventType.UPDATELINKED;
        }
        return prmEventType;
    }

    private PrmEventType setDeletePayeeEventType(PayeeType payeeType) {
        PrmEventType prmEventType = null;
        if (payeeType.equals(PayeeType.BPAY)) {
            prmEventType = PrmEventType.DELETEBPAY;
        } else if (payeeType.equals(PayeeType.PAY_ANYONE)) {
            prmEventType = PrmEventType.DELETEACCOUNT;
        } else if (payeeType.equals(PayeeType.LINKED)) {
            prmEventType = PrmEventType.DELETELINKED;
        }
        return prmEventType;
    }

    /*
    * For Deprecated Method of Payment Service v1
    *
    * Picking nickname instead of accountName for add Payee event for payee type BPAY
    * */
    public void triggerPayeeEvents(ServiceErrors serviceErrors, com.bt.nextgen.api.account.v1.model.PaymentDto paymentDto) {
        if (permissionBaseService.hasBasicPermission("feature.global.prmNonValueEvents")) {
            try {
                logger.info("Started triggering payee PRM event");
                PrmDto prmDto = getBasicDto();
                PayeeType payeeType = null;
                com.bt.nextgen.api.account.v1.model.PayeeDto payeeDto = paymentDto.getToPayteeDto();
                if (payeeDto != null) {
                    if (!StringUtils.isEmpty(payeeDto.getAccountId())) {
                        prmDto.setUtilString6(payeeDto.getAccountId());
                    }
                    if (!StringUtils.isEmpty(payeeDto.getCode())) {
                        prmDto.setUtilString5(payeeDto.getCode());
                    }
                    if (!StringUtils.isEmpty(payeeDto.getAccountName())) {
                        prmDto.setUtilString4(payeeDto.getAccountName());
                    }
                }
                if (null != payeeDto.getPayeeType()) {
                    payeeType = PayeeType.valueOf(paymentDto.getToPayteeDto().getPayeeType());
                    if (null != paymentDto.getOpType() && !paymentDto.getOpType().isEmpty() && payeeType != null) {
                        setPayeeData(prmDto, payeeDto, payeeType);
                        switch (paymentDto.getOpType()) {
                            case Attribute.ADD:
                                if (payeeType.equals(PayeeType.BPAY) && !StringUtils.isEmpty(payeeDto.getNickname())) {
                                    prmDto.setUtilString4(payeeDto.getNickname());
                                }
                                prmDto.setEventType(setAddPayeeEventType(payeeType));
                                break;
                            case Attribute.UPDATE:
                                prmDto.setEventType(setUpdatePayeeEventType(payeeType));
                                break;
                            case Attribute.DELETE:
                                prmDto.setEventType(setDeletePayeeEventType(payeeType));
                                break;
                        }
                    }
                }
                logger.info("Enter Payee Event triggerPayeeEvents()");
                // check to confirm if CIS key is available for customer else prm event is not triggered
                if ( null != prmDto.getCustomerCisKey() ) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info("Payee Event triggerPayeeEvents() triggered successfully");
                } else {
                    logger.info("Customer CIS Key Is Null Hence not triggering triggerPayeeEvents() event");
                }
                logger.info("Exit Payee Event triggerPayeeEvents()");
            } catch (Exception ae) {
                logger.error("Error in sending PRM payee event : ", ae);
            }
        }
    }

    @Override
    public void triggerAccessUnblockPrmEvent(ServiceOpsModel serviceOpsModel) {
        try {
            logger.info("Started triggering Access UnBlock PRM event");
            PrmDto prmDto = getBasicDto();
            if (prmDto != null && serviceOpsModel != null) {
                prmDto.setEventType(PrmEventType.ACCESSUNBLOCK);
                prmDto.setEventInitiatorType(getInitiatorType());
                String cisKey = serviceOpsModel.getCisId();
                prmDto.setCustomerCisKey(cisKey);
                prmDto.setUserId(serviceOpsModel.getUserId());
                prmDto.setProfileOwner(BANK_ID + cisKey);
                if (null != userProfileService) {
                    prmDto.setUtilString3(userProfileService.getUserId());
                }
                logger.info("Enter access unblock event triggerAccessUnblockPrmEvent()");
                // check to confirm if CIS key is available for customer else prm event is not triggered
                if ( null != prmDto.getCustomerCisKey() ) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info("Access UnBlock PRM event triggered successfully ");
                } else {
                    logger.info("Customer CIS Key Is Null Hence not triggering Customer Sign In Unlocked PRM event");
                }
                logger.info("Exit PRM access UnBlock  PrmEvent()");
            }
        } catch (Exception ae) {
            logger.error("Error in sending PRM access UnBlock event : ", ae);
        }
    }

    @Override
    public void triggerForgotPasswordPrmEvent() {
        logger.info("Started Trggerring Forgot Password PRM Event");
        try {
            PrmDto prmDto = getBasicDto();
            if (null != prmDto) {
                prmDto.setEventType(PrmEventType.FORGOTPASSWORD);
                prmDto.setEventInitiatorType(getInitiatorType());
                logger.info("Enter ForgotPassword PRM Event triggerForgotPasswordPrmEvent()");
                // check to confirm if CIS key is available for customer else prm event is not triggered
                if ( null != prmDto.getCustomerCisKey() ) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info("ForgotPassword PRM Event Triggered Successfully");
                } else {
                    logger.info("Customer CIS Key Is Null Hence not triggering ForgotPassword PRM event");
                }
                logger.info("Exit ForgotPassword PRM Event triggerForgotPasswordPrmEvent()");
            }
        } catch (Exception ae) {
            logger.error("Error in sending Forgotten Password PRM event : ", ae);
        }
    }

    @Override
    public void triggerPaymentLimitChangePrmEvent(DailyLimitDto keyedObject) {
        logger.info("Started Triggerring Payment Limit Change PRM Event");
        try {
            PrmDto prmDto = getBasicDto();
            if (null != prmDto) {
                prmDto.setUtilString5(keyedObject.getPreviousAmount());
                prmDto.setUtilString6(keyedObject.getLimit().toString());
                prmDto.setUtilString10(keyedObject.getPayeeType());
                if(PayeeType.valueOf(keyedObject.getPayeeType()).equals(PayeeType.PAY_ANYONE)) {
                    prmDto.setUtilString10("THIRD PARTY TRANSFER");
                }
                prmDto.setEventType(PrmEventType.INCLIMIT);
                logger.info("Enter Payment Limit Change PRM Event triggerPaymentLimitChangePrmEvent()");
                // check to confirm if CIS key is available for customer else prm event is not triggered
                if( null !=  prmDto.getCustomerCisKey()) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info("Payment Limit Change PRM Event Triggerred Successfully");
                } else {
                    logger.info("Customer CIS Key Is Null Hence not triggering Payment Limit Change PRM event");
                }
                logger.info("Exit Payment Limit Change PRM Event triggerPaymentLimitChangePrmEvent()");
            }
        } catch ( Exception ae) {
            logger.error("Error in Payment Limit Change PRM event : ", ae);
        }
    }

    @Override
    public void triggerPaymentLimitChangePrmEvent(com.bt.nextgen.api.account.v1.model.DailyLimitDto keyedObject) {
        logger.info("Started Triggerring Payment Limit Change PRM Event");
        try {
            PrmDto prmDto = getBasicDto();
            if(null != prmDto) {
                prmDto.setUtilString4(keyedObject.getPreviousAmount());
                prmDto.setUtilString5(keyedObject.getLimit().toString());
                prmDto.setUtilString10(keyedObject.getPayeeType());
                if(PayeeType.valueOf(keyedObject.getPayeeType()).equals(PayeeType.PAY_ANYONE)) {
                    prmDto.setUtilString10("THIRD PARTY TRANSFER");
                }
                prmDto.setEventType(PrmEventType.INCLIMIT);
                logger.info("Enter Payment Limit Change PRM Event triggerPaymentLimitChangePrmEvent()");
                // check to confirm if CIS key is available for customer else prm event is not triggered
                if ( null != prmDto.getCustomerCisKey()) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info("Payment Limit Change PRM Event Triggerred Successfully");
                } else {
                    logger.info("Customer CIS Key Is Null Hence not triggering Payment Limit Change PRM event");
                }
                logger.info("Exit Payment Limit Change PRM Event triggerPaymentLimitChangePrmEvent()");
            }
        } catch (Exception ae) {
            logger.error("Error in Payment Limit Change PRM Event : ", ae);
        }
    }

    private void setPayeeData(PrmDto prmDto, com.bt.nextgen.api.account.v1.model.PayeeDto payeeDto, PayeeType payeeType) {
        if (payeeType.equals(PayeeType.BPAY)) {
            prmDto.setUtilString6(payeeDto.getCode());
            prmDto.setUtilString5("");
            prmDto.setUtilString10("BPAY");
        } else if (payeeType.equals(PayeeType.PAY_ANYONE)) {
            prmDto.setUtilString10("THIRD PARTY TRANSFER");
        } else if (payeeType.equals(PayeeType.LINKED)) {
            prmDto.setUtilString10("");
        }
    }

    /*
    * Logout event for Panorama. new Service interface before logging out to send event to PRM
    * */
    @Override
    public void triggerLogOffPrmEvent(HttpServletRequest httpServletRequest, ServiceErrors serviceErrors) {
        try {
            logger.info("Started triggering:" + PrmEventType.PRMLOGOUT + " : PRM event :");
            PrmDto prmDto = null;
            prmDto = getBasicDto(httpServletRequest);
            if (null != prmDto) {
                prmDto.setEventType(PrmEventType.PRMLOGOUT);
                prmDto.setEventInitiatorType(getInitiatorType());
                if(InitiatorType.STAFF.toString().equalsIgnoreCase(getInitiatorType())) {
                    prmDto.setUtilString3(userProfileService.getUserId());
                }
                if(null!=prmDto.getCustomerCisKey()) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info(PrmEventType.PRMLOGOUT + " : PRM event triggered successfully ");
                }
                else{
                    logger.info("Customer CIS Key Is Null Hence not triggerring this event");
                }
            }

        } catch (Exception ae) {
            logger.error("Error in sending PRM logoff event : ", ae);
        }

    }

    /*
    * This event is generated when investor or adviser change password from Your details link.
    * */
    @Override
    public void triggerChgPwdPrmEvent(ServiceErrors serviceErrors) {
        logger.info("Started triggering Prm in session Change password PRM event :");
        try {
            PrmDto prmDto = getBasicDto();
            if (userProfileService != null) {
                prmDto.setEventType(PrmEventType.PWDCHANGE);
                prmDto.setEventInitiatorType(getInitiatorType());
                logger.info("Enter Change Pwd event triggerChgPwdPrmEvent()");
                // check to confirm if CIS key is available for customer else prm event is not triggered
                if ( null != prmDto.getCustomerCisKey()) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info("Change Pwd event triggered successfully ");
                } else {
                    logger.info("Customer CIS Key Is Null Hence not triggering Change Pwd PRM event");
                }
            logger.info("Exit Change Pwd event triggerChgPwdPrmEvent()");
            }
        } catch (Exception ae) {
            logger.error("Error in sending PRM Change pwd event : ", ae);
        }
    }

    /*
    * This event occurs when new password is issued from Service OPS
    *
    */
    @Override
    public void triggerIssuePwdServiceOpsPrmEvent(ServiceOpsModel serviceOpsModel) {
        logger.info("Started triggering service ops temp pwd PRM event");
        try {
            PrmDto prmDto = buildPrmDto();

            if (null != serviceOpsModel) {
                prmDto.setUserId(serviceOpsModel.getUserName());
            }

            prmDto.setEventType(PrmEventType.TMPPWDISSUE);
            logger.info("Prm Logon controller if: profileService.getBaseProfile()");

            String cisKey = serviceOpsModel.getCisId();
            prmDto.setCustomerCisKey(cisKey);
            prmDto.setProfileOwner(BANK_ID + cisKey);
            prmDto.setEventInitiatorType(getInitiatorType());

            logger.info("Enter service ops temp pwd PRM Event triggerIssuePwdServiceOpsPrmEvent()");
            // check to confirm if CIS key is available for customer else prm event is not triggered
            if(null != prmDto.getCustomerCisKey()) {
                prmConnectService.submitRequest(prmDto);
                logger.info("Service Ops temp Pwd PRM event triggered successfully ");
            } else {
                logger.info("Customer CIS Key Is Null Hence not triggering Service Ops temp Pwd PRM event");
            }
        } catch (Exception ae) {
            logger.error("Error in sending PRM change pwd service ops event : ", ae);
        }
    }

    /*
    * This event occurs when user's access blocked/unblocked from Service ops.
    * */
    @Override
    public void triggerAccessBlockPrmEvent(ServiceOpsModel serviceOpsModel, boolean isAccessBlocked) {
        logger.info("Started triggering AccessBlock PRM event");
        try {
            PrmDto prmDto = buildPrmDto();
            if (null != serviceOpsModel && prmDto != null) {
                if(isAccessBlocked){
                    prmDto.setSucceded(TRUE);
                } else{
                    prmDto.setSucceded(FALSE);
                }
                prmDto.setEventType(PrmEventType.ACCESSBLOCK);
                prmDto.setUserId(serviceOpsModel.getUserName());
                logger.info("Prm triggerAccessBlockPrmEvent SO controller if: profileService.getBaseProfile()");
                if(null!=userProfileService && userProfileService.isAdviser()){
                    prmDto.setCustomerCisKey(userProfileService.getUserId());
                }else{
                    String cisKey = serviceOpsModel.getCisId();
                    prmDto.setCustomerCisKey(cisKey);
                    prmDto.setProfileOwner(BANK_ID + cisKey);
                }
                prmDto.setEventInitiatorType(getInitiatorType());
                prmDto.setUtilString5(BLOCK_OPERATOR);
                if (null != userProfileService) {
                    prmDto.setUtilString3(userProfileService.getUserId());
                }
                logger.info("Enter accessBlock PRM Event triggerAccessBlockPrmEvent()");
                // check to confirm if CIS key is available for customer else prm event is not triggered
                if( null != prmDto.getCustomerCisKey()) {
                    prmConnectService.submitRequest(prmDto);
                    logger.info("AccessBlock PRM event triggered successfully ");
                }else{
                    logger.info("Customer CIS Key Is Null Hence not triggering AccessBlock PRM event");
                }
                logger.info("Exit PRM accessBlock triggerAccessBlockPrmEvent()");
            }
        } catch (Exception ae) {
            logger.error("Error in sending PRM AccessBlock event : ", ae);
        }
    }

    /*
    * Populates common data for every event
    * IP Address
    * CISKey
    * Session Id
    * user Id
    * Staff Id
    */
    private PrmDto getBasicDto() {
        PrmDto prmDto = buildPrmDto();
        String cisKey = getCustomerCisKey(null);

        if (null != cisKey) {
            prmDto.setProfileOwner(BANK_ID + cisKey);
            // Populating CISKEY of loggedin person. It will be override in case of service ops events by client CISKEY
            prmDto.setCustomerCisKey(cisKey);
        }

        return prmDto;
    }

    private PrmDto getBasicDto(HttpServletRequest httpServletRequest) {
        PrmDto prmDto = buildPrmDto(httpServletRequest);
        String cisKey = getCustomerCisKey(null);

        if (null != cisKey) {
            prmDto.setProfileOwner(BANK_ID + cisKey);
            // Populating CISKEY of loggedin person. It will be override in case of service ops events by client CISKEY
            prmDto.setCustomerCisKey(cisKey);
        }

        return prmDto;
    }

    /*
    * Populates common data for every event
    * IP Address
    * CISKey
    * Session Id
    * user Id
    * Staff Id
    */

    private PrmDto buildPrmDto() {

        PrmDto prmDto = buildCommonPrmDto();
        String clientIp = getClientIp();

        if (clientIp != null)
            prmDto.setClientIp(clientIp);

        return prmDto;
    }

    private PrmDto buildPrmDto(HttpServletRequest httpServletRequest) {

        PrmDto prmDto = buildCommonPrmDto();
        String clientIp = httpServletRequest.getRemoteAddr();
        logger.info("Received clinet ip :", clientIp);
        if (null != clientIp)
            prmDto.setClientIp(clientIp);
        return prmDto;
    }

    private PrmDto buildCommonPrmDto() {

        PrmDto prmDto = new PrmDto();
        prmDto.setMsgRcrdSrcId(MSG_RCRD_SRC_ID);
        prmDto.setTranDateTime(PrmUtil.getTranDateTime());
        prmDto.setRiskMsgType(RISK_MSG_TYPE);
        prmDto.setTranCode(TRAN_CODE);
        prmDto.setSqncNum(UUID.randomUUID().toString().replaceAll("-", ""));
        prmDto.setBankId(BANK_ID);
        prmDto.setChannelId(CHANNEL_ID);
        prmDto.setSucceded(TRUE);
        // Getting User Id / Customer Number
        if (null != userProfileService.getBaseProfile() && null != userProfileService.getBaseProfile().getActiveProfile()) {
            logger.info("Prm getBaiscDto if: profileService.getBaseProfile()");
            prmDto.setUserId(userProfileService.getUserId());
            prmDto.setEventInitiatorId(userProfileService.getUserId());
        }
        if (null != userProfileService.getSamlToken()) {
            prmDto.setSessionId(userProfileService.getSamlToken().getSession());
        }
        prmDto.setEventInitiatorType(InitiatorType.CUSTOMER.toString());
        prmDto.setEmployeeIdExtension(getEmployeeIDExtension());
        return prmDto;
    }
    /*
* Populate primary mobile number for SMS 2FA event
*
*/
    private String populateDeviceNumber() {
        ServiceErrorsImpl serviceErrors = new ServiceErrorsImpl();

        if(null!=userProfileService && null !=userProfileService.getActiveProfile()) {

            IndividualDetailImpl clientDetail = (IndividualDetailImpl) clientIntegrationService
                    .loadClientDetails(userProfileService.getActiveProfile().getClientKey(), serviceErrors);
            if (null != clientDetail && !serviceErrors.hasErrors()) {
                List<Phone> listPhone = clientDetail.getPhones();
                if (!CollectionUtils.isEmpty(listPhone)) {
                    for (Phone phone : listPhone) {
                        if (phone.getType().equals(AddressMedium.MOBILE_PHONE_PRIMARY))
                            return phone.getNumber();
                    }
                }
            } else {
                logger.error("Error In Retrieving Device Number From Avaloq {}", serviceErrors.getErrorMessagesForScreenDisplay());
            }
        }
        return null;
    }

    private String getClientIp() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();

        if (null != attr && attr.getRequest() != null) {
            HttpServletRequest request = attr.getRequest();

            HttpRequestParams requestParams = HttpRequestConverter.toHttpRequestParams(request);
            logger.debug("IP address :requestParams " + requestParams);

            if (null != requestParams.getHttpOriginatingIpAddress()) {
                logger.debug("IP address : requestParams.getHttpOriginatingIpAddress());" + requestParams
                        .getHttpOriginatingIpAddress());
                return requestParams.getHttpOriginatingIpAddress();
            }
        }

        return null;
    }

    private String getInitiatorType() {
        if (!userProfileService.isEmulating() && userProfileService.isInvestor()) {
            return InitiatorType.CUSTOMER.toString();
        } else {
            return InitiatorType.STAFF.toString();
        }
    }

    private String getCustomerCisKey(ClientKey clientKey) {

        if (clientKey == null && null != userProfileService.getActiveProfile()) {
            clientKey = userProfileService.getActiveProfile().getClientKey();
        }

        if (null != clientKey) {
            IndividualDetailImpl clientDetail = (IndividualDetailImpl) clientIntegrationService
                    .loadClientDetails(clientKey, new FailFastErrorsImpl());

            if (clientDetail != null && null != clientDetail.getCISKey() ) {
                logger.debug("Prm Logon controller : clientDetail.getCISKey() :" + clientDetail.getCISKey());
                return clientDetail.getCISKey().getId();
            }
        }

        return null;
    }

    private String getEmployeeIDExtension() {
        if (!InitiatorType.CUSTOMER.toString().equalsIgnoreCase(getInitiatorType())) {
            if(null != userProfileService && null != userProfileService.getActiveProfile()) {
                return userProfileService.isServiceOperator() ? "WBC" : "AVL";
            } else {
                return "AVL";
            }
        }
        return  "";
    }
}