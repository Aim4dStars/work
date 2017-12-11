package com.bt.nextgen.service.web;

import com.btfin.panorama.service.exception.ServiceErrorImpl;
import com.bt.nextgen.service.ServiceErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class HttpStreamServiceImpl implements HttpStreamService {
	private static final Logger logger = LoggerFactory.getLogger(HttpStreamServiceImpl.class);

	@Autowired
	private UrlProxyService urlProxyService;

	/**
	 * Stream binary content from HTTP URL to the output stream.
	 * <p/>
	 * Notes:
	 * - Does not currently support HTTPS.
	 * - Does not handle different response code scenarios as currently there is no business requirement for it.
	 * - No validation on the file type returned by the server.
	 *
	 * @param urlString     the full HTTP URL string
	 * @param response      the HttpServletResponse object
	 * @param serviceErrors service errors object
	 * @return HttpStreamReturnCode.ERROR if there is any connection error, else HttpStreamReturnCode.OK
	 */
	public HttpStreamReturnCode streamBinaryContentFromUrl(String urlString, String fileName, HttpServletResponse response,
														   ServiceErrors serviceErrors) {
		OutputStream outputStream = null;
		InputStream inputStream = null;

		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) urlProxyService.connect(url);
			connection.setRequestMethod("GET");
			connection.connect();

			logger.debug("Connected to {}.  Response code {}, content type {}", urlString, connection.getResponseCode(),
					connection.getContentType());

			inputStream = connection.getInputStream();
			outputStream = response.getOutputStream();

			setHeader(fileName, connection.getContentType(), response);

			byte[] buffer = new byte[4096];
			int bytes;
			int totalBytes = 0;

			while ((bytes = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytes);
				totalBytes += bytes;
			}

			logger.debug("Received {} bytes from {}", totalBytes, urlString);
		} catch (MalformedURLException e) {
			logger.error("Invalid URL \"{}\"", urlString, e);
			serviceErrors.addError(new ServiceErrorImpl("Invalid URL \"" + urlString + "\": " + e));
		} catch (IOException e) {
			logger.error("Unable to read from \"{}\"", urlString, e);
			serviceErrors.addError(new ServiceErrorImpl("Unable to read from \"" + urlString + "\": " + e));
		} finally {
			closeStreams(outputStream, inputStream, urlString, serviceErrors);
		}

		return serviceErrors.hasErrors() ? HttpStreamReturnCode.ERROR : HttpStreamReturnCode.OK;
	}

	private void closeStreams(OutputStream outputStream, InputStream inputStream, String urlString, ServiceErrors serviceErrors) {
		try {
			if (outputStream != null) {
				outputStream.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}
		} catch (IOException e) {
			logger.error("Error encountered while closing streams for \"{}\"", urlString, e);
			serviceErrors.addError(new ServiceErrorImpl("Error encountered while closing streams:" + e));
		}
	}

	private void setHeader(String fileName, String contentType, HttpServletResponse response) {
		response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType(contentType);
	}
}
