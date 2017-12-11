package com.bt.nextgen.service.integration.base;

import org.joda.time.DateTime;
import org.junit.Test;

import static com.bt.nextgen.service.integration.base.SystemType.ASGARD;
import static com.bt.nextgen.service.integration.base.SystemType.WRAP;
import static org.junit.Assert.assertEquals;

/**
 * Created by L075207 on 28/09/2017.
 */
public class ThirdPartyDetailsTest {

    @Test
    public void gettersSettersForThirdPartyDetails() {
        final DateTime migrationDate = new DateTime();
        ThirdPartyDetails thirdPartyDetails = new ThirdPartyDetails();

        //set the third party details
        thirdPartyDetails.setMigrationDate(migrationDate);
        thirdPartyDetails.setMigrationKey("M00721465");
        thirdPartyDetails.setSystemType(WRAP);

        //get the third party details
        assertEquals(thirdPartyDetails.getMigrationDate(), migrationDate);
        assertEquals(thirdPartyDetails.getMigrationKey(), "M00721465");
        assertEquals(thirdPartyDetails.getSystemType(), WRAP);

        //if the system type is ASGARD
        thirdPartyDetails.setSystemType(ASGARD);
        assertEquals(thirdPartyDetails.getSystemType(), ASGARD);
    }

}