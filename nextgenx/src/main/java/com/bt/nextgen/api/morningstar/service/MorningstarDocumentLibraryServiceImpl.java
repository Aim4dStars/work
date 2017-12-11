package com.bt.nextgen.api.morningstar.service;

import com.bt.nextgen.api.morningstar.model.MorningstarAssetProfileKey;
import com.bt.nextgen.api.morningstar.model.MorningstarDocumentKey;
import com.bt.nextgen.core.util.Properties;
import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.asset.Asset;
import com.bt.nextgen.service.integration.asset.AssetIntegrationService;
import com.bt.nextgen.service.web.HttpStreamReturnCode;
import com.bt.nextgen.service.web.HttpStreamService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class MorningstarDocumentLibraryServiceImpl implements MorningstarDocumentLibraryService {
	private static final Logger logger = LoggerFactory.getLogger(MorningstarDocumentLibraryServiceImpl.class);

	private static final String morningstarFundProfilePdfUrl = Properties.get("morningstar.fundProfile.pdf.url");
	private static final String morningstarDocumentLibraryUrl = Properties.get("morningstar.documentLibrary.pdf.url");

	@Autowired
	private HttpStreamService httpStreamService;

	@Autowired
	@Qualifier("avaloqAssetIntegrationService")
	private AssetIntegrationService assetIntegrationService;

	/**
	 * Stream PDF document from Morningstar to the user
	 *
	 * @param key           the asset profile key
	 * @param redirect      set to true to redirect user to file instead of streaming the file from proxy
	 * @param response      the HTTP servlet response object
	 * @param serviceErrors service errors
	 * @return HttpStreamReturnCode.OK if OK or HttpStreamReturnCode.ERROR if error.
	 */
	@Override
	public HttpStreamReturnCode getFundProfileDocument(MorningstarAssetProfileKey key, boolean redirect,
													   HttpServletResponse response, ServiceErrors serviceErrors) {
		final String urlString =
				morningstarFundProfilePdfUrl + "?symbol=" + encodeParam(key.getSymbol()) + "&type=" + encodeParam(key.getType()) +
						"&client=" + encodeParam(key.getClient());

		// Local environments cannot use proxy due to security restrictions, hence redirecting instead
		if (redirect) {
			try {
				response.sendRedirect(urlString);
			} catch (IOException e) {
				logger.error("Unable to redirect to {}", urlString, e);
				serviceErrors.addError(new ServiceErrorImpl("Unable to redirect to " + urlString));
				return HttpStreamReturnCode.ERROR;
			}

			return HttpStreamReturnCode.OK;
		}

		return httpStreamService.streamBinaryContentFromUrl(urlString, encodeParam(key.getSymbol()) + "." +
				encodeParam(key.getType().toLowerCase()), response, serviceErrors);
	}

	/**
	 * Forwards the user to the PDS document
	 *
	 * @param key           the document key.  The symbol must ISIN code.
	 * @param response      HttpServletResponse object
	 * @param serviceErrors the service errors object
	 */
	@Override
	public void getDocumentFromLibrary(MorningstarDocumentKey key, HttpServletResponse response, ServiceErrors serviceErrors) {
		Asset asset = assetIntegrationService.loadAsset(key.getAssetId(), serviceErrors);

		// Via the original path - not currently used
		//		String redirectUrl =
		//				morningstarDocumentLibraryUrl + "&isin=" + asset.getIsin() + "&documenttype=" +
		//						MorningstarDocumentType.forCode(key.getDocumentType()).getExternalTypeId() + "&format=pdf";

		// Using this way will only retrieve PDS.  No requirement to grab other type of documents for now
		String redirectUrl = morningstarDocumentLibraryUrl + asset.getIsin() + ".pdf";

		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			logger.error("Unable to redirect to {}", redirectUrl, e);
			serviceErrors.addError(new ServiceErrorImpl("Unable to redirect to " + redirectUrl));
		}
	}

	private String encodeParam(String param) {
		try {
			return URLEncoder.encode(param, StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			logger.error("Unable to encode param", e);
			return null;
		}
	}
}
