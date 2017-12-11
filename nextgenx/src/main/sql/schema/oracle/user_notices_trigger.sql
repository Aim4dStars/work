CREATE or REPLACE TRIGGER user_notices_after_insert AFTER INSERT ON NOTICE_TYPE_REF
FOR EACH ROW

BEGIN
   -- Insert record into user_updates table only if new version is 1
   IF :new.version = 1  THEN
   FOR user IN (SELECT unique user_id  from users)
      LOOP
         INSERT INTO USER_NOTICES(user_id, notice_id, version) VALUES (user.user_id,:new.notice_id, :new.version);
      END LOOP;
   END IF;
END;
/