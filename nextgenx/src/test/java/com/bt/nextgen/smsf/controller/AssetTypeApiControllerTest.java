/*
 * package com.bt.nextgen.smsf.controller;
 * 
 * import com.bt.nextgen.config.TestConfig; import
 * com.bt.nextgen.core.security.Roles; import
 * com.btfin.panorama.core.security.saml.SamlToken; import
 * com.btfin.panorama.core.security.profile.Profile; import
 * com.bt.nextgen.util.SamlUtil; import org.junit.Before; import
 * org.junit.Ignore; import org.junit.Test; import org.junit.runner.RunWith;
 * import org.springframework.beans.factory.annotation.Autowired; import
 * org.springframework.security.authentication.TestingAuthenticationToken;
 * import org.springframework.security.core.context.SecurityContextHolder;
 * import org.springframework.test.context.ContextConfiguration; import
 * org.springframework.test.context.junit4.SpringJUnit4ClassRunner; import
 * org.springframework.test.context.web.WebAppConfiguration; import
 * org.springframework.web.context.WebApplicationContext; import
 * org.springframework.web.servlet.config.annotation.EnableWebMvc;
 * 
 * import org.springframework.test.web.servlet.MockMvc; import static
 * org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; import
 * static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
 * import org.springframework.test.web.servlet.setup.MockMvcBuilders;
 * 
 * @RunWith(SpringJUnit4ClassRunner.class)
 * 
 * @WebAppConfiguration
 * 
 * @ContextConfiguration(classes = { TestConfig.class })
 * 
 * @EnableWebMvc public class AssetTypeApiControllerTest { private MockMvc
 * mockMvc;
 * 
 * @Autowired private WebApplicationContext wac;
 * 
 * 
 * @Before public void setup() { this.mockMvc =
 * MockMvcBuilders.webAppContextSetup(this.wac).build(); }
 * 
 * 
 * @Ignore
 * 
 * @Test // TODO: How do we perform integration testing with cached static codes
 * public void retrieveAssetTypes() throws Exception {
 * mockMvc.perform(get("/secure/api/v1_0/smsf/assetTypes"))
 * //.headers(generateDefaultHttpRequestParams()) //.param("deviceToken",
 * "version%3D1%26pm%5Ffpua%3Dmozilla%2F5%2E0%20%28windows%20nt%206%2E1%3B%20wow64%3B%20rv%3A25%2E0%29%20gecko%2F20100101%20firefox%2F25%2E0%7C5%2E0%20%28Windows%29%7CWin32%26pm%5Ffpsc%3D24%7C1680%7C1050%7C1010%26pm%5Ffpsw%3D%7Cdsw%7Cpdf%7Cpdf%7Cqt5%7Cqt4%7Cqt3%7Cqt2%7Cqt1%26pm%5Ffptz%3D10%26pm%5Ffpln%3Dlang%3Den%2DUS%7Csyslang%3D%7Cuserlang%3D%26pm%5Ffpjv%3D1%26pm%5Ffpco%3D1"
 * ) .andExpect(status().is(200))
 * .andExpect(content().contentType("application/json;charset=UTF-8"))
 * .andExpect(content().string("{\"success\":true,\"data\":true}")); } }
 */