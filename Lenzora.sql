CREATE DATABASE lenzora;
USE lenzora;
SHOW DATABASES;

USE lenzora;

INSERT INTO users (username, password, role)
VALUES (
    'testuser1',
    '$2a$10$WQAV1NyIJNuJCb8AwrMuW.fviTpP9gPWdp7fqIdhBrJAiytEp2.Iq',
    'USER'
);


INSERT INTO users (username, password, role)
VALUES (
    'testuser2',
    '$2a$10$tAxwI9/CsEzyVJaINx9RlOyJ1TRqBkJQen7BJZQP2kNk3rUl4rPri',
    'USER'
);

INSERT INTO users (username, password, role)
VALUES (
    'testuser3',
    '$2a$10$gdCpvzJnlyxY0BV1NvHXJeVcqC6H7Ogo6Ch4N.BYpEcF2JODQk.m6',
    'USER'
);

INSERT INTO users (username, password, role)
VALUES (
    'testuser4',
    '$2a$10$JAZSc7XvANjLkazD2P3uzeTNm5g88VroovUzTeAJBO0j7M252KMZm',
    'USER'
);

SELECT * FROM users;




