SET FOREIGN_KEY_CHECKS=0;
TRUNCATE TABLE `USER_NOTICES`;
TRUNCATE TABLE `NOTICE_TYPE_REF`;
SET FOREIGN_KEY_CHECKS=1;

INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'TERMS_OF_USE' , 1, '2016-05-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'PDS' , 1, '2016-05-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'PDS_DIRECT' , 1, '2017-01-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'PDS_DIRECT_SUPER' , 1, '2017-08-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'PDS_ADVISED_SUPER' , 1, '2017-01-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'PDS_ADVISED_INVESTOR' , 1, '2017-01-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'WHATS_NEW_DIRECT' , 1, '2017-01-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'WHATS_NEW_DIRECT_SUPER' , 1, '2017-08-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'WHATS_NEW_ADVISED_SUPER' , 1, '2017-01-01');
INSERT INTO NOTICE_TYPE_REF (notice_id, version, last_updated_on) VALUES ( 'WHATS_NEW_ADVISED_INVESTOR' , 1, '2017-05-01');