drop table if exists bpay_biller;

create table bpay_biller(biller_code varchar(10) primary key, biller_name varchar(50), crn_check_digit_routine varchar(50), crn_fixed_digits_mask varchar(50), crn_valid_lengths varchar(50));
