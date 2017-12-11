DROP TABLE IF EXISTS communication;
DROP TABLE IF EXISTS address;
DROP TABLE IF EXISTS party;
DROP TABLE IF EXISTS account;
DROP TABLE IF EXISTS application;

DROP SEQUENCE IF EXISTS ui_application_id_seq;
DROP SEQUENCE IF EXISTS ui_party_id_seq;
DROP SEQUENCE IF EXISTS ui_account_id_seq;
DROP SEQUENCE IF EXISTS ui_address_id_seq;

CREATE TABLE application (ui_application_id NUMBER(19) PRIMARY KEY, status VARCHAR(30), version NUMBER(3));
CREATE TABLE account (ui_account_id NUMBER(19) NOT NULL, ui_application_id NUMBER(19) NOT NULL, business_partner_id VARCHAR(20)  NOT NULL, status VARCHAR(100), version NUMBER(3), PRIMARY KEY (ui_account_id), FOREIGN KEY (ui_application_id) REFERENCES application (ui_application_id));
CREATE TABLE party (ui_party_id NUMBER(19), ui_application_id NUMBER(19) NOT NULL, ui_account_id NUMBER(19) NOT NULL, gcm_pan VARCHAR(20)  NOT NULL, status VARCHAR2(100), version NUMBER(3), PRIMARY KEY (ui_party_id), FOREIGN KEY (ui_application_id) REFERENCES application (ui_application_id), FOREIGN KEY (ui_account_id) REFERENCES account (ui_account_id));
CREATE TABLE address (ui_address_id NUMBER(19), ui_party_id NUMBER(19) NOT NULL, address_id VARCHAR(20)  NOT NULL, version NUMBER(3), PRIMARY KEY (ui_address_id), FOREIGN KEY (ui_party_id) REFERENCES party (ui_party_id));
CREATE TABLE communication (ui_party_id NUMBER(19) NOT NULL, communication_id VARCHAR(20));

CREATE SEQUENCE ui_application_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ui_party_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ui_account_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE ui_address_id_seq START WITH 1 INCREMENT BY 1;
