from flask import Blueprint,request,jsonify
import pymysql
import yaml
import traceback
from datetime import datetime


with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])


notifications=Blueprint("notifications",__name__)

@notifications.route('/')
def index():
    return  str(int(datetime.now().timestamp()))+str(int(False))

@notifications.route('/createNotice',methods=['POST'])
def createNotice():
    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')
    message = request.values.get('message')
    isSent = False
    timestamp = str(int(datetime.now().timestamp()))
    owner = ""

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        owner = row[1]

    if len(message)>100:
        errors.append("message too long")


    info['errors'] = errors

    if len(info['errors'])==0:
        try:
            insertString = 'INSERT INTO Notifications(owner,message,isSent,timestamp)values(%s,%s,%s,%s)'
            cursor.execute(insertString, (owner,message,str(int(isSent)),timestamp))
            connection.commit()
        except Exception:
            traceback.print_exc()
            connection.rollback()
            info['errors'] = 'createNotice fail'

    return jsonify(info)
