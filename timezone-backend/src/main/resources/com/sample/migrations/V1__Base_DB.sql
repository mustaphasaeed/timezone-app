CREATE TABLE IF NOT EXISTS USERS(
  ID BIGINT(20) NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(250) NOT NULL,
  USERNAME VARCHAR(100) NOT NULL,
  PASSWORD VARCHAR(100) NOT NULL,
  USER_ROLE VARCHAR(10) NOT NULL,
  ACTIVE BIT(1) NULL,
  CREATION_DATE DATETIME NULL,
  LAST_LOGIN_DATE DATETIME NULL,
  PRIMARY KEY (ID) ,
  UNIQUE INDEX USERNAME_UNIQUE (USERNAME ASC) 
);

CREATE TABLE IF NOT EXISTS TIMEZONES(
  ID BIGINT(20) NOT NULL AUTO_INCREMENT,
  NAME VARCHAR(50) NOT NULL,
  CITY VARCHAR(50) NOT NULL,
  OFF_SET INT NOT NULL,
  USER_ID BIGINT(20) NOT NULL,
  PRIMARY KEY (ID) ,
  CONSTRAINT USER_FK FOREIGN KEY (USER_ID)REFERENCES users (ID)
);

INSERT INTO users (NAME, USERNAME, PASSWORD, USER_ROLE, ACTIVE, CREATION_DATE, LAST_LOGIN_DATE) 
VALUES ('Admin', 'admin@timezone.com', 'W6ph5Mm5Pz8GgiULbPgzG37mj9g=', 'ADMIN', 1, now(), now())