package com.bt.nextgen.service.web;

import com.bt.nextgen.service.ServiceErrors;

import javax.servlet.http.HttpServletResponse;

public interface HttpStreamService {
	HttpStreamReturnCode streamBinaryContentFromUrl(String urlString, String fileName, HttpServletResponse response,
													ServiceErrors serviceErrors);
}
