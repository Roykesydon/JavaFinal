# blog.py
from os import error
from flask import Blueprint,request,jsonify
import pymysql
import yaml
import traceback
import requests

with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

posts=Blueprint("posts",__name__)

@posts.route('/')
def index():
    r = requests.get('https://www.google.com.tw/')
    print(r.status_code)
    return str(r.status_code)

@posts.route('/createPost',methods=['POST'])
def createPost():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
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
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')
    postID = request.values.get('postID')
    userID = -1
    creator=""
    postCategory = ""
    price = ""
    joinedPeople=-1

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
        postCategory = row[2]
        price = row[3]
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

    #send notice
    if joinedPeople == 9:
        cursor.execute("SELECT * from Users WHERE userID = %s",creator)
        rows = cursor.fetchall()
        connection.commit()
        row = rows[0]
        creatorAccesskey = row[5]
        para = {'accessKey':creatorAccesskey,'message':'Your post queue is full\n'+postCategory+" "+price+" NT"}
        r = requests.post('http://' + cfg['db']['host']  + ':13261/notifications/createNotice', data = para)

    return jsonify(info)

@posts.route('/getProfileAndOwnPost',methods=['POST'])
def getOwnPost():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    errors = []

    userID = request.values.get('userID')

    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE userID = %s",userID)
    rows = cursor.fetchall()
    connection.commit()

    joinPostsSpace = -1

    if len(rows) == 0:
        errors.append("userID doesn't exist!")
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
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
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
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
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
            postDetail = ""+creator+","+category+","+str(price)+","+postID+","+str(joinPeopleCount)
            for i in range(3):
                cursor.execute("SELECT * from Users WHERE joinPost"+str(i+1)+" = %s",postID)
                rows = cursor.fetchall()
                connection.commit()
                for row in rows:
                    postDetail += ','+row[1]
            info['ownPost'].append(postDetail)

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
                    postDetail = ""+creator+","+category+","+str(price)+","+postID+","+str(joinPeopleCount)
                    for i in range(3):
                        cursor.execute("SELECT * from Users WHERE joinPost"+str(i+1)+" = %s",postID)
                        rows = cursor.fetchall()
                        connection.commit()
                        for row in rows:
                            postDetail += ','+row[1]
                    info['ownPost'].append(postDetail)

    info['errors'] = errors

    return jsonify(info)

@posts.route('/removeUser',methods=['POST'])
def removeUser():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
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
                errors.append("cannot remove post's creator")
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

@posts.route('/deletePost',methods=['POST'])
def deletePost():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')
    postID = request.values.get('postID')


    postCategory = ""
    price = ""
    ownerID=""

    cursor = connection.cursor()

    cursor.execute("SELECT * from Posts WHERE postID = %s",postID)
    rows = cursor.fetchall()
    connection.commit()
    if len(rows)==0:
        errors.append("post doesn't exist")
    else:
        row = rows[0]
        postCategory = row[2]
        price = row[3]

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
        ownerID = row[1]
        createPosts = [row[11],row[12],row[13]]
        isAdmin = row[7]

        # check auth
        if not isAdmin:
            for index,createPost in enumerate(createPosts):
                if createPost == postID:
                    postflag = True
                    isCreator = True
                    break

            if postflag == False:
                errors.append("user don't have auth")

    if len(errors) == 0:
        for index in range(3):
            cursor.execute("SELECT * from Users WHERE joinPost"+str(index+1)+" = %s",postID)
            rows = cursor.fetchall()
            connection.commit()
            for row in rows:
                userID = row[1]
                userAccessKey = row[5]
                para = {'accessKey':userAccessKey,'message':'Post been deleted\n'+postCategory+" NT$ "+price+"\nowner ID:"+ownerID}
                r = requests.post('http://' + cfg['db']['host']  + ':13261/notifications/createNotice', data = para)

    if len(errors) == 0:
        for index in range(3):
            cursor.execute("UPDATE Users SET joinPost"+str(index+1)+" = %s WHERE joinPost"+str(index+1)+" = %s",(None,postID))
            connection.commit()
        for index in range(3):
            cursor.execute("UPDATE Users SET createPost"+str(index+1)+" = %s WHERE createPost"+str(index+1)+" = %s",(None,postID))
            connection.commit()

    if len(errors) == 0:
        cursor.execute("DELETE FROM Posts WHERE postID = %s",postID)
        connection.commit()

    info['errors'] = errors

    return jsonify(info)

@posts.route('/completePost',methods=['POST'])
def completePost():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    errors = []

    accessKey = request.values.get('accessKey')
    postID = request.values.get('postID')
    chooseList = request.values.get('chooseList')
    chooseList = chooseList.split(',')

    creatorEmail = ""
    postCategory = ""
    price = ""
    ownerID = ""

    cursor = connection.cursor()

    cursor.execute("SELECT * from Posts WHERE postID = %s",postID)
    rows = cursor.fetchall()
    connection.commit()
    if len(rows)==0:
        errors.append("post doesn't exist")
    else:
        row = rows[0]
        postCategory = row[2]
        price = row[3]

    cursor.execute("SELECT * from Users WHERE accessKey = %s",accessKey)
    rows = cursor.fetchall()
    connection.commit()

    if len(rows) == 0:
        errors.append("accessKey doesn't exist!")
    else:
        row = rows[0]
        ownerID = row[1]
        createPost = [row[11],row[12],row[13]]
        creatorEmail = row[3]
        if postID not in createPost:
            errors.append("don't own the post")

    if len(errors)==0:
        for chooseUser in chooseList:
            cursor.execute("SELECT * from Users WHERE userID = %s",chooseUser)
            rows = cursor.fetchall()
            connection.commit()
            if len(rows)==0:
                errors.append("can't find choose user")
                break
            else:
                row = rows[0]
                joinPost = [row[8],row[9],row[10]]
                if postID not in joinPost:
                    errors.append("choose list has person not in post")
                    break

    # sendNotice,deletePost

    if len(errors) == 0:
        for index in range(3):
            cursor.execute("SELECT * from Users WHERE joinPost"+str(index+1)+" = %s",postID)
            rows = cursor.fetchall()
            connection.commit()
            for row in rows:
                userID = row[1]
                userAccessKey = row[5]
                if userID in chooseList:
                    para = {'accessKey':userAccessKey,'message':'Match successfully\n'+postCategory+" NT$ "+price+"\nowner email:"+creatorEmail}
                    r = requests.post('http://' + cfg['db']['host']  + ':13261/notifications/createNotice', data = para)
                else:
                    para = {'accessKey':userAccessKey,'message':'Match failed\n'+postCategory+" NT$ "+price+"\nowner ID:"+ownerID}
                    r = requests.post('http://' + cfg['db']['host']  + ':13261/notifications/createNotice', data = para)
        para = {'accessKey':accessKey,'message':'Match successfully\nAlready send your email to people you chose\n'+postCategory+" "+price +" NT"}
        r = requests.post('http://' + cfg['db']['host']  + ':13261/notifications/createNotice', data = para)

        for index in range(3):
            cursor.execute("UPDATE Users SET joinPost"+str(index+1)+" = %s WHERE joinPost"+str(index+1)+" = %s",(None,postID))
            connection.commit()
        for index in range(3):
            cursor.execute("UPDATE Users SET createPost"+str(index+1)+" = %s WHERE createPost"+str(index+1)+" = %s",(None,postID))
            connection.commit()
        cursor.execute("DELETE FROM Posts WHERE postID = %s",postID)
        connection.commit()

    info['errors'] = errors

    return jsonify(info)

@posts.route('/getFilteredPost',methods=['POST'])
def getFilteredPost():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    errors = []
    info['posts'] = []

    category = request.values.get('category')

    cursor = connection.cursor()

    try:
        cursor.execute("SELECT * from Posts WHERE category = %s",category)
        rows = cursor.fetchall()
        connection.commit()
    except Exception:
        traceback.print_exc()
        connection.rollback()
        errors.append('getFilteredPost fail')
    

    for row in rows:
        creator = row[1]
        category = row[2]
        price = row[3]
        postID = row[4]
        joinPeopleCount = row[5]
        info['posts'].append(creator+","+category+","+str(price)+","+postID+","+str(joinPeopleCount))

    info['errors'] = errors

    return jsonify(info)