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

@posts.route('/getOwnAndJoinPost',methods=['POST'])
def getOwnAndJoinPost():

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
        info['userID'] = row[1]
        joinPost=[] 
        for i in range(3):
            joinPost.append(row[i+8])

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
            
        info['joinPost'] = []
        for postID in joinPost:
            if postID != None:
                cursor.execute("SELECT * from Posts WHERE postID = %s",postID)
                rows = cursor.fetchall()
                connection.commit()

                if len(rows)!=0:
                    row = rows[0]
                    creator = row[1]
                    category = row[2]
                    price = row[3]
                    postID = row[4]
                    joinPeopleCount = row[5]
                    info['joinPost'].append(creator+","+category+","+str(price)+","+postID+","+str(joinPeopleCount))

    info['errors'] = errors

    return jsonify(info)

@posts.route('/removeUser',methods=['POST'])
def removeUser():
    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')
    postID = request.values.get('postID')
    removeUserID = request.values.get('removeUserID')

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        postflag = False
        isCreator = False
        postIndex = -1
        
        row = rows[0]
        userID = row[1]
        joinedPosts = [ row[8],row[9],row[10]]
        createPosts = [row[11],row[12],row[13]]

        for index ,joinedPost in enumerate(joinedPosts):
            if joinedPost == postID:
                postflag = True
                break

        for index,createPost in enumerate(createPosts):
            if createPost == postID:
                postflag = True
                isCreator = True
                break
    
    errorChainFlag=False
    if postflag: 
        removeUserPostIndex = -1
        joinPeopleCount = -1
        
        #check if post exists
        cursor.execute("SELECT * from Posts WHERE postID = %s",postID)
        rows = cursor.fetchall()
        connection.commit()
        if len(rows) == 0:
            errorChainFlag = True
            errors.append("Post doesn't exist")
        else:
            joinPeopleCount = int(rows[0][5])
            
        #check if remove user joined the post
        cursor.execute("SELECT * from Users WHERE userID = %s",removeUserID)
        rows = cursor.fetchall()
        connection.commit()
        if len(rows) == 0:
            errorChainFlag = True
            errors.append("removeUser doesn't exist")
        if (not errorChainFlag):
            row=rows[0]
            removeUserJoinedPosts = [ row[8],row[9],row[10]]
            if postID not in removeUserJoinedPosts:
                errorChainFlag = True
                errors.append("removeUser doesn't join the post")
                
            for index ,joinedPost in enumerate(removeUserJoinedPosts):
                if joinedPost == postID:
                    removeUserPostIndex = index+1
                    break
                   
        #check if user has auth to remove target:
        if (not isCreator) and removeUserID != userID:
            errorChainFlag = True 
            errors.append("User doesn't have auth to remove removeUser")
                   
        #update post joined count and remove user data
        if not errorChainFlag:
            try:  
                print(removeUserPostIndex)
                cursor.execute("UPDATE Users SET joinPost"+str(removeUserPostIndex)+" = %s WHERE userID = %s",(None,removeUserID))
                connection.commit() 
                cursor.execute("UPDATE Posts SET joinPeopleCount = %s WHERE postID = %s",(str(joinPeopleCount-1),postID))
                connection.commit() 
            except Exception:
                traceback.print_exc()
                connection.rollback()
                errors.append('updatePostData fail') 
            
            
    else:
        errors.append("User not related to this post")


    info['errors'] = errors

    return jsonify(info) 