from flask import Blueprint,request,jsonify,json
import pymysql
import yaml
import traceback
from datetime import datetime


with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])


comments=Blueprint("comments",__name__)

@comments.route('/')
def index():
    return  str(int(datetime.now().timestamp()))+str(int(False))

@comments.route('/createComment',methods=['POST'])
def createComment():
    info = dict()
    errors = []
    accessKey = request.values.get('accessKey')
    userID = request.values.get('userID')
    message = request.values.get('message')
    print(message)
    timestamp = str(int(datetime.now().timestamp()))


    sender = ""
    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        sender = row[1]


    if len(message)>100:
        errors.append("message too long")


    cursor.execute("SELECT * from Users WHERE userID = %s",userID)
    rows = cursor.fetchall()
    connection.commit()
    if len(rows) == 0:
        errors.append("userID doesn't exist!")

    if sender == userID:
        errors.append("don't send yourself")


    info['errors'] = errors

    if len(info['errors'])==0:
        try:
            insertString = 'INSERT INTO Comments(sender,receiver,message,timestamp)values(%s,%s,%s,%s)'
            cursor.execute(insertString, (sender,userID,message,timestamp))
            connection.commit()
        except Exception:
            traceback.print_exc()
            connection.rollback()
            info['errors'] = 'createNotice fail'

    return jsonify(info)


@comments.route('/getComments',methods=['POST'])
def getComments():
    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')

    userID = ""

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        userID = row[1]

        cursor.execute("SELECT * from Comments WHERE receiver = %s ORDER BY timestamp DESC",userID)
        rows = cursor.fetchall()
        connection.commit()


        info['Notices'] = []
        for row in rows:
            info['Notices'].append(""+row[1]+"="+row[3]+"="+row[4])

    info['errors'] = errors

    return jsonify(info)