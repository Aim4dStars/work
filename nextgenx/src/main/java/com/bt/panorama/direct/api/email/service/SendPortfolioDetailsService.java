package com.bt.panorama.direct.api.email.service;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.panorama.direct.api.email.model.PortfolioDetailDto;


public interface SendPortfolioDetailsService {

    boolean sendPortfolioDetails(PortfolioDetailDto portfolioDetailDto, ServiceErrors serviceErrors);
}
