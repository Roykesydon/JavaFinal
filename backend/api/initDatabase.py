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
    userID varchar(200) NOT NULL,\
    name varchar(200) NOT NULL,\
    email varchar(200) NOT NULL,\
    password varchar(200) NOT NULL, \
    accessKey varchar(200),\
    lastAccessTime varchar(200),\
    bad int,good int,\
    PRIMARY KEY (_ID) );")
connection.commit()

