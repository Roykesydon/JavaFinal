
DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Posts;
DROP TABLE IF EXISTS Notifications;
DROP TABLE IF EXISTS Comments;

CREATE TABLE IF NOT EXISTS Users( 
    _ID int  NOT NULL AUTO_INCREMENT,
    userID varchar(40) NOT NULL,
    name varchar(60) NOT NULL,
    email varchar(50) NOT NULL,
    password varchar(40) NOT NULL, 
    accessKey varchar(100),
    lastAccessTime varchar(100),
    isAdmin bool NOT NULL,
    joinPost1 varchar(60),
    joinPost2 varchar(60),
    joinPost3 varchar(60),
    createPost1 varchar(60),
    createPost2 varchar(60),
    createPost3 varchar(60),
    identityCode varchar(6),
    lastReadComment int,
    lastSendIdentifyCodeTime varchar(100),
    tryIdentifyCodeCount int,
    PRIMARY KEY (_ID) );


CREATE TABLE IF NOT EXISTS Posts( 
    _ID int  NOT NULL AUTO_INCREMENT,
    creator varchar(40) NOT NULL,
    category varchar(60) NOT NULL,
    price varchar(50) NOT NULL,
    postID varchar(60) NOT NULL,
    joinPeopleCount int NOT NULL,
    PRIMARY KEY (_ID) );

CREATE TABLE IF NOT EXISTS Notifications( 
    _ID int  NOT NULL AUTO_INCREMENT,
    owner varchar(40) NOT NULL,
    message varchar(250) NOT NULL,
    isSent bool NOT NULL,
    timestamp varchar(15)  NOT NULL,
    PRIMARY KEY (_ID) );


CREATE TABLE IF NOT EXISTS Comments( 
    _ID int  NOT NULL AUTO_INCREMENT,
    sender varchar(40) NOT NULL,
    receiver varchar(40) NOT NULL,
    message varchar(250) NOT NULL,
    timestamp varchar(15)  NOT NULL,
    PRIMARY KEY (_ID) );