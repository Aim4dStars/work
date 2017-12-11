package com.bt.nextgen.document.web.controller;

import com.bt.nextgen.document.web.service.DocumentService;
import com.btfin.panorama.core.security.avaloq.Constants;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentData;
import com.bt.nextgen.service.integration.financialdocument.FinancialDocumentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(method = RequestMethod.GET, value = "/secure", produces = "application/pdf")
public class DocumentController
{

	@Autowired
	private DocumentService documentService;

    public static final String DOC_TYPES_PARAMETER_MAPPING = "doc-type";
    public static final String FILE_EXTENSION_PARAMETER_MAPPING = "type";

	@RequestMapping(method = RequestMethod.GET, value = "/statements/{statementId}")
	@PreAuthorize("isAuthenticated()")
	public void getStatement(@PathVariable(value = "statementId") String statementId,
                             @RequestParam(value = FILE_EXTENSION_PARAMETER_MAPPING, required = false) String fileExtension,
                             @RequestParam(value = DOC_TYPES_PARAMETER_MAPPING, required = false) String documentType,
                             HttpServletResponse response)
		throws IOException
	{
        FinancialDocumentData doc = documentService.loadDocument(statementId, FinancialDocumentType.forCode(documentType));
		if (doc == null)
		{
			return;
		}
        else {
            if (fileExtension != null && Constants.FILE_EXTENSION_CSV.equals(fileExtension)) {
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + statementId+".csv" + "\"");
            } else {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment;");
            }
            response.getOutputStream().write(doc.getData());
        }
	}
}
