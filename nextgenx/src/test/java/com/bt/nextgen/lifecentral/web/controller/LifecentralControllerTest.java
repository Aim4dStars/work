package com.bt.nextgen.lifecentral.web.controller;

import com.bt.nextgen.core.api.model.ApiResponse;
import com.bt.nextgen.core.util.ApplicationPropertiesImpl;
import com.bt.nextgen.lifecentral.model.LifecentralDto;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by M041926 on 5/08/2016.
 */
public class LifecentralControllerTest {

    private LifecentralController controller;

    @Before
    public void setup() {
        controller = new LifecentralController(new ApplicationPropertiesImpl());
    }

    @Test
    public void lifeCentralUrl() throws Exception {
        ApiResponse resp = controller.lifeCentralUrl(false);
        assertNotNull("expecting not null response", resp);
        LifecentralDto dto = (LifecentralDto) resp.getData();

        String lifeCentralURL = dto.getLifeCentralUrl();
        String eamLifeCentralURL = dto.getEamLifeCentralUrl();

        assertNotNull("expecting not null lifecentralURL", lifeCentralURL);
        assertNotNull("expecting not null eamLifeCentralUrl", eamLifeCentralURL);

        resp = controller.lifeCentralUrl(true);
        dto = (LifecentralDto) resp.getData();

        assertNotNull("expecting not null lifecentralURL", dto.getLifeCentralUrl());
        assertNotNull("expecting not null eamLifeCentralUrl", dto.getEamLifeCentralUrl());

        assertNotEquals(lifeCentralURL, dto.getLifeCentralUrl());
        assertNotEquals(eamLifeCentralURL, dto.getEamLifeCentralUrl());
    }

}