create or replace PACKAGE BODY onboarding_id_updates
AS
    ob_correlation_input_xml_ns VARCHAR2(200);
    FUNCTION get_correlation_xml_from_clob (correlation_data_xml IN CLOB) RETURN XMLTYPE;
    PROCEDURE set_app_correlation_id (application_id IN VARCHAR, avaloq_order_id_var IN VARCHAR);
    PROCEDURE set_party_correlation_ids (application_id in VARCHAR2, parties IN XMLTYPE);
    PROCEDURE set_account_correlation_ids (application_id IN VARCHAR2, accounts IN XMLTYPE);

    PROCEDURE set_onboarding_correlation_ids (application_id IN VARCHAR2, correlation_data_xml in CLOB, status OUT INTEGER)
    IS
        xmlObject               XMLTYPE;
        schemaDOC               VARCHAR2(5000);
        schemaURL               VARCHAR2(30);
        application_count       INTEGER;
        temp_count              INTEGER;
        address_count           INTEGER;
    BEGIN
        ob_correlation_input_xml_ns := 'xmlns="ns://private.btfin.com/Panorama/Onboarding/IDUpdate/V1_0"';
        status    := 0;
        xmlObject := get_correlation_xml_from_clob(correlation_data_xml);
        FOR app IN (
                SELECT
                    ExtractValue(VALUE(application), 'application/application_correlation_id/text()',ob_correlation_input_xml_ns) AS application_correlation_id,
                    ExtractValue(VALUE(application), 'application/avaloq_order_id/text()',ob_correlation_input_xml_ns)            AS avaloq_order_id,
                    Extract(VALUE(application), 'application/accounts',ob_correlation_input_xml_ns)                               AS accounts,
                    Extract(VALUE(application), 'application/parties',ob_correlation_input_xml_ns)                                AS parties
                FROM
                TABLE ( XMLSequence ( Extract(xmlObject, '/application',ob_correlation_input_xml_ns) ) ) application
                )
        LOOP
            set_app_correlation_id(app.application_correlation_id, app.avaloq_order_id);
            set_account_correlation_ids(app.application_correlation_id, app.accounts);
            set_party_correlation_ids(app.application_correlation_id, app.parties);
        END LOOP;
        commit;
        status := 1;
    EXCEPTION
        WHEN OTHERS THEN
            rollback;
            raise;
    END set_onboarding_correlation_ids;

    FUNCTION get_correlation_xml_from_clob (correlation_data_xml IN CLOB)
    RETURN XMLTYPE
    AS
        xmlObject       XMLTYPE;
        schemaURL       VARCHAR2(30);
    BEGIN
        schemaURL := 'onboarding-correlation-ids.xsd';
        dbms_xmlschema.compileSchema(schemaURL);
        xmlObject := XMLTYPE(correlation_data_xml).createSchemaBasedXML(schemaURL);
        xmlObject.schemaValidate();
        RETURN xmlObject;
    END get_correlation_xml_from_clob;

    PROCEDURE set_app_correlation_id (application_id IN VARCHAR, avaloq_order_id_var IN VARCHAR)
    IS
        application_count       INTEGER;
    BEGIN
        dbms_output.put_line(application_id);
        IF avaloq_order_id_var IS NOT NULL AND application_id IS NOT NULL
        THEN
            SELECT
                COUNT ( id )
            INTO application_count
            FROM onboarding_application
            WHERE id = application_id;
            IF application_count = 1
            THEN
                UPDATE onboarding_application
                SET avaloq_order_id = avaloq_order_id_var,
                    status = null,
                    failure_message = null,
                    last_modified_id = USER,
                    last_modified_date = CURRENT_TIMESTAMP
                WHERE id = application_id;
            ELSE
                raise_application_error('-20100', 'ONBOARDING APPLICATION ID CANNOT BE FOUND. APPLICATION ID: ' || application_id);
            END IF;
        ELSE
            raise_application_error('-20101', 'AVALOQ ORDER ID AND APPLICATION ID CANNOT BE NULL OR EMPTY');
        END IF;
    END set_app_correlation_id;

    PROCEDURE set_account_correlation_ids (application_id IN VARCHAR2, accounts IN XMLTYPE)
    IS
        temp_count      INTEGER;
    BEGIN
        FOR account_row IN
                (SELECT
                     ExtractValue(Value(account), 'account/account_correlation_seq/text()',ob_correlation_input_xml_ns) AS account_correlation_seq,
                     ExtractValue(Value(account), 'account/account_number/text()',ob_correlation_input_xml_ns)          AS account_number
                 FROM
                 TABLE ( XMLSequence ( Extract(accounts, '/accounts/account',ob_correlation_input_xml_ns) ) ) account
                )
        LOOP
            IF account_row.account_correlation_seq IS NOT NULL THEN
                temp_count := 0;
                SELECT
                    COUNT (*)
                INTO temp_count
                FROM onboarding_account
                WHERE onboarding_account_seq = account_row.account_correlation_seq AND
                    onboarding_application_id = application_id;
                IF temp_count = 1
                THEN
                    UPDATE onboarding_account
                    SET account_number = account_row.account_number,
                        last_modified_id = USER,
                        last_modified_date = CURRENT_TIMESTAMP
                    WHERE onboarding_account_seq = account_row.account_correlation_seq AND
                        onboarding_application_id = application_id;
                ELSE
                    INSERT INTO
                        onboarding_account
                            (onboarding_account_seq,
                            onboarding_application_id,
                            account_number,
                            last_modified_date,
                            last_modified_id)
                    VALUES
                        (account_row.account_correlation_seq,
                        application_id,
                        account_row.account_number,
                        CURRENT_TIMESTAMP,
                        USER);
                END IF;
            ELSE
                raise_application_error('-20103',
                    'ACCOUNT CORRELATION SEQUENCE IS EMPTY. APPLICATION ID: ' || application_id);
            END IF;
        END LOOP;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END set_account_correlation_ids;

    PROCEDURE set_party_correlation_ids (application_id in VARCHAR2, parties IN XMLTYPE)
    IS
        temp_count      INTEGER;
    BEGIN
        FOR party_row IN
                (SELECT
                     ExtractValue(Value(party), 'party/party_correlation_seq/text()',ob_correlation_input_xml_ns) AS party_correlation_seq,
                     ExtractValue(Value(party), 'party/gcm_pan/text()',ob_correlation_input_xml_ns)               AS gcm_pan,
                     Extract(Value(party), 'party/addresses',ob_correlation_input_xml_ns)                         AS addresses
                 FROM
                 TABLE ( XMLSequence ( Extract(parties, '/parties/party',ob_correlation_input_xml_ns) ) ) party
                )
        LOOP
            IF party_row.party_correlation_seq IS NOT NULL THEN
                temp_count := 0;
                IF party_row.gcm_pan IS NOT NULL THEN
                    SELECT COUNT (*)
                    INTO temp_count
                    FROM onboarding_party
                    WHERE onboarding_party_seq = party_row.party_correlation_seq AND
                        onboarding_application_id = application_id;
                    IF temp_count = 1 THEN
                        UPDATE onboarding_party
                        SET gcm_pan = party_row.gcm_pan,
                            last_modified_id = USER,
                            last_modified_date = CURRENT_TIMESTAMP
                        WHERE onboarding_party_seq = party_row.party_correlation_seq AND
                            onboarding_application_id = application_id;
                    ELSE
                        INSERT INTO onboarding_party
                        (
                            onboarding_party_seq,
                            onboarding_application_id,
                            gcm_pan,
                            last_modified_id,
                            last_modified_date
                        ) VALUES (
                            party_row.party_correlation_seq,
                            application_id,
                            party_row.gcm_pan,
                            USER,
                            CURRENT_TIMESTAMP
                        );
                    END IF;
                ELSE
                    raise_application_error('-20105', 'GCM PAN IS NULL. APPLICATION ID: ' || application_id);
                END IF;
            ELSE
                raise_application_error('-20106', 'PARTY CORRELATION SEQUENCE IS EMPTY. APPLICATION ID: ' || application_id);
            END IF;
        END LOOP;
    END set_party_correlation_ids;

END onboarding_id_updates;
/