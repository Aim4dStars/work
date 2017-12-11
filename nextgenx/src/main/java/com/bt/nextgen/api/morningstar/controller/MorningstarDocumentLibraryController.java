package com.bt.nextgen.api.morningstar.controller;

import com.bt.nextgen.api.morningstar.model.MorningstarAssetProfileKey;
import com.bt.nextgen.api.morningstar.model.MorningstarDocumentKey;
import com.bt.nextgen.api.morningstar.service.MorningstarDocumentLibraryService;
import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
public class MorningstarDocumentLibraryController {
	private static final boolean redirectMode = Properties.getBoolean("morningstar.fundProfile.pdf.redirect");

	@Autowired
	private MorningstarDocumentLibraryService documentLibraryService;

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = "/secure/ms/fundpdf/fundprofile-b")
	public void getFundProfileDocument(@RequestParam("symbol") String symbol, @RequestParam("type") String type,
									   @RequestParam("client") String client, HttpServletResponse response) {

		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		documentLibraryService
				.getFundProfileDocument(new MorningstarAssetProfileKey(symbol, type, client), redirectMode, response, serviceErrors);
	}

	@PreAuthorize("isAuthenticated()")
	@RequestMapping(method = RequestMethod.GET, value = "/secure/ms/documentlibrary")
	public void getDocumentFromLibrary(@RequestParam("assetId") String assetId, @RequestParam("type") String type,
									   HttpServletResponse response) {

		ServiceErrors serviceErrors = new FailFastErrorsImpl();
		documentLibraryService.getDocumentFromLibrary(new MorningstarDocumentKey(assetId, type), response, serviceErrors);
	}
}
