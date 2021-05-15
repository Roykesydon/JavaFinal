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

#施工中
@notifications.route('/getNewestTenNotice',methods=['POST'])
def getNewestTenNotice():
    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()

    joinPostsSpace = -1

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        owner = row[1]

        cursor.execute("SELECT * from Posts WHERE creator = %s",info['userID'])
        rows = cursor.fetchall()
        connection.commit()

        info['ownPost'] = []
        for row in rows:
            creator = row[1]
            category = row[2]
            price = row[3]
            postID = row[4]
            joinPeopleCount = row[5]
            info['ownPost'].append(creator+","+category+","+str(price)+","+postID+","+str(joinPeopleCount))

    info['errors'] = errors

    return jsonify(info)


#施工中
#一次polling只會回傳最多一則還未讀的notification
@notifications.route('/checkNotification',methods=['POST'])
def checkNotification():
    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()
    message=""
    findNoticeFlag = False

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        cursor.execute("SELECT * from Notifications WHERE owner = %s",row[1])
        rows = cursor.fetchall()
        connection.commit()

        for row in rows:
            isSent = row[3]
            noticeID = row[0]
            if int(isSent)==0:
                findNoticeFlag = True
                message = row[2]
                cursor.execute("UPDATE Notifications SET isSent = %s WHERE _ID = %s",("1",noticeID))
                connection.commit()
                break

    if(findNoticeFlag):
        info["message"] = message
    else:
        errors.append("No unread message")

    info['errors'] = errors

    return jsonify(info)
