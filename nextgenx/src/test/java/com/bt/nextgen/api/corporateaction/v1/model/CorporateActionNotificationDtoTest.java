package com.bt.nextgen.api.corporateaction.v1.model;

import java.math.BigInteger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionNotificationDtoTest {
	@Test
	public void testCorporateActionNotificationDto() {
	    // Useless test.  Currently unused.
        CorporateActionNotificationDto dto1 = new CorporateActionNotificationDto();
        CorporateActionNotificationDto dto2 = new CorporateActionNotificationDto(CorporateActionSendNotificationStatus.OK, null);

        dto1.setNotificationCount(BigInteger.ONE);

        assertNull(dto1.getStatus());
        assertNull(dto1.getAttachments());
        assertNull(dto1.getKey());
        assertEquals(BigInteger.ONE, dto1.getNotificationCount());
        assertEquals(CorporateActionSendNotificationStatus.OK, dto2.getStatus());
	}
}
