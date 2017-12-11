package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.ANZSICCodeDto;
import com.btfin.panorama.service.exception.FailFastErrorsImpl;
import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.code.Code;
import com.bt.nextgen.service.integration.code.CodeCategoryInterface;
import com.bt.nextgen.service.integration.code.MockStaticIntegrationService;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.btfin.panorama.core.conversion.CodeCategory.ANZSIC_DIVISION;
import static com.btfin.panorama.core.conversion.CodeCategory.ANZSIC_GROUP;
import static com.btfin.panorama.core.conversion.CodeCategory.ANZSIC_INDUSTRY;
import static com.btfin.panorama.core.conversion.CodeCategory.ANZSIC_SUBDIVISION;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ANZSICCodeDtoServiceImplTest.Config.class)
public class ANZSICCodeDtoServiceImplTest {

    private static final int CODES_SIZE = 469;

    @Autowired
    private ANZSICCodeDtoService anzsicCodeDtoService;

    @Autowired
    private StaticIntegrationService staticIntegrationService;

    private ServiceErrors errors;

    @Before
    public void initErrors() {
        this.errors = new FailFastErrorsImpl();
    }

    @Test
    public void loadCodesWillLoadAllFourAnzsicResources() {
        final Map<CodeCategoryInterface, Map<String, Code>> map = staticIntegrationService.loadCodes(errors);
        Set<CodeCategoryInterface> set = new HashSet();
        set.add(ANZSIC_DIVISION);
        set.add(ANZSIC_SUBDIVISION);
        set.add(ANZSIC_GROUP);
        set.add(ANZSIC_INDUSTRY);
        assertThat(map.size(), is(greaterThanOrEqualTo(set.size())));
        assertTrue(map.keySet().containsAll(set));
    }

    @Test
    public void findAll() {
        final List<ANZSICCodeDto> codes = anzsicCodeDtoService.findAll(errors);
        assertNotNull(codes);
        assertThat(codes.size(), is(CODES_SIZE));
        assertAnzsicCode(codes.get(0), "5710", "5710", "SIC 0317", "Accommodation, Cafes and Restaurants",
                "Accommodation", "Accommodation", "Accommodation (5710)");
        assertAnzsicCode(codes.get(CODES_SIZE - 1), "4721", "4721", "SIC 0265", "Wholesale Trade",
                "Personal & Household Good Wholesaling", "Textile, Clothing & Footwear Wholesaling",
                "Textile product wholesaling (4721)");
    }

    private static void assertAnzsicCode(ANZSICCodeDto anzsic, String key, String code, String ucmCode,
            String division, String subdivision, String group, String industry) {
        assertThat(anzsic.getKey(), is(key));
        assertThat(anzsic.getCode(), is(code));
        assertThat(anzsic.getUcmCode(), is(ucmCode));
        assertThat(anzsic.getIndustryDivision(), is(division));
        assertThat(anzsic.getIndustrySubdivision(), is(subdivision));
        assertThat(anzsic.getIndustryGroup(), is(group));
        assertThat(anzsic.getIndustryClass(), is(industry));
    }

    @Configuration
    public static class Config {

        @Bean
        public StaticIntegrationService staticIntegrationService() {
            return new MockStaticIntegrationService();
        }

        @Bean
        public ANZSICCodeDtoService anzsicCodeDtoService() {
            return new ANZSICCodeDtoServiceImpl();
        }
    }
}
