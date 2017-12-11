CREATE OR REPLACE PACKAGE onboarding_id_updates
AS
    PROCEDURE set_onboarding_correlation_ids (application_id IN VARCHAR2,
                                              correlation_data_xml in CLOB,
                                              status OUT INTEGER);
END onboarding_id_updates;
/