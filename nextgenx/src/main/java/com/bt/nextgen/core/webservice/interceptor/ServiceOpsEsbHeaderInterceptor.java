package com.bt.nextgen.core.webservice.interceptor;

import au.com.westpac.gn.utility.xsd.esbheader.v3.ChannelAttributes;
import au.com.westpac.gn.utility.xsd.esbheader.v3.LocationType;
import au.com.westpac.gn.utility.xsd.esbheader.v3.Requester;

import com.bt.nextgen.core.security.profile.InvestorProfileService;
import com.bt.nextgen.core.security.profile.UserProfileServiceSpringImpl;
import com.btfin.panorama.core.security.profile.UserProfileService;

import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by L069679 on 16/01/2017.
 */
public class ServiceOpsEsbHeaderInterceptor extends EsbHeaderAdderInterceptor implements ApplicationContextAware {
	public static final String BTPL_BRAND_SILO = "BTPL";
	private static final String WPAC_BSB_VALUE = "032909";
	private static final String BTPL_BSB_VALUE = "032949";

	private UserProfileService profileService;

	private BeanFactory beans;

	private static final Logger LOGGER = getLogger(ServiceOpsEsbHeaderInterceptor.class);

	/**
	 * Save a reference to the application context. Note that this is all we do here, we do NOT go enquiring for
	 * service beans at this point in the application lifecycle, as Spring auto-wiring has (apparently) yet to fully
	 * complete and sort itself out at this stage.
	 *
	 * @param context the application context.
	 */
	@Override
	public synchronized void setApplicationContext(ApplicationContext context) {
		this.beans = context;
	}

	@Override
	protected void setChannelAttributes(ChannelAttributes channel) {
		super.setChannelAttributes(channel);
		if (null != ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
			if (null != request && null != request.getParameter("silo")) {
				channel.setBrandSilo(request.getParameter("silo"));
			} else if (null != request && null != request.getAttribute("silo-movement")) {
				LOGGER.info("Silo Movement brand silo set to: " + (String) request.getAttribute("silo-movement"));
				channel.setBrandSilo((String) request.getAttribute("silo-movement"));
			}

		}

		channel.setOrganisationId(WPAC_BRAND_SILO);
		if((BTPL_BRAND_SILO).equalsIgnoreCase(channel.getBrandSilo())) {
			channel.getLocation().add(location(BTPL_BSB_VALUE, LocationType.BSB));
		} else {
			channel.getLocation().add(location(WPAC_BSB_VALUE, LocationType.BSB));
		}
	}

    /**
     * Overridden to add a requester ID.
     *
     * @param attributes current set of channel attributes.
     * @return the appropriate requester type.
     */
    @Override
    protected Requester requester(ChannelAttributes attributes) {
        final Requester requester = super.requester(attributes);
        // TODO: Need to modify according to the request. Hardcoding for testing purpose
        requester.setRequesterId("12345");
        return requester;
    }
}
