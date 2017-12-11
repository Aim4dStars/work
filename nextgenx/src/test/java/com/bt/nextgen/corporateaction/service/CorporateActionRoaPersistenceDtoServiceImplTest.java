package com.bt.nextgen.corporateaction.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.bt.nextgen.api.corporateaction.v1.model.CorporateActionPersistenceDto;
import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionRoaPersistenceDtoServiceImpl;

import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionRoaPersistenceDtoServiceImplTest {
    @InjectMocks
    private CorporateActionRoaPersistenceDtoServiceImpl corporateActionRoaPersistenceDtoServiceImpl;

    @Test
    public void test() {
        assertNotNull(corporateActionRoaPersistenceDtoServiceImpl.submit(new CorporateActionPersistenceDto(), null));
    }
}
