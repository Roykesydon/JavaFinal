# blog.py
from flask import Blueprint,request,jsonify
import pymysql
import yaml
import traceback

with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])


posts=Blueprint("posts",__name__)

@posts.route('/')
def index():
    return"Posts route"

@posts.route('/createPost',methods=['POST'])
def createPost():

    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')
    category = request.values.get('category')
    price = request.values.get('price')

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()

    postsSpace = -1

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        creator = row[1]
        createdPosts = [ row[11],row[12],row[13]]

        for index,createdPost in enumerate(createdPosts):
            if createdPost == None:
                postsSpace = index
                break
        else:
            errors.append("can't create more Post")


    info['errors'] = errors

    if len(info['errors'])==0:
        try:
            insertString = 'INSERT INTO Posts(creator,category,price,postID,joinPeopleCount)values(%s,%s,%s,%s,%s)'
            cursor.execute(insertString, (creator,category,price, (creator+str(postsSpace+1)), 0))
            connection.commit()
            cursor.execute("UPDATE Users SET createPost"+ str(postsSpace+1)+ " = %s WHERE accessKey = %s",((creator+str(postsSpace+1)),accessKey))
            connection.commit()
        except Exception:
            traceback.print_exc()
            connection.rollback()
            info['errors'] = 'createPost fail'

    return jsonify(info)

@posts.route('/getProfileAndOwnPost',methods=['POST'])
def getOwnPost():

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
        info['userID'] = userID = row[1]
        info['name'] = name = row[2]
        info['email'] = email = row[3]
        info['lastAccessTime'] = lastAccessTime = row[6]

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

@posts.route('/getAllPost',methods=['POST'])
def getAllPost():

    info = dict()
    errors = []
    info['posts'] = []

    cursor = connection.cursor()
    
    try:
        cursor.execute("SELECT * from Posts")
        rows = cursor.fetchall()
        connection.commit()
    except Exception:
        traceback.print_exc()
        connection.rollback()
        errors.append('getAllPost fail')
    

    for row in rows:
        creator = row[1]
        category = row[2]
        price = row[3]
        postID = row[4]
        joinPeopleCount = row[5]
        info['posts'].append(creator+","+category+","+str(price)+","+postID+","+str(joinPeopleCount))

    info['errors'] = errors

    return jsonify(info)