DECLARE
PROCEDURE prc_notice_type_ref(p_notice_id VARCHAR2, p_version INTEGER, p_last_updated_on DATE) IS
BEGIN
    INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on)
    SELECT p_notice_id, p_version, p_last_updated_on FROM DUAL
    WHERE NOT EXISTS (SELECT * FROM NOTICE_TYPE_REF WHERE notice_id = p_notice_id and version = p_version);
END prc_notice_type_ref;

BEGIN
    prc_notice_type_ref('TERMS_OF_USE' , 1 , to_date('01-06-2016','dd-mm-yyyy'));
    prc_notice_type_ref('PDS' , 1 , to_date('01-06-2016','dd-mm-yyyy'));
    prc_notice_type_ref('PDS_DIRECT' , 1 , to_date('01-01-2017','dd-mm-yyyy'));
    prc_notice_type_ref('PDS_DIRECT_SUPER' , 1 , to_date('01-01-2017','dd-mm-yyyy'));
    prc_notice_type_ref('PDS_ADVISED_SUPER' , 1 , to_date('01-01-2017','dd-mm-yyyy'));
    prc_notice_type_ref('PDS_ADVISED_INVESTOR' , 1 , to_date('01-01-2017','dd-mm-yyyy'));
    prc_notice_type_ref('WHATS_NEW_DIRECT' , 1 , to_date('01-01-2017','dd-mm-yyyy'));
    prc_notice_type_ref('WHATS_NEW_DIRECT_SUPER' , 1 , to_date('01-08-2017','dd-mm-yyyy'));
    prc_notice_type_ref('WHATS_NEW_ADVISED_SUPER' , 1 , to_date('01-01-2017','dd-mm-yyyy'));
    prc_notice_type_ref('WHATS_NEW_ADVISED_INVESTOR' , 1 , to_date('01-01-2017','dd-mm-yyyy'));
END;