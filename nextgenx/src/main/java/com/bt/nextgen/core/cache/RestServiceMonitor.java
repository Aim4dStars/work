package com.bt.nextgen.core.cache;

import com.bt.nextgen.core.IServiceStatus;
import com.btfin.panorama.service.client.status.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import com.bt.nextgen.core.util.Properties;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;


/**
 * RestServiceMonitor
 *
 * RestServiceMonitor provides service status logging for off thread implementations of Nextgen.
 *
 * To determine whether Nextgen is ready for login and general use, the status of multiple services need to be
 * checked.  Any service client which implements @IServiceStatusRegistry can register with Service Monitor, and have
 * its status checked at regular intervals.
 *
 * Service Monitor sets up two status checks: statusCheck and timeoutCheck.
 *
 * statusCheck polls every registered client at a regular interval until all services are ready
 * timeoutCheck logs an error message if Nextgen (and all services) are not ready after a designated time period.
 */
@EnableScheduling
@Configuration
@Profile({"OffThreadImplementation"})
public class RestServiceMonitor implements IServiceStatus, IServiceStatusRegistry {

	private static final Logger logger = LoggerFactory.getLogger(RestServiceMonitor.class);

	private TaskScheduler scheduler;

	private ScheduledFuture scheduledMonitor;
	private ScheduledFuture scheduledTimeout;

	private static final String STATUS_CHECK_MINUTES = "external.services.status.minutes";
	private static final String TIMEOUT_MINUTES = "external.services.timeout.minutes";

	private Set<IServiceStatusClient> serviceStatusClients = new HashSet<>();

	private static boolean servicesUp = false;
	private boolean dataInitCachePopulated = false;

	private static final long ONE_MINUTE_IN_MILLIS=60000L;//milliseconds

	/**
	 * Schedules the status check to run every STATUS_CHECK_MINUTES minutes.
	 */
	@PostConstruct
	public void scheduleStatusMonitor() {
		ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduler = new ConcurrentTaskScheduler(localExecutor);
		scheduledMonitor = scheduler.scheduleAtFixedRate(statusCheck, Properties.getInteger(STATUS_CHECK_MINUTES) * ONE_MINUTE_IN_MILLIS);
	}

	/**
	 * Performs a status check, cancels itself once the overall status of nextgen is Started (i.e. all
	 * services up)
	 */
	protected Runnable statusCheck = new Runnable() {
		@Override
		public void run() {
			logger.info("Checking for external application services status...");
			servicesUp = checkCacheStatus();

			logger.info("Checking external application services status done");
			if(servicesUp == true) {
				scheduledMonitor.cancel(true);
			}
	}
	};

	/**
	 * Schedules the timeout check to run after TIMEOUT_MINUTES minutes.
	 */
	@PostConstruct
	public void scheduleTimeoutCheck() {
		ScheduledExecutorService localExecutor = Executors.newSingleThreadScheduledExecutor();
		scheduler = new ConcurrentTaskScheduler(localExecutor);

		//set timer to TIMEOUT_MINUTES minutes from now
		Calendar date = Calendar.getInstance();
		long t= date.getTimeInMillis();
		Date stopTime = new Date(t + (Properties.getInteger(TIMEOUT_MINUTES) * ONE_MINUTE_IN_MILLIS));

		scheduledTimeout = scheduler.schedule(timeoutCheck, stopTime);
	}

	/**
	 * Logs an error message if Nextgen has not started within a designated time.
	 *
	 * timeoutCheck is only executed once.
	 */
	 protected Runnable timeoutCheck = new Runnable() {
		@Override
		public void run() {
			if(checkCacheStatus() == false) {
				logger.error("Services have not started within an acceptable time period, please investigate");
			}
			scheduledTimeout.cancel(true);//only run timeout once
		}
	};

	/**
	 * Iterates over each registered service client, and collects the service status from each client.
	 *
	 * @return a List of Service Statuses
	 */
	@Override
	public List<ServiceStatus> getServiceStatus() {
		List<ServiceStatus> applicationServiceStatuses = new CopyOnWriteArrayList<>();

		for(IServiceStatusClient serviceStatusClient : serviceStatusClients) {
			ServiceStatus serviceStatus = serviceStatusClient.getServiceStatus();
			applicationServiceStatuses.add(serviceStatus);
		}
		return applicationServiceStatuses;
	}

	/**
	 * This method will log the status of each registered remote service.
	 *
	 * If a remote service stores cached data, the status of the caches will be logged as well.
	 *
	 * Once all the dataInitialization is completed the application will be open for login.
	 *
	 * TODO: rename to monitor() once on-thread is decommissioned
	 */
	@Override
	public boolean checkCacheStatus(){
		logger.debug("Cache populated status of Data Initialization services is {}", dataInitCachePopulated);

		//serviceStatusClients.size() > 0 ensures there are services registered with RestServiceMonitor before checking them
		if (!dataInitCachePopulated && !serviceStatusClients.isEmpty()) {

			boolean allServicesReady = true;

			List<ServiceStatus> serviceStatuses = getServiceStatus();

			for(ServiceStatus serviceStatus : serviceStatuses) {
				logger.info("OffThread Service Check: Service Name:{} Status: {}", serviceStatus.getServiceName(), serviceStatus.getServiceStatus());

				//log cache status
				if(serviceStatus instanceof CacheServiceStatus) {
					List<CacheStatus> cacheStatuses = ((CacheServiceStatus) serviceStatus).getCacheStatuses();
					if (cacheStatuses != null && !cacheStatuses.isEmpty()) {
						logger.info("Cached elements for {}:", serviceStatus.getServiceName());
						for (CacheStatus cacheStatus : cacheStatuses) {
							logger.info("Cache: {}.{} Number of elements {}", serviceStatus.getServiceName(),
									cacheStatus.getCacheType(), cacheStatus.getCachedElements());
						}
					}
				}

				if(!"Started".equals(serviceStatus.getServiceStatus())) {
					allServicesReady = false;
				}
			}

			if (allServicesReady) {
				dataInitCachePopulated = true;
				logger.info("Startup Complete: Static codes and all off thread caches have populated.");
			} else {
				logger.info("Startup Incomplete: Static codes or caches still loading");
			}
		}

		logger.debug("Finished checking cache status of Data Initialization services : {}", dataInitCachePopulated);

		return dataInitCachePopulated;
	}

	/*
     * Each registered client will be looped over in getServiceStatus().  Each Service client registers with Service
     * Monitor by calling this method.
     */
	@Override
	public void register(IServiceStatusClient iServiceStatusClient) {
		serviceStatusClients.add(iServiceStatusClient);
	}

}
