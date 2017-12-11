package com.bt.nextgen.service.web;

import com.bt.nextgen.core.util.Properties;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;

@Service
public class UrlProxyServiceImpl implements UrlProxyService {
	private static String proxyServerHostname = Properties.get("proxyServer.hostname");
	private static int proxyServerPort = Properties.getInteger("proxyServer.port");

	/**
	 * Basic proxy connection handler.  Supports HTTP only and there is no authentication support.
	 * Multiple proxies using ProxySelector is not supported.  This may be implemented in the future.
	 *
	 * @param url the target URL
	 * @return URLConnection
	 * @throws IOException
	 */
	public URLConnection connect(URL url) throws IOException {
		SocketAddress address = new InetSocketAddress(proxyServerHostname, proxyServerPort);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, address);

		return url.openConnection(proxy);
	}
}
