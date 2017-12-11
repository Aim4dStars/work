package com.bt.nextgen.service.avaloq.movemoney;

import com.bt.nextgen.integration.xml.annotation.ServiceBean;
import com.bt.nextgen.integration.xml.annotation.ServiceBeanType;

@ServiceBean(xpath = "doc", type = ServiceBeanType.ABSTRACT, lazyBeanClasses = { DepositDetailsImpl.class,
        RecurringDepositDetailsImpl.class })
public class DepositDetailsBaseImpl {
}
