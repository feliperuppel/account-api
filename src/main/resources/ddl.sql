create table public.account
(
account_id IDENTITY,
balance DECIMAL default 0,
creation_date TIMESTAMP not null,
primary key(id)
);

--create table public.transfer_history
--(
--transfer_history_id IDENTITY,
--account_id_from BIGINT NOT NULL,
--account_id_to BIGINT NOT NULL,
--transfer_amount DECIMAL DEFAULT 0,
--audit_date TIMESTAMP NOT NULL,
--primary key(transfer_history_id),
--foreign key(account_id_from) references account(account_id),
--foreign key(account_id_to) references account(account_id)
--);
--
--create table public.single_operation_history
--(
--history_id IDENTITY,
--account_id BIGINT NOT NULL,
--amount DECIMAL DEFAULT 0,
--
--);