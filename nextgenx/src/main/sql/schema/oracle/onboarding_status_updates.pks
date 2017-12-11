create or replace PACKAGE onboarding_status_updates
AS
    PROCEDURE set_onboarding_app_status (application_id IN VARCHAR2,application_status IN VARCHAR2, FAILURE_MSG IN VARCHAR2, status OUT INTEGER);
    PROCEDURE set_onboarding_party_status (application_id IN VARCHAR2, gcm_pan_var IN VARCHAR2,  party_status IN VARCHAR2, FAILURE_MSG IN VARCHAR2, status OUT INTEGER);
    PROCEDURE set_onboarding_comm_status (communication_id IN VARCHAR2, communication_status IN VARCHAR2, FAILURE_MSG IN VARCHAR2, status OUT INTEGER);
END onboarding_status_updates;
/