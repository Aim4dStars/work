package com.bt.nextgen.api.morningstar.service;

import com.bt.nextgen.api.morningstar.model.MorningstarAssetProfileKey;
import com.bt.nextgen.api.morningstar.model.MorningstarDocumentKey;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.web.HttpStreamReturnCode;

import javax.servlet.http.HttpServletResponse;

public interface MorningstarDocumentLibraryService {
	HttpStreamReturnCode getFundProfileDocument(MorningstarAssetProfileKey key, boolean redirect, HttpServletResponse response,
												ServiceErrors serviceErrors);

	void getDocumentFromLibrary(MorningstarDocumentKey key, HttpServletResponse response, ServiceErrors serviceErrors);
}
