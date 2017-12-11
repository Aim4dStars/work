package com.bt.nextgen.api.account.v3.model;

import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class MigrationDetailsDtoTest {

    @Test
    public void getMigrationDetailsDto() {
        MigrationDetailsDto dto = new MigrationDetailsDto();
        dto.setSourceId("wrap");
        dto.setMigrationDate(new DateTime());
        dto.setAccountId("M12345678");

        assertEquals(dto.getSourceId(),"wrap");
        assertEquals(dto.getAccountId(),"M12345678");
        assertNotNull(dto.getMigrationDate());
    }

}
