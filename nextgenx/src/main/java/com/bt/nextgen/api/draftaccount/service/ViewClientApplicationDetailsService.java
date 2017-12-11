package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ClientApplicationDetailsDto;
import com.bt.nextgen.service.ServiceErrors;

import java.util.List;

/**
 * Created by F058391 on 10/08/2016.
 */
public interface ViewClientApplicationDetailsService {

    ClientApplicationDetailsDto viewClientApplicationById(Long ClientApplicationId, ServiceErrors serviceErrors);
    ClientApplicationDetailsDto viewOnlineClientApplicationByAccountNumbers(List<String> accountNumbers, ServiceErrors serviceErrors);
    ClientApplicationDetailsDto viewClientApplicationByAccountNumber(String accountNumber, ServiceErrors serviceErrors);
}
