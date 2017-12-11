package com.bt.nextgen.api.corporateaction.v1.model;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionAccountElectionsDtoImplTest {
    @Test
    public void testCorporateActionAccountElectionsDtoImpl() {
        List<CorporateActionAccountElectionDto> accountElectionDtos = new ArrayList<>();

        CorporateActionAccountElectionsDtoImpl dto1 = new CorporateActionAccountElectionsDtoImpl();
        CorporateActionAccountElectionsDtoImpl dto2 = new CorporateActionAccountElectionsDtoImpl(accountElectionDtos);

        assertNull(dto1.getOptions());
        assertNotNull(dto2.getOptions());
    }
}
