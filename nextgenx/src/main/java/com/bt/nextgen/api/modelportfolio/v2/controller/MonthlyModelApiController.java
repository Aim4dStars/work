package com.bt.nextgen.api.modelportfolio.v2.controller;

import com.bt.nextgen.api.modelportfolio.v2.service.ModelPortfolioDtoService;
import com.bt.nextgen.core.exception.AccessDeniedException;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Controller("MonthlyModelApiControllerV2")
@RequestMapping(produces = "application/pdf")
public class MonthlyModelApiController
{
    @Autowired
    private UserProfileService profileService;

    @Autowired
    private ModelPortfolioDtoService modelPortfolioService;

    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.monthlyDownload}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    public void getMonthlyModel(@PathVariable(value = "model-id") String modelId,
            HttpServletResponse response) throws IOException {

        if (!profileService.isEmulating()) {
            FinancialDocumentData doc = modelPortfolioService.loadMonthlyModelDocument(modelId, FinancialDocumentType.IMMODEL);
            generateDocument(response, doc);
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "${api.modelportfolio.v2.uri.monthlyDownloads}")
    @PreAuthorize("isAuthenticated() and hasPermission(null, 'View_model_portfolios')")
    public void getAllMonthlyModel(HttpServletResponse response)
            throws IOException {

        if (!profileService.isEmulating()) {
            FinancialDocumentData doc = modelPortfolioService.loadMonthlyModelDocument(null, FinancialDocumentType.IMMODELALL);
            generateDocument(response, doc);
        } else {
            throw new AccessDeniedException("Access Denied");
        }
    }

    private void generateDocument(HttpServletResponse response, FinancialDocumentData docData) throws IOException {
        if (docData == null) {
            return;
        }

        String docId = docData.getDocumentKey().getId();
        // IM Model report is always a Excel report.
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + docId + ".xls" + "\"");

        response.getOutputStream().write(docData.getData());
    }
}
