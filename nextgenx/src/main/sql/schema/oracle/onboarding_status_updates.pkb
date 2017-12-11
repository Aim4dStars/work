create or replace PACKAGE BODY onboarding_status_updates
AS
    FUNCTION get_trimmed_failure_message (failure_message IN VARCHAR2) RETURN VARCHAR2;
    PROCEDURE set_onboarding_app_status (application_id IN VARCHAR2, application_status IN VARCHAR2, FAILURE_MSG IN VARCHAR2, status OUT INTEGER)
    IS
        application_count       INTEGER;
        trimmed_message         VARCHAR2(4000);
    BEGIN
        status := 0;
        -- 'UPDATE APPLICATION STATUS IF APPLICATION FOUND'
        IF application_id IS NOT NULL THEN
            trimmed_message := get_trimmed_failure_message(failure_msg);
            SELECT COUNT ( id ) INTO application_count
            FROM onboarding_application
            WHERE id = application_id;
            IF application_count = 1 THEN
                UPDATE onboarding_application
                SET status = application_status,
                    failure_message = trimmed_message,
                    last_modified_id = USER,
                    last_modified_date = CURRENT_TIMESTAMP
                WHERE id = application_id;
            ELSE
                raise_application_error('-20100', 'ONBOARDING APPLICATION ID CANNOT BE FOUND. APPLICATION ID:' || application_id);
            END IF;
        ELSE
            raise_application_error('-20101', 'APPLICATION ID IS NULL.');
        END IF;
        status := 1;
    END set_onboarding_app_status;
    PROCEDURE set_onboarding_party_status (application_id IN VARCHAR2, gcm_pan_var IN VARCHAR2, party_status IN VARCHAR2, FAILURE_MSG IN VARCHAR2, status OUT INTEGER)
    IS
        party_count     INTEGER;
        trimmed_message VARCHAR2(4000);
    BEGIN
        status := 0;
        -- 'UPDATE PARTY STATUS IF APPLICATION FOUND'
        IF application_id IS NOT NULL AND gcm_pan_var IS NOT NULL THEN
            trimmed_message := get_trimmed_failure_message(failure_msg);
            SELECT COUNT (*) INTO party_count
            FROM onboarding_party
            WHERE onboarding_application_id = application_id
                AND gcm_pan = gcm_pan_var;
            IF party_count = 1 THEN
                UPDATE onboarding_party
                SET status = party_status,
                    failure_message = trimmed_message,
                    last_modified_id = USER,
                    last_modified_date = CURRENT_TIMESTAMP
                WHERE onboarding_application_id = application_id
                    AND gcm_pan = gcm_pan_var;
            ELSE
                raise_application_error('-20106', 'PARTY CANNOT BE FOUND. APPLICATION ID :' || application_id || ' AND GCM PAN:' || gcm_pan_var);
            END IF;
        ELSE
            raise_application_error('-20107', 'GCM PAN OR APPLICATION ID IS NULL');
        END IF;
        status := 1;
    END set_onboarding_party_status;

    PROCEDURE set_onboarding_comm_status (communication_id IN VARCHAR2, communication_status IN VARCHAR2, failure_msg IN VARCHAR2, status OUT INTEGER)
    IS
    trimmed_message VARCHAR2(4000);
    BEGIN
        status := 0;
        trimmed_message := get_trimmed_failure_message(failure_msg);
        -- 'UPDATE COMMUNICATION STATUS IF COMMUNICATION FOUND'
        IF communication_id IS NOT NULL THEN
            UPDATE onboarding_communication
            SET status = communication_status,
                failure_message = trimmed_message,
                last_modified_id = USER,
                last_modified_date = CURRENT_TIMESTAMP
            WHERE communication_id = set_onboarding_comm_status.communication_id;
            IF SQL%ROWCOUNT = 0 THEN
                raise_application_error('-20108', 'COMMUNICATION ID - ' || set_onboarding_comm_status.communication_id || ' IS NOT FOUND');
            END IF;
        ELSE
            raise_application_error('-20109', 'COMMUNICATION ID IS NULL');
        END IF;
        status := 1;
    END set_onboarding_comm_status;

    FUNCTION get_trimmed_failure_message (failure_message IN VARCHAR2) RETURN VARCHAR2
    AS
    BEGIN
        IF failure_message IS NOT NULL AND LENGTH(failure_message) > 4000 THEN
          RETURN SUBSTR(failure_message,1,4000);
        ELSE
          RETURN failure_message;
        END IF;
    END get_trimmed_failure_message;
END onboarding_status_updates;
/