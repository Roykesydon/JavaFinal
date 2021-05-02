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

    print(accessKey)

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()

    postsSpace = -1

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        creator = row[1]
        createdPosts = [ row[11],row[12],row[13]]

        print(createdPosts)
        for index,createdPost in enumerate(createdPosts):
            if createdPost == None:
                postsSpace = index
                break
        else:
            errors.append("can't create more Post")


    info['errors'] = errors

    if len(info['errors'])==0:
        try:
            insertString = 'INSERT INTO Posts(creator,category,price,postID)values(%s,%s,%s,%s)'
            cursor.execute(insertString, (creator,category,price, (creator+str(postsSpace)) ))
            connection.commit()
            cursor.execute("UPDATE Users SET createPost"+ str(postsSpace+1)+ " = %sWHERE accessKey = %s",((creator+str(postsSpace+1)),accessKey))
            connection.commit()
        except Exception:
            traceback.print_exc()
            connection.rollback()
            info['errors'] = 'createPost fail'

    return jsonify(info)
