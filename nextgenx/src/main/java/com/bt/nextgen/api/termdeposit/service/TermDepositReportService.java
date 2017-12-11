package com.bt.nextgen.api.termdeposit.service;

/**
 * Created by M044020 on 3/08/2017.
 */
public interface TermDepositReportService {
    String getTermDepositRatesAsCsv(String brand, String type, String productId, String accountId);
}
