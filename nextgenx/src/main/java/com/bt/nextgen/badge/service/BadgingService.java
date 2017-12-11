package com.bt.nextgen.badge.service;

import com.bt.nextgen.badge.model.Badge;
import com.bt.nextgen.service.ServiceErrors;

public interface BadgingService {

    Badge getBadgeForCurrentUser(ServiceErrors serviceErrors);

}
