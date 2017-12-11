CREATE OR REPLACE PACKAGE communication
AS
    PROCEDURE set_onboarding_communication (communication_id     IN VARCHAR2,
                                            application_id       IN VARCHAR2,
                                            gcm_pan              IN VARCHAR2,
                                            communication_status IN VARCHAR2,
                                            email_address        IN VARCHAR2,
                                            initiation_time      IN TIMESTAMP,
                                            tracking_id          IN VARCHAR2,
                                            failure_message      IN VARCHAR2,
                                            status               OUT INTEGER);

    PROCEDURE get_onboarding_communications( from_ts IN TIMESTAMP,
                                            to_ts IN TIMESTAMP,
                                            communication_result_set OUT SYS_REFCURSOR );

END COMMUNICATION;