create or replace PACKAGE BODY communication
AS
    PROCEDURE set_onboarding_communication (communication_id     IN VARCHAR2,
                                            application_id       IN VARCHAR2,
                                            gcm_pan              IN VARCHAR2,
                                            communication_status IN VARCHAR2,
                                            email_address        IN VARCHAR2,
                                            initiation_time      IN TIMESTAMP,
                                            tracking_id          IN VARCHAR2,
                                            failure_message      IN VARCHAR2,
                                            status               OUT INTEGER)
    IS
        record_count    INTEGER;
        trimmed_message VARCHAR2(4000);
    BEGIN
        IF application_id IS NULL OR application_id = '' THEN
             RAISE_APPLICATION_ERROR('-20100', 'APPLICATION ID IS NULL OR EMPTY.');
        END IF;

        IF communication_id IS NULL OR communication_id = '' THEN
            RAISE_APPLICATION_ERROR('-20101', 'COMMUNICATION ID IS NULL OR EMPTY.');
        END IF;

        IF gcm_pan IS NULL OR gcm_pan = '' THEN
           RAISE_APPLICATION_ERROR('-20102', 'GCM PAN ID IS NULL OR EMPTY.');
        END IF;

        IF email_address IS NULL THEN
            RAISE_APPLICATION_ERROR('-20103', 'EMAIL ADDRESS CANNOT BE NULL.');
        END IF;

        IF initiation_time IS NULL THEN
            RAISE_APPLICATION_ERROR('-20104', 'COMMUNICATION INITIATION TIMESTAMP CANNOT BE NULL.');
        END IF;

        IF failure_message IS NOT NULL AND LENGTH(failure_message) > 4000 THEN
            trimmed_message := SUBSTR(failure_message,1,4000);
        ELSE
            trimmed_message := failure_message;
        END IF;

        INSERT INTO
            onboarding_communication
            (
            communication_id,
            onboarding_application_id,
            gcm_pan,
            status,
            email_address,
            communication_initiation_time,
            tracking_id,
            created_date,
            failure_message,
            last_modified_id,
            last_modified_date
            )
        VALUES
            (set_onboarding_communication.communication_id,
            application_id,
            set_onboarding_communication.gcm_pan,
            communication_status,
            set_onboarding_communication.email_address,
            initiation_time,
            set_onboarding_communication.tracking_id,
            CURRENT_TIMESTAMP,
            trimmed_message,
            USER,
            CURRENT_TIMESTAMP
            );

        status := 1;

    END set_onboarding_communication;

    PROCEDURE get_onboarding_communications( from_ts IN TIMESTAMP,
                                            to_ts IN TIMESTAMP,
                                            communication_result_set OUT SYS_REFCURSOR )
    IS
    BEGIN
    IF from_ts IS NULL OR to_ts IS NULL THEN
      RAISE_APPLICATION_ERROR(-20100,'FROM OR TO TIMESTAMP CANNOT BE NULL.');
    END IF;

    OPEN communication_result_set
    FOR SELECT
      communication_id,
      onboarding_application_id,
      gcm_pan,
      status,
      email_address,
      communication_initiation_time
    FROM
      onboarding_communication
    WHERE
      communication_initiation_time
      BETWEEN
      from_ts
      AND
      to_ts;

    END get_onboarding_communications;
END communication;