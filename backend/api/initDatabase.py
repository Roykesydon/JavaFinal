# admin.py
import pymysql
import yaml


with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'],charset='utf8')

cursor = connection.cursor()
cursor.execute("DROP TABLE IF EXISTS Users;")
connection.commit()

cursor.execute("CREATE TABLE IF NOT EXISTS Users( \
    _ID int  NOT NULL AUTO_INCREMENT,\
    userID varchar(40) NOT NULL,\
    name varchar(60) NOT NULL,\
    email varchar(50) NOT NULL,\
    password varchar(40) NOT NULL, \
    accessKey varchar(100),\
    lastAccessTime varchar(100),\
    bad int,good int,\
    PRIMARY KEY (_ID) );")
connection.commit()

