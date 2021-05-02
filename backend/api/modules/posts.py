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

@posts.route('/joinPost',methods=['POST'])
def joinPost():

    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')
    postID = request.values.get('postID')
    userID = -1

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()

    joinPostsSpace = -1

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        userID = row[1]
        joinedPosts = [ row[8],row[9],row[10]]

        for index,joinedPost in enumerate(joinedPosts):
            print(joinedPosts)
            if joinedPost == postID:
                errors.append('already join post')
                break

        for index,joinedPost in enumerate(joinedPosts):
            if joinedPost == None:
                joinPostsSpace = index
                break
        else:
            errors.append("can't join more Post")


    cursor.execute("SELECT * from Posts WHERE postID = %s",postID)
    rows = cursor.fetchall()
    connection.commit()

    if len(rows) == 0:
        errors.append("postID don't exist")
    else:
        row = rows[0]

        creator = row[1]
        joinedPeople = row[5]

        if joinedPeople >=10:
            errors.append('post already full')
        if creator == userID:
            errors.append("can't join user's own post")

    info['errors'] = errors

    if len(info['errors'])==0:
        try:
            cursor.execute("UPDATE Users SET joinPost"+ str(joinPostsSpace+1)+ " = %sWHERE accessKey = %s",(postID,accessKey))
            connection.commit()
            cursor.execute("UPDATE Posts SET joinPeopleCount = %s WHERE PostID = %s",(str(joinedPeople+1),postID))
            connection.commit()
        except Exception:
            traceback.print_exc()
            connection.rollback()
            info['errors'] = 'joinPost fail'

    return jsonify(info)