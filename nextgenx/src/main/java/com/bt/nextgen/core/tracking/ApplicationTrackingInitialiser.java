package com.bt.nextgen.core.tracking;

import com.bt.nextgen.api.statements.permission.DocumentRequestManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import static com.bt.nextgen.core.tracking.HttpRequestTrackingReference.httpRequestTrackingGenerator;

/**
 * Makes sure that all tracking references are initialised correctly
 */
public class ApplicationTrackingInitialiser implements ServletContextListener, ServletRequestListener
{
	private static final Logger logger = LoggerFactory.getLogger(ApplicationTrackingInitialiser.class);
	public static final String TRACKING_REFERENCE = "trackingReference";

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		MDC.put(TRACKING_REFERENCE, String.valueOf(TrackingReferenceLocator.locate()));
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		MDC.remove(TRACKING_REFERENCE);
	}

	@Override
	public void requestDestroyed(ServletRequestEvent event) {
		TrackingReferenceLocator.clear();
		MDC.remove(TRACKING_REFERENCE);
		DocumentRequestManager.removeDocuments();
	}

	@Override
	public void requestInitialized(ServletRequestEvent event) {
		HttpServletRequest httpRequest = (HttpServletRequest) event.getServletRequest();
		TrackingReference ref = httpRequestTrackingGenerator().generate(httpRequest);
		TrackingReferenceLocator.initialiseReference(ref);

		logger.debug("Initialised tracking reference {}", ref.getTrackingReference());

		MDC.put(TRACKING_REFERENCE, String.valueOf(ref));
	}
}
