--themes
set @movemoney_acl_class=1;
set @addressbook_acl_class=2;
set @transactions_acl_class=3;
set @termdeposit_acl_class=4;
set @clientonboarding_acl_class=5;
set @managedportfolio_acl_class=6;

--id , class , id (PRIMARY KEY)
insert into acl_class values (@movemoney_acl_class,'MOVEMONEY'), (@addressbook_acl_class,'ADDRESSBOOK'),(@transactions_acl_class,'TRANSACTION'),(@termdeposit_acl_class,'TERMDEPOSIT'),(@clientonboarding_acl_class,'CLIENTONBOARDING'),(@managedportfolio_acl_class,'MANAGEDPORTFOLIO');

--functionalities  I think I should make "object_id_identity" as varchar and have values like "payment", "deposit" etc
set @payment_acl_obj=1;
set @addressbook_acl_obj=2;
set @transactions_acl_obj=3;
set @termdeposit_acl_obj=4;
set @clientonboarding_acl_obj=5;
set @managedportfolio_acl_obj=6;
-- id ,  object_id_class , object_id_identity , parent_object, owner_sid ,  entries_inheriting 
INSERT INTO acl_object_identity VALUES (@payment_acl_obj, @movemoney_acl_class, @payment_acl_obj, NULL, 1, 1);
INSERT INTO acl_object_identity VALUES (@addressbook_acl_obj, @addressbook_acl_class, @addressbook_acl_obj, NULL, 1, 1);
INSERT INTO acl_object_identity VALUES (@transactions_acl_obj, @transactions_acl_class, @transactions_acl_obj, NULL, 1, 1);
INSERT INTO acl_object_identity VALUES (@termdeposit_acl_obj, @termdeposit_acl_class, @termdeposit_acl_obj, NULL, 1, 1);
INSERT INTO acl_object_identity VALUES (@clientonboarding_acl_obj, @clientonboarding_acl_class, @clientonboarding_acl_obj, NULL, 1, 1);
INSERT INTO acl_object_identity VALUES (@managedportfolio_acl_obj, @managedportfolio_acl_class, @managedportfolio_acl_obj, NULL, 1, 1);

--authorities
SET @pay_inpay_all_sid=1;
SET @pay_inpay_linked_sid=2;
SET @bp_acc_maint_sid=3;
SET @no_txn_sid=4;
--Blockers
SET @deceased_sid=5;
SET @court_order_sid=6;
SET @payment_in=7;
SET @payment_out=8;
--Emulator
SET @emulator_sid=9;
--ParaPlanner and Assistant
SET @assist_sid=10;

--id,  principal,  sid
insert into acl_sid values (@pay_inpay_all_sid, 0, 'PAY_INPAY_ALL');
insert into acl_sid values (@pay_inpay_linked_sid, 0, 'PAY_INPAY_LINK');
insert into acl_sid values (@bp_acc_maint_sid, 0, 'BP_ACC_MAINT');
insert into acl_sid values (@no_txn_sid, 0, 'NO_TRX');

insert into acl_sid values (@deceased_sid, 0, 'DECEASED');
insert into acl_sid values (@court_order_sid, 0, 'COURT_ORDER');
insert into acl_sid values (@payment_in, 0, 'INPAY');
insert into acl_sid values (@payment_out, 0, 'PAY');

insert into acl_sid values (@emulator_sid, 0, 'EMULATOR');

insert into acl_sid values (@assist_sid, 0, 'ASSIST');


--Operations (permissions/masks)i think should start from 6
--MOVEMONEY Payment
SET @ADD_BILLER_OR_PAYEE=9;
SET @CHANGE_LIMIT=10;
SET @PAY_ONLY_TO_LINKED_ACCOUNTS=11;
SET @CAN_TRANSACT_PAYMENT=12;
--MOVEMONEY Depsoit
SET @CAN_TRANSACT_DEPOSIT=13;
--TRANSACTION
SET @STOP_SCHEDULED_TRANSACTION=14;

--Now turn for ACL - each entry in acl_sid table would have group of rows in acl_entry table for different/applicable masks

--id , acl_object_identity , ace_order , sid , mask , granting , audit_success tinyint,  audit_failure tinyint
-- permissions for @pay_inpay_all_sid user
-- ADD_BILLER_OR_PAYEE
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 1, @pay_inpay_all_sid, @ADD_BILLER_OR_PAYEE, 1, 0, 0);
--CHANGE_LIMIT
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 2, @pay_inpay_all_sid, @CHANGE_LIMIT, 1, 0, 0);
--PAY_ONLY_TO_LINKED_ACCOUNTS
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 3, @pay_inpay_linked_sid, @PAY_ONLY_TO_LINKED_ACCOUNTS, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 4, @pay_inpay_all_sid, @PAY_ONLY_TO_LINKED_ACCOUNTS, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 5, @no_txn_sid, @PAY_ONLY_TO_LINKED_ACCOUNTS, 0, 0, 0);
-- CAN_TRANSACT_PAYMENT
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 7, @pay_inpay_all_sid, @CAN_TRANSACT_PAYMENT, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 8, @pay_inpay_linked_sid, @CAN_TRANSACT_PAYMENT, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 9, @no_txn_sid, @CAN_TRANSACT_PAYMENT, 0, 0, 0);
-- CAN_TRANSACT_DEPOSIT
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 11, @pay_inpay_all_sid, @CAN_TRANSACT_DEPOSIT, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 12, @pay_inpay_linked_sid, @CAN_TRANSACT_DEPOSIT, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 13, @no_txn_sid, @CAN_TRANSACT_DEPOSIT, 0, 0, 0);
-- TRANSACTION
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@transactions_acl_obj, 1, @pay_inpay_all_sid, @STOP_SCHEDULED_TRANSACTION, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@transactions_acl_obj, 2, @pay_inpay_linked_sid, @STOP_SCHEDULED_TRANSACTION, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@transactions_acl_obj, 3, @no_txn_sid, @STOP_SCHEDULED_TRANSACTION, 0, 0, 0);
--TermDeposit
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 1, @pay_inpay_all_sid, 2, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 2, @pay_inpay_linked_sid, 2, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 9, @no_txn_sid, 2, 1, 0, 0);
--ManagedPortfolio
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@managedportfolio_acl_obj, 1, @pay_inpay_all_sid, 2, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@managedportfolio_acl_obj, 2, @pay_inpay_linked_sid, 2, 1, 0, 0);

--Restriction/Block 
--Payment
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 15, @deceased_sid, @CAN_TRANSACT_PAYMENT, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 16, @court_order_sid, @CAN_TRANSACT_PAYMENT, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 17, @payment_out, @CAN_TRANSACT_PAYMENT, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 18, @payment_in, @CAN_TRANSACT_PAYMENT, 1, 0, 0);
--Deposit
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 19, @deceased_sid, @CAN_TRANSACT_DEPOSIT, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 20, @court_order_sid, @CAN_TRANSACT_DEPOSIT, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 21, @payment_out, @CAN_TRANSACT_DEPOSIT, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 22, @payment_in, @CAN_TRANSACT_DEPOSIT, 0, 0, 0);

--Add and withdraw (WRITE) Term Deposit
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 3, @deceased_sid, 2, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 4, @court_order_sid, 2, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 5, @payment_out, 2, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 6, @payment_in, 2, 1, 0, 0);

--ManagedPortfolio
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@managedportfolio_acl_obj, 3, @deceased_sid, 2, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@managedportfolio_acl_obj, 4, @court_order_sid, 2, 0, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@managedportfolio_acl_obj, 5, @payment_out, 2, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@managedportfolio_acl_obj, 6, @payment_in, 2, 1, 0, 0);

--Addressbook Edit permission
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@addressbook_acl_obj, 1, @bp_acc_maint_sid, 1, 1, 0, 0);
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@addressbook_acl_obj, 2, @bp_acc_maint_sid, 2, 1, 0, 0);

--Emulator 
-- MoveMoney (payment & Deposit)
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@payment_acl_obj, 24, @emulator_sid, 2, 0, 0, 0);
-- Address Book 
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@addressbook_acl_obj, 4, @emulator_sid, 2, 0, 0, 0);
-- Term Deposit
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 7, @emulator_sid, 2, 0, 0, 0);
--ManagedPortfolio
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@managedportfolio_acl_obj, 7, @emulator_sid, 2, 0, 0, 0);

--ASSISTANT 
-- Address Book 
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@clientonboarding_acl_obj, 1, @assist_sid, 2, 0, 0, 0);
-- Term Deposit
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@termdeposit_acl_obj, 8, @assist_sid, 2, 0, 0, 0);
--ManagedPortfolio
insert into acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) values (@managedportfolio_acl_obj, 8, @assist_sid, 2, 0, 0, 0);
