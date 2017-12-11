package com.bt.nextgen.api.cashratehistory.service;

import com.bt.nextgen.api.cashratehistory.model.CashRateHistoryDto;

import java.util.List;

/**
 * Created by L072457 on 30/12/2014.
 */
public interface CashRateHistoryDtoService {

    /**
     * Loading cash rate history from CMS, from CSV file.
     * @param realPath
     * @return List<CashRateHistoryDto>
     */
    public List<CashRateHistoryDto> getCashRates(String realPath);

    /**
     * Loading cash rate history from Avaloq.
     * @return List<CashRateHistoryDto>
     */
    public List<CashRateHistoryDto> loadCashRates();
}
