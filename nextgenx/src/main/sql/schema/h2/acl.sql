DROP TABLE IF EXISTS acl_sid;
CREATE TABLE acl_sid (  id bigint(20) NOT NULL AUTO_INCREMENT,  principal tinyint(1) NOT NULL,  sid varchar(100) NOT NULL,  PRIMARY KEY (id), UNIQUE KEY uk_acl_sid (sid,principal)) ;

DROP TABLE IF EXISTS acl_class;
CREATE TABLE acl_class (id bigint(20) NOT NULL AUTO_INCREMENT, class varchar(255) NOT NULL, PRIMARY KEY (id), UNIQUE KEY uk_acl_class (class)) ;

DROP TABLE IF EXISTS acl_entry;
CREATE TABLE acl_entry (id bigint(20) NOT NULL AUTO_INCREMENT, acl_object_identity bigint(20) NOT NULL, ace_order int(11) NOT NULL, sid bigint(20) NOT NULL, mask int(11) NOT NULL, granting tinyint(1) NOT NULL, audit_success tinyint(1) NOT NULL,  audit_failure tinyint(1) NOT NULL, PRIMARY KEY (id), UNIQUE KEY uk_acl_entry (acl_object_identity,ace_order)) ;

DROP TABLE IF EXISTS acl_object_identity;
CREATE TABLE acl_object_identity ( id bigint(20) NOT NULL AUTO_INCREMENT,  object_id_class bigint(20) NOT NULL, object_id_identity bigint(20) NOT NULL, parent_object bigint(20) DEFAULT NULL, owner_sid bigint(20) DEFAULT NULL,  entries_inheriting tinyint(1) NOT NULL,   PRIMARY KEY (id),  UNIQUE KEY uk_acl_objid (object_id_class,object_id_identity));