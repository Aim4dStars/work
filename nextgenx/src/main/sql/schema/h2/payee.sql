drop table if exists payee_type;
create table payee_type(payee_type varchar(20) primary key, description varchar(50));

drop table if exists payee;
drop sequence if exists payee_seq;
create sequence if not exists PAYEE_SEQ start with 1 increment by 1;
create table payee(payee_seq number(19) primary key, cash_account_id char(9), payee_type varchar(20), nickname varchar(50), PAYMENT_CATEGORY varchar(100), PAYMENT_SUB_CATEGORY varchar(100));

drop table if exists bpay_payee;
create table bpay_payee(payee_seq number(19) primary key, biller_code varchar(10), customer_reference varchar(50));
alter table bpay_payee add foreign key(biller_code) references bpay_biller(biller_code);

drop table if exists pay_anyone_payee;
create table pay_anyone_payee(payee_seq number(19) primary key, name varchar(50), bsb_code char(6), account_number char(9));
alter table pay_anyone_payee add foreign key(bsb_code) references bsb(bsb_code);

drop table if exists LINKED_ACCOUNT;
create table LINKED_ACCOUNT(payee_seq number(19) primary key, ACCOUNT_NAME varchar(50), bsb_code char(6), ACCOUNT_NUMBER char(9));
alter table LINKED_ACCOUNT add foreign key(bsb_code) references bsb(bsb_code);
