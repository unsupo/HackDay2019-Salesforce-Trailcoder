------------------------------------------------------------------------------------------------------------------------
------------------------------- TABLES
------------------------------------------------------------------------------------------------------------------------

CREATE TABLE IF NOT EXISTS USERS (
    USER_ID         INTEGER IDENTITY PRIMARY KEY,
    FIRST_NAME      VARCHAR(50),
    LAST_NAME       VARCHAR(50),
    PASSWORD        VARCHAR(100),
    EMAIL_ADDRESS   VARCHAR(50) NOT NULL,
    USER_GROUP      VARCHAR(50) DEFAULT 'default' CHECK(USER_GROUP in ('admin'/*ADMIN*/, 'default' /*Normal User*/)),
    DATE_CREATED    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    DATE_LAST_LOGIN TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IS_VERIFIED     BOOLEAN DEFAULT FALSE,
    IMAGE_URL       VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS PROBLEMS (
    PROBLEM_ID      INTEGER IDENTITY PRIMARY KEY,
    PROBLEM_NAME    VARCHAR(100) UNIQUE,
    DIFFICULTY      VARCHAR(50),
    DIFFICULTY_NUM  INTEGER,
    HTML            VARCHAR(100000),
    DESCRIPTION     VARCHAR(100000),
    CODE_SAMPLE     VARCHAR(100000),
    IS_COMPLETE     BOOLEAN default FALSE,
    START_TIME      TIMESTAMP,
    END_TIME        TIMESTAMP
);
