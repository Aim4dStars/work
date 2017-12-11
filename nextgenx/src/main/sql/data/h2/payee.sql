insert into payee_type values('BPAY', 'BPay Payee');
insert into payee_type values('PAY_ANYONE', 'Pay Anyone Payee');
insert into payee_type values('PRIMARY_LINKED', 'Primary Linked Account');
insert into payee_type values('SECONDARY_LINKED', 'Secondary Linked Account');

insert into payee values(payee_seq.nextval, 234568876, 'BPAY', 'Payee 1','PrimaryCategory','SecoundryCategory');
insert into payee values(payee_seq.nextval, 234568876, 'PAY_ANYONE', 'Payee 2','PrimaryCategory','SecoundryCategory');
insert into pay_anyone_payee select max(payee_seq), 'Pay Anyone Name', '012003', '111222333' from payee;
insert into payee values(payee_seq.nextval, 234568876, 'PRIMARY_LINKED', 'Payee 3','PrimaryCategory','SecoundryCategory');
insert into LINKED_ACCOUNT select max(payee_seq), 'Linked Account Name', '012003', '111222333' from payee;

insert into payee values(payee_seq.nextval, 10000702, 'BPAY', 'Payee 1','PrimaryCategory','SecoundryCategory');
insert into payee values(payee_seq.nextval, 10000702, 'PAY_ANYONE', 'Payee 2','PrimaryCategory','SecoundryCategory');
insert into pay_anyone_payee select max(payee_seq), 'Pay Anyone Name', '012003', '111222333' from payee;
insert into payee values(payee_seq.nextval, 10000702, 'PRIMARY_LINKED', 'Payee 3','PrimaryCategory','SecoundryCategory');
insert into LINKED_ACCOUNT select max(payee_seq), 'Linked Account Name', '012003', '111222333' from payee;
