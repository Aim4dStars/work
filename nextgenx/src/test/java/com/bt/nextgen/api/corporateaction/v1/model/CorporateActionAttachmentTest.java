package com.bt.nextgen.api.corporateaction.v1.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class CorporateActionAttachmentTest {
	@Test
	public void testCorporateActionAttachment() {
	    // Useless test but needs to be covered as it is currently unused due to deferral in project (maybe should be cleaned up)
        CorporateActionAttachment attachment = new CorporateActionAttachment("0", "Name", 100L, CorporateActionAttachmentStatus.OK);

        assertEquals("0", attachment.getId());
        assertEquals("Name", attachment.getName());
        assertEquals(100L, attachment.getSize());
        assertEquals(CorporateActionAttachmentStatus.OK, attachment.getStatus());
	}
}
