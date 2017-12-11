package com.bt.nextgen.api.termdeposit.controller;

import com.bt.nextgen.api.termdeposit.service.TermDepositCalculatorDtoService;
import com.bt.nextgen.api.termdeposit.service.TermDepositReportService;
import com.bt.nextgen.cms.service.CmsService;
import com.bt.nextgen.core.toggle.FeatureToggles;
import com.bt.nextgen.core.toggle.FeatureTogglesService;
import com.bt.nextgen.web.controller.cash.util.Attribute;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.Charset;

@Controller
public class TermDepositReportsController {
    @Autowired
    private TermDepositCalculatorDtoService termDepositCalculatorDtoService;

    @Autowired
    private TermDepositReportService depositReportService;

    @Autowired
    private FeatureTogglesService featureTogglesService;

    @Autowired
    private CmsService cmsService;

    @RequestMapping(method = RequestMethod.GET, value = "/secure/reportcsv/getTermDepositRates", produces =
            "application/xml")
    public ResponseEntity<byte[]> getTDRates(@RequestParam final String brand,
            @RequestParam(required = false, value = "type") final String type,
            @RequestParam(required = false, value = "product-id") final String productId,
            @RequestParam(required = false, value = "account-id")String accountId) throws Exception {
        boolean termDepositToggle = featureTogglesService.findOne(new FailFastErrorsImpl())
                .getFeatureToggle(FeatureToggles.TERMDEPOSIT_TOGGLE);
        String termDepositCsv;
        if (termDepositToggle) {
            termDepositCsv = depositReportService.getTermDepositRatesAsCsv(brand, type, productId, accountId);
        } else {
            termDepositCsv = termDepositCalculatorDtoService.getTermDepositRatesAsCsv(brand, type, productId);
        }
        final String csvFileName = cmsService.getContent(brand + "_name") + "_" + Attribute.TERM_DEPOSIT_FILENAME;
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(Attribute.APPLICATION_CSV));
        headers.setContentDispositionFormData(Attribute.ATTACHMENT, csvFileName);
        headers.setContentLength(termDepositCsv.getBytes(Charset.defaultCharset()).length);
        return new ResponseEntity<>(termDepositCsv.getBytes(), headers, HttpStatus.OK);
    }
}
