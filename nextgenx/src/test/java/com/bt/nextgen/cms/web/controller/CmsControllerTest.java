package com.bt.nextgen.cms.web.controller;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Test cases for {@link CmsController}
 * Created by M044020 on 11/09/2017.
 */
public class CmsControllerTest {
    @Test
    public void cmsServerUnavailable() throws Exception {
        CmsController cmsController = new CmsController();
        ResponseEntity<String> result = cmsController.cmsServerUnavailable();
        assertThat(result.getStatusCode(), is(HttpStatus.SERVICE_UNAVAILABLE));
        assertThat(result.getBody(), is(nullValue()));
    }
}