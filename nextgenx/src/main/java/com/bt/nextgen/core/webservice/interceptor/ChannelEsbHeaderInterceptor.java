package com.bt.nextgen.core.webservice.interceptor;

import au.com.westpac.gn.utility.xsd.esbheader.v3.ChannelAttributes;

/**
 * Created by L075208 on 8/07/2016.
 */
public class ChannelEsbHeaderInterceptor extends ApplicationSubmissionEsbHeaderInterceptor{

    @Override
    protected void setChannelAttributes(ChannelAttributes channel) {
        super.setChannelAttributes(channel);
        channel.setOrganisationId(WPAC_BRAND_SILO);
    }
}