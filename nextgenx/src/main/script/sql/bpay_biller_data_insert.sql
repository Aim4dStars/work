create table &3._&5._&2. as select * from &1._&2.;
rem delete inactive billers
delete from &3._&5._&2. where deactive_date < sysdate;
commit;
rem delete any payment methods other than Debit Account
delete from &3._&5._&2. where payment_method != '001';
commit;
-- Delete duplicate records that have the same primary key
delete from &3._&5._&2. a where a.rowid > ANY (select b.rowid from &3._&5._&2. b where a.&4. = b.&4.);
commit;
alter table &3._&5._&2. add constraint pk_&4._&2. primary key (&4.) exceptions into exceptions;