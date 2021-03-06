/* DROP DATABASE morbid; */
/* CREATE DATABASE morbid WITH TEMPLATE = template0 ENCODING = 'UTF8' LC_COLLATE = 'en_US.UTF-8' LC_CTYPE = 'en_US.UTF-8'; */

DROP TABLE IF EXISTS accounts CASCADE;
CREATE TABLE accounts (
    id       SERIAL                             ,
    created  TIMESTAMP    NOT NULL              ,
    deleted  TIMESTAMP                          ,
    active   BOOLEAN      NOT NULL DEFAULT true ,
    name     VARCHAR(64)  NOT NULL UNIQUE       ,
    type     VARCHAR(32)  NOT NULL              ,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    id       SERIAL                               ,
    account  BIGINT       REFERENCES account (id) ,
    created  TIMESTAMP    NOT NULL                ,
    deleted  TIMESTAMP                            ,
    active   BOOLEAN      NOT NULL DEFAULT true   ,
    username VARCHAR(64)  NOT NULL UNIQUE         ,
    email    VARCHAR(128) NOT NULL                ,
    type     VARCHAR(256) NOT NULL                ,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS secrets CASCADE;
CREATE TABLE secrets (
    id       SERIAL                               ,
    user_id  BIGINT       REFERENCES users (id)   ,
    created  TIMESTAMP    NOT NULL                ,
    deleted  TIMESTAMP                            ,
    method   VARCHAR(16)  NOT NULL                ,
    password VARCHAR(256) NOT NULL                ,
    token    VARCHAR(128) NOT NULL UNIQUE         ,
    PRIMARY KEY(id)
);

DROP TABLE IF EXISTS permissions CASCADE;
CREATE TABLE permissions (
    id         SERIAL                               ,
    user_id    BIGINT       REFERENCES users (id)   ,
    created    TIMESTAMP    NOT NULL                ,
    deleted    TIMESTAMP                            ,
    name       VARCHAR(128) NOT NULL                ,
    PRIMARY KEY(id)
);
