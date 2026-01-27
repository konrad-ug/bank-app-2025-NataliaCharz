INSERT INTO PERSONAL_ACCOUNT(identification, name, surname, balance)
VALUES ('12345678901', 'Jan', 'Kowalski',  1000);

INSERT INTO HISTORY_TRANSACTION(type, amount, date, account_id)
VALUES ('INCOMING', 1000, CURRENT_DATE, '12345678901');
