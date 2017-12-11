package com.bt.nextgen.service.web;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public interface UrlProxyService {
	URLConnection connect(URL url) throws IOException;
}
