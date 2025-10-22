INSERT INTO coaches (first_name, last_name, phone, activity_type)
SELECT 'Василий', 'Пупкин', '+79123753434', 'BOXING'
    WHERE NOT EXISTS (SELECT 1 FROM coaches WHERE phone = '+79123753434');

INSERT INTO coaches (first_name, last_name, phone, activity_type)
SELECT 'Василиса', 'Чудесная', '+79123845767', 'DANCING'
    WHERE NOT EXISTS (SELECT 1 FROM coaches WHERE phone = '+79123845767');

INSERT INTO coaches (first_name, last_name, phone, activity_type)
SELECT 'Всеволод', 'Кузнецов', '+79126759128', 'GYM'
    WHERE NOT EXISTS (SELECT 1 FROM coaches WHERE phone = '+79126759128');

INSERT INTO coaches (first_name, last_name, phone, activity_type)
SELECT 'Василий', 'Пупкин', '+79124922828', 'SWIMMING_POOL'
    WHERE NOT EXISTS (SELECT 1 FROM coaches WHERE phone = '+79124922828');
