-- Lozinke su hesovane pomocu BCrypt algoritma https://www.dailycred.com/article/bcrypt-calculator
-- Lozinka za oba user-a je 123

INSERT INTO USERS (email, password, first_name, last_name, enabled) VALUES ('user@example.com', '$2a$10$mvlQGvru4mF9GniFJTSjUew1WAPzZrWBuDYfcvFxZxRfVA4/0ZS5K', 'Marko', 'Markovic', true);
INSERT INTO USERS (email, password, first_name, last_name, enabled) VALUES ('majatepavcevic133@gmail.com', '$2a$10$mBurwG6npT1ETPmxLqKK1eCSEoMR.v57WH6ionkh0.hLVzjNLFxM2', 'Dragana', 'Todorovic', true);
INSERT INTO USERS (email, password, first_name, last_name, enabled) VALUES ('knezevicljiljana12@gmail.com', '$2a$10$mBurwG6npT1ETPmxLqKK1eCSEoMR.v57WH6ionkh0.hLVzjNLFxM2', 'Dragana', 'Todorovic', true);

INSERT INTO AUTHORITY (name) VALUES ('ROLE_USER');
INSERT INTO AUTHORITY (name) VALUES ('ROLE_ADMIN');

INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (1, 1);
INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (2, 2);
INSERT INTO USER_AUTHORITY (user_id, authority_id) VALUES (3, 2);
