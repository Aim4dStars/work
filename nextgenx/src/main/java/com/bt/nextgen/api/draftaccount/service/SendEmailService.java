package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.SendEmailDto;
import com.bt.nextgen.core.api.dto.SubmitDtoService;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.userinformation.ClientKey;

public interface SendEmailService extends SubmitDtoService<ClientKey, SendEmailDto> {

    String sendEmailWithExistingRegoCodeForInvestor(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors);

    String sendEmailWithExistingRegoCodeForAdviser(String gcmId, String role, ServiceErrors serviceErrors);

    String sendEmailFromServiceOpsDesktopForInvestor(String clientId, String adviserPositionId, String role, ServiceErrors serviceErrors);

    String sendEmailFromServiceOpsDesktopForAdviser(String gcmId, String role, ServiceErrors serviceErrors);

    String resendRegistrationEmail(String gcmId, String role, ServiceErrors serviceErrors);
}
