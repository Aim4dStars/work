package com.bt.nextgen.config;

import com.bt.nextgen.core.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by m035652 on 4/07/14.
 */
@Component
public class EnvironmentConfirmation  implements ApplicationListener<ContextRefreshedEvent> {

    private static Logger logger = LoggerFactory.getLogger(EnvironmentConfirmation.class);

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event)
    {
        logger.info("Panorama UI version {} started", Properties.getString("nextgen.version"));
    }

}

