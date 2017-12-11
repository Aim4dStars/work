package com.bt.nextgen.api.modelportfolio.v2.util.common;

import com.bt.nextgen.service.ServiceErrors;
import com.bt.nextgen.service.integration.broker.BrokerIntegrationService;
import com.btfin.panorama.core.security.profile.UserProfileService;
import com.btfin.panorama.service.integration.broker.Broker;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class AdviserModelHelper {

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private BrokerIntegrationService brokerService;

    private static final String BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final int SECONDS = 6;
    public static final int MINUTES = 5;
    

    public String generateUniqueModelId(String prefix, ServiceErrors serviceErrors) {
        return generateUniqueModelId(prefix, SECONDS, serviceErrors);
    }

    public String generateUniqueModelId(String prefix, int modifier, ServiceErrors serviceErrors) {

        // 3 characters from position name, i.e. broker name.
        Broker dealerBroker = userProfileService.getDealerGroupBroker();

        String posName = "ZZZ";
        if (dealerBroker != null) {
            posName = dealerBroker.getPositionName().trim().replace(" ", "");
        }

        if (posName.length() < 3) {
            posName = posName + "ZZZ";
        }
        StringBuilder builder = new StringBuilder(prefix.substring(0, 2));
        builder = builder.append(posName.toUpperCase().substring(0, 3)).append(generateTimeStamp(modifier));
        return builder.toString();
    }
    
    protected synchronized String generateTimeStamp(int modifier) {
        long currentSec = DateTime.now().getMillis()/1000;
        long sec = currentSec % Double.doubleToRawLongBits(Math.pow(BASE36.length(), SECONDS));
        
        // Convert
        final byte b = Integer.valueOf(BASE36.length()).byteValue();

        StringBuilder builder = new StringBuilder();
        while(sec > 0) {
            Long a = Long.valueOf(sec - (b * (sec / b)));
            builder = builder.insert(0, BASE36.substring(a.intValue(), a.intValue() + 1));
            sec = sec / b;
        }
        SecureRandom sr = new SecureRandom();
        return builder.append(sr.nextInt(10)).toString();
    }
}
