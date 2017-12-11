package com.bt.nextgen.api.cashratehistory.service;

import com.bt.nextgen.api.cashratehistory.model.CashRateHistoryDto;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.api.ApiVersion;
import com.bt.nextgen.core.api.exception.BadRequestException;
import com.bt.nextgen.core.web.ApiFormatter;
import com.bt.nextgen.core.web.AvaloqFormatter;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.integration.asset.AssetKey;
import com.bt.nextgen.service.integration.history.CashRateHistoryService;
import com.bt.nextgen.service.integration.history.CashReport;
import com.bt.nextgen.service.integration.history.InterestDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by L072457 on 30/12/2014.
 */
@Service
public class CashRateHistoryDtoServiceImpl implements CashRateHistoryDtoService {


    private static final Logger logger = LoggerFactory.getLogger(CashRateHistoryDtoServiceImpl.class);

    private static final String CASH_RATES_HISTORY = "Doc.IP.cash.rates";
    private static final String COMMA = ",";
    private static final int SKIP_LINE_COUNT = 1;
    private static final int DECIMAL_PRECISION = 2;

    @Autowired
    private CmsService cmsService;

    @Autowired
    private CashRateHistoryService cashRateHistoryService;

    /**
     * Loading cash rate history from CMS, from CSV file.
     * @param realPath
     * @return List<CashRateHistoryDto>
     */
    public List<CashRateHistoryDto> getCashRates(String realPath) {

        List<CashRateHistoryDto> cashRates = new ArrayList<>();
        //(../../public/static/csv/CashRates.csv)
        String cashRatesHistory = cmsService.getContent(CASH_RATES_HISTORY).replace("../..", "");
        String csvFile = realPath.concat(cashRatesHistory);
        BufferedReader br = null;
        String line = "";
        int skipCount = 0;
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                //skipping first two lines title and headers
                if(skipCount > SKIP_LINE_COUNT) {
                    CashRateHistoryDto cashRateHistoryDto = new CashRateHistoryDto();
                    // use comma as separator
                    String[] history = line.split(COMMA);
                    cashRateHistoryDto.setRateDate(history[0]);
                    BigDecimal rate = new BigDecimal((history[1]));
                    //rounding to two decimal point.
                    cashRateHistoryDto.setRate(rate.setScale(DECIMAL_PRECISION, RoundingMode.FLOOR).toPlainString());

                    cashRates.add(cashRateHistoryDto);
                }
                ++ skipCount;
            }
        } catch (FileNotFoundException e) {
            logger.error("Cash Rate History - CSV file not found.", e);
        } catch (IOException ioe) {
            logger.error("Cash Rate History - CSV file format Issue.", ioe);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    logger.error("Cash Rate History - Exception to close BufferedReader.", e);
                }
            }
        }
        // Sort Cash Rate history entries
        Collections.sort(cashRates, new Comparator<CashRateHistoryDto>() {
            @Override
            public int compare(CashRateHistoryDto o1, CashRateHistoryDto o2) {
                try {
                    SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yy");
                    return -(format.parse(o1.getRateDate()).compareTo(format.parse(o2.getRateDate())));
                }catch (Exception e) {
                    return 0;
                }
            }
        });
        //setting current rate flag true.
        if(cashRates.size() > 0) {
            cashRates.get(0).setCurrentRate(true);
        }

        return cashRates;
    }

    /**
     * Loading cash rate history from Avaloq.
     * @return List<CashRateHistoryDto>
     */
    public List<CashRateHistoryDto> loadCashRates() {
        //for cash rate history no need to send any asset key
        Collection<AssetKey> assetIds = new ArrayList<>();
        ServiceErrors serviceErrors = new ServiceErrorsImpl();
        logger.info("Getting cash rates from avaloq.");
        List<CashReport> cashReports = cashRateHistoryService.loadCashRateHistory(assetIds, serviceErrors);

        return toModelDto(cashReports);
    }

    private List<CashRateHistoryDto> toModelDto(List<CashReport> cashReports) {
        List<CashRateHistoryDto> cashRates = new ArrayList<>();
        if(cashReports != null && !cashReports.isEmpty()) {
            CashRateHistoryDto cashRateHistoryDto = null;
            for(CashReport report : cashReports) {
                List<InterestDate> interestDates = report.getInterestRates();
                if(interestDates != null && !interestDates.isEmpty()) {
                    for(InterestDate interestDate : interestDates) {
                        cashRateHistoryDto = new CashRateHistoryDto();
                        cashRateHistoryDto.setRateDate(ApiFormatter.asShortDate(interestDate.getEffectiveDate()));
                        BigDecimal rate = (BigDecimal) (interestDate.getInterestRate() != null ? interestDate.getInterestRate() :"0");
                        cashRateHistoryDto.setRate(rate.setScale(DECIMAL_PRECISION, RoundingMode.FLOOR).toPlainString());

                        cashRates.add(cashRateHistoryDto);
                    }
                } else {
                    logger.info("InterestDate list is null or with zero size.");
                }
            }
            // Sort Cash Rate history entries
            sortCashRates(cashRates);
            //setting current rate flag true.
            if(!cashRates.isEmpty()) {
                cashRates.get(0).setCurrentRate(true);
            }
        } else {
            logger.info("No any cash rate history found from avaloq.");
            throw new BadRequestException(ApiVersion.CURRENT_VERSION, "Unable to find cash rate history from avaloq.");
        }
        return cashRates;
    }

    private void sortCashRates(List<CashRateHistoryDto> cashRates) {
        Collections.sort(cashRates, new Comparator<CashRateHistoryDto>() {
            @Override
            public int compare(CashRateHistoryDto o1, CashRateHistoryDto o2) {
                try {
                    return AvaloqFormatter.asAvaloqFormatDate(o2.getRateDate()).compareTo(AvaloqFormatter.asAvaloqFormatDate(o1.getRateDate()));
                }
                //CHECKSTYLE:OFF
                catch (Exception e) {
                    logger.warn("Error in sorting cash rated", e);
                    return 0;
                }
                //CHECKSTYLE:ON
            }
        });
    }

}
