package com.bt.nextgen.api.draftaccount.service;

import com.bt.nextgen.api.draftaccount.model.PreservationAgeDto;
import com.bt.nextgen.service.ServiceErrors;
import com.btfin.panorama.service.exception.ServiceErrorsImpl;
import com.bt.nextgen.service.avaloq.code.CodeImpl;
import com.bt.nextgen.service.integration.code.Code;
import com.btfin.panorama.core.conversion.CodeCategory;
import com.bt.nextgen.service.integration.code.StaticIntegrationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.bt.nextgen.api.draftaccount.util.XMLGregorianCalendarUtil.date;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

/**
 * Created by F058391 on 15/06/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class PreservationAgeServiceImplTest {

    @Mock
    StaticIntegrationService staticIntegrationService;

    @InjectMocks
    PreservationAgeServiceImpl service;

    @Test
    public void shouldReturnPreservationAges() {

        final CodeImpl preservationAge57 = getCode("Preservation age equals 57 years.", "PSV_AGE_57", "57", "19610701", "19620630");
        final List<Code> codeList = Arrays.<Code>asList(preservationAge57);
        when(staticIntegrationService.loadCodes(eq(CodeCategory.PRESERVATION_AGE), any(ServiceErrors.class))).thenReturn(codeList);
        final List<PreservationAgeDto> preservationAges = service.findAll(new ServiceErrorsImpl());
        assertThat(preservationAges.size(), is(1));
        final PreservationAgeDto preservationAgeDto = preservationAges.get(0);
        assertThat(preservationAgeDto.getAge(), is(57));

        assertThat(preservationAgeDto.getBirthDateFrom(), is("19610701"));
        assertThat(preservationAgeDto.getBirthDateTo(), is("19620630"));
    }

    private CodeImpl getCode(String name, String psvAge57, String value, String from, String to) {
        final CodeImpl code = new CodeImpl();
        code.setName(name);
        code.setCodeId(psvAge57);
        code.addField("psv_age", value);
        code.addField("birth_date_from", from);
        code.addField("birth_date_to", to);
        return code;
    }

}