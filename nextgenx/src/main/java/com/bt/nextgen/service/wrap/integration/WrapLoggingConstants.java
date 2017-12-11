package com.bt.nextgen.service.wrap.integration;

/**
 * Constants to be used in logging
 *
 */
public interface WrapLoggingConstants {
    //
    String WRAP_INTEGRATION = "WRAP_INTEGRATION_LOGGING";
    String DELIMITER = "::";
    // use these from below in logging code
    String WRAP_INTEGRATION_TIMING = WRAP_INTEGRATION + DELIMITER + "TIMING" + DELIMITER;
    String WRAP_INTEGRATION_MAPPING = WRAP_INTEGRATION + DELIMITER + "MAPPING" + DELIMITER;

}
