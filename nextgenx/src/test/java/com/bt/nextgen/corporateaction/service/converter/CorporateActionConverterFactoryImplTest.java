package com.bt.nextgen.corporateaction.service.converter;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import com.bt.nextgen.api.corporateaction.v1.service.CorporateActionHelper;
import com.bt.nextgen.api.corporateaction.v1.service.EffectiveCorporateActionType;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionConverterFactoryImpl;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionRequestConverterService;
import com.bt.nextgen.api.corporateaction.v1.service.converter.CorporateActionResponseConverterService;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockRequestConverterServiceImpl;
import com.bt.nextgen.api.corporateaction.v1.service.converter.MultiBlockResponseConverterServiceImpl;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionDetails;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionOfferType;
import com.bt.nextgen.service.integration.corporateaction.CorporateActionType;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionConverterFactoryImplTest {
    @InjectMocks
    private CorporateActionConverterFactoryImpl corporateActionConverterFactory;

    @Mock
    private CorporateActionHelper corporateActionHelper;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private CorporateActionDetails corporateActionDetails;

    @Mock
    private EffectiveCorporateActionType effectiveCorporateActionType;

    @Before
    public void setup() {
        Map<String, Object> convertersMap = new HashMap<>();
        MultiBlockResponseConverterServiceImpl dummyResponseService = mock(MultiBlockResponseConverterServiceImpl.class);
        MultiBlockRequestConverterServiceImpl dummyRequestService = mock(MultiBlockRequestConverterServiceImpl.class);

        convertersMap.put("CA_MULTI_BLOCK_RESPONSE", dummyResponseService);
        convertersMap.put("CA_EXERCISE_RIGHTS_RESPONSE", dummyResponseService);
        convertersMap.put("CA_MULTI_BLOCK_CONVERSION_RESPONSE", dummyResponseService);
        convertersMap.put("CA_EXERCISE_RIGHTS_RESPONSE", dummyResponseService);
        convertersMap.put("CA_MANDATORY_RESPONSE", dummyResponseService);
        convertersMap.put("CA_MULTI_BLOCK_REQUEST", dummyRequestService);

        when(corporateActionHelper.getEffectiveCorporateActionType(any(CorporateActionDetails.class))).thenReturn
                (effectiveCorporateActionType);

        when(applicationContext.getBeansWithAnnotation(any(Class.class))).thenReturn(convertersMap);
    }

    @Test
    public void testGetResponseConverterService_whenNoSpecificClassWithOfferType_thenReturnResponseConverterService() {
        when(effectiveCorporateActionType.getType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.PUBLIC_OFFER);

        corporateActionConverterFactory.setApplicationContext(applicationContext);

        CorporateActionResponseConverterService converterService = corporateActionConverterFactory.getResponseConverterService
                (corporateActionDetails);

        assertNotNull(converterService);
        assertTrue(converterService instanceof CorporateActionResponseConverterService);
    }

    @Test
    public void testGetResponseConverterService_whenClassWithOfferTypeExist_thenReturnResponseConverterService() {
        when(effectiveCorporateActionType.getType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.CONVERSION);

        corporateActionConverterFactory.setApplicationContext(applicationContext);

        CorporateActionResponseConverterService converterService = corporateActionConverterFactory.getResponseConverterService
                (corporateActionDetails);

        assertNotNull(converterService);
    }

    @Test
    public void testGetResponseConverterService_whenExerciseRights_thenReturnResponseConverterService() {
        when(effectiveCorporateActionType.getType()).thenReturn(CorporateActionType.EXERCISE_RIGHTS);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(null);

        corporateActionConverterFactory.setApplicationContext(applicationContext);

        CorporateActionResponseConverterService converterService = corporateActionConverterFactory.getResponseConverterService
                (corporateActionDetails);

        assertNotNull(converterService);
    }

    @Test
    public void testGetResponseConverterService_whenMandatoryCorporateActionType_thenReturnResponseConverterService() {
        when(effectiveCorporateActionType.getType()).thenReturn(CorporateActionType.ASSIMILATION_FRACTION);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(null);

        corporateActionConverterFactory.setApplicationContext(applicationContext);

        CorporateActionResponseConverterService converterService = corporateActionConverterFactory.getResponseConverterService
                (corporateActionDetails);

        assertNotNull(converterService);
    }

    @Test
    public void testGetRequestConverterService_whenNoSpecificClassWithOfferType_thenReturnRequestConverterService() {
        when(effectiveCorporateActionType.getType()).thenReturn(CorporateActionType.MULTI_BLOCK);
        when(corporateActionDetails.getCorporateActionOfferType()).thenReturn(CorporateActionOfferType.PUBLIC_OFFER);

        corporateActionConverterFactory.setApplicationContext(applicationContext);

        CorporateActionRequestConverterService converterService = corporateActionConverterFactory.getRequestConverterService
                (corporateActionDetails);

        assertNotNull(converterService);
        assertTrue(converterService instanceof CorporateActionRequestConverterService);
    }
}