# admin.py
from flask import Blueprint,request,jsonify
import pymysql
import yaml
import re
import traceback
import hashlib
import random
import string
import datetime

with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

class CheckForm():
    global connection

    def __init__(self):
        self.__Errors=[]

    def letterNumberOnly(self,str):
        if re.match("^[A-Za-z0-9]*$", str):
            return True
        return False 

    def userid(self,str):
        if not self.letterNumberOnly(str):
            self.__Errors.append('ID has illegal characters')
        if len(str)>30 or len(str)<5:
            self.__Errors.append('ID length error')

    def name(self,str):
        if not self.letterNumberOnly(str):
            self.__Errors.append('name has illegal characters')
        if len(str)>50 or len(str)<5:
            self.__Errors.append('name length error')

    def passwd(self,str):
        if not self.letterNumberOnly(str):
            self.__Errors.append('passwd has illegal characters')
        if len(str)>30 or len(str)<5:
            self.__Errors.append('password length error')

    def passwdConfirm(self,ori,cof):
        if not self.letterNumberOnly(cof):
            self.__Errors.append('passwdConfirm has illegal characters')
        if ori!=cof:
            self.__Errors.append('password diffrent from passwordConfirm')

    def email(self,str):
        # if not re.search(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)",str):
        if not re.search(r"^[A-Za-z0-9]+@+[A-Za-z0-9]+[.-]+[A-Za-z0-9.]+[A-Za-z0-9]$",str):
            self.__Errors.append('email format error')

    def getErrors(self):
        return self.__Errors


class CheckRepeat():

    def __init__(self):
        self.__Errors=[]
        self.__cursor = None
    def userid(self,str):
        connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
        self.__cursor = connection.cursor()
        self.__cursor.execute("SELECT * from Users WHERE userID = %(userID)s",{'userID':str})
        rows = self.__cursor.fetchall()
        if len(rows):
            self.__Errors.append('ID has been registered')

    def email(self,str):

        connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
        self.__cursor = connection.cursor()
        self.__cursor.execute("SELECT * from Users WHERE email = %(userID)s",{'userID':str})
        rows = self.__cursor.fetchall()
        if len(rows):
            self.__Errors.append('email has been registered')
    def getErrors(self):
        return self.__Errors



def checkRegisterRequest(data):
    checkForm = CheckForm()
    checkForm.userid(data['userid'])
    checkForm.name(data['name'])
    checkForm.passwd(data['passwd'])
    checkForm.passwdConfirm(data['passwd'],data['passwdConfirm'])
    checkForm.email(data['email'])
    Errors = checkForm.getErrors()
    checkRepeat = CheckRepeat()
    if 'email has illegal characters' not in Errors and 'email format error' not in Errors:
        checkRepeat.email(data['email'])
    if 'ID has illegal characters' not in Errors and 'ID length error' not in Errors:
        checkRepeat.userid(data['userid'])
    for error in checkRepeat.getErrors():
        Errors.append(error)

    return Errors



user=Blueprint("user",__name__)

@user.route('/')
def index():
    return "User route"



@user.route('/register',methods=['POST'])
def register():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    cursor = connection.cursor()
    info['name'] = request.values.get('name')
    info['userid'] = request.values.get('userid')
    info['passwd'] = request.values.get('passwd')
    info['passwdConfirm'] = request.values.get('passwdConfirm')
    info['email'] = request.values.get('email')
    # print(info)
    errors = checkRegisterRequest(info)

    info['errors'] = errors

    if len(info['errors'])==0:
        try:
            insertString = 'INSERT INTO Users(name,userid,password,email,isAdmin)values(%(Name)s,%(userId)s,%(password)s,%(email)s,%(isAdmin)s)'
            md5 = hashlib.md5()
            md5.update((request.values.get('passwd')).encode("utf8"))
            cursor.execute(insertString, {'Name':info['name'], 'userId':info['userid'],'password': md5.hexdigest(),'email':info['email'],'isAdmin':False})
            connection.commit()
        except Exception:
            traceback.print_exc()
            connection.rollback()
            info['errors'] = 'register fail'

    del info['passwd']
    del info['passwdConfirm']

    return jsonify(info)



@user.route('/login',methods=['POST'])
def login():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    userid = request.values.get('userid')
    passwd = request.values.get('passwd')
    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE userID = %(userId)s",{'userId':userid})
    rows = cursor.fetchall()
    connection.commit()
    Errors = []
    if not len(rows):
        Errors.append('userID doesn\'t exist')
    else:
        cursor.execute("SELECT * from Users WHERE userID = %(userId)s",{'userId':userid})
        rows = cursor.fetchall()
        connection.commit()
        row = rows[0]
        md5 = hashlib.md5()
        md5.update(passwd.encode("utf8"))
        # ! password current index is 4
        if md5.hexdigest() == row[4]:
            accessKey = ''.join(random.choices(string.ascii_letters + string.digits, k = 16))+'-'+userid
            now = datetime.datetime.today()
            now = now.strftime('%Y-%m-%d')

            info['accessKey'] = accessKey
            try:
                cursor.execute("UPDATE Users SET accessKey = %(accessKey)s, lastAccessTime = %(Now)s WHERE userID = %(userID)s",{'accessKey':accessKey,'Now':now,'userID':userid})
                connection.commit()
            except:
                Errors.append('login error')
        else:
            Errors.append('password error')
        info['isAdmin'] = row[7]
        info['userID'] = row[1]

    info['errors'] = Errors
    return jsonify(info)


class CheckEmail():
    def __init__(self):
        self.__Errors=[]

    def userid(self,str):
        connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
        cursor = connection.cursor()
        cursor.execute("SELECT * from Users WHERE userID = %(userID)s",{'userID':str})
        rows = cursor.fetchall()
        if not len(rows):
            self.__Errors.append('ID has not found')
        else:
            now = datetime.datetime.today()
            now = now.strftime('%Y-%m-%d')
            if(now==rows[0][16]):
                self.__Errors.append('today has set Identify code')

    def getErrors(self):
        return self.__Errors


@user.route('/setIdentityCode',methods=['POST'])
def setIdentityCode():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    cursor = connection.cursor()
    userid = request.values.get('userid')
    cursor.execute("SELECT * from Users WHERE userID = %(userID)s",{'userID':userid})
    checkEmail=CheckEmail()
    checkEmail.userid(userid)
    info['errors'] = checkEmail.getErrors()
    connection.commit()
    if len(info['errors'])==0:
        info['userID'] = userid
        try:
            IdentityCode = ""
            for i in range(6):
                tmp=random.randint(0,2)
                if tmp==0:
                    tmp=random.randint(0,9)
                    IdentityCode += str(tmp)
                elif tmp==1:
                    tmp=random.randint(65,90)
                    IdentityCode += chr(tmp)
                else :
                    tmp=random.randint(97,122)
                    IdentityCode += chr(tmp)
            cursor.execute("UPDATE Users SET IdentityCode = %(IdentityCode)s WHERE userID = %(userID)s",{'IdentityCode':IdentityCode,'userID':userid})
            connection.commit()
            now = datetime.datetime.today()
            now = now.strftime('%Y-%m-%d')
            cursor.execute("UPDATE Users SET lastSendIdentifyCodeTime = %(lastSendIdentifyCodeTime)s WHERE userID = %(userID)s",{'lastSendIdentifyCodeTime':now,'userID':userid})
            connection.commit()
            cursor.execute("UPDATE Users SET tryIdentifyCodeCount = %(tryIdentifyCodeCount)s WHERE userID = %(userID)s",{'tryIdentifyCodeCount':"0",'userID':userid})
            connection.commit()
        except Exception:
            traceback.print_exc()
            connection.rollback()
            info['errors'] = 'setIdentityCode fail'
    return jsonify(info)

def checkPassWord(data):
    checkForm = CheckForm()
    checkForm.passwd(data['passwd'])
    checkForm.passwdConfirm(data['passwd'],data['passwdConfirm'])
    Errors = checkForm.getErrors()
    return Errors

@user.route('/checkIdentityCode',methods=['POST'])
def checkIdentityCode():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    errors=[]
    cursor = connection.cursor()
    userid = request.values.get('userid')
    IdentityCode=request.values.get('IdentityCode')

    cursor.execute("SELECT * from Users WHERE userID = %(userID)s",{'userID':userid})
    rows = cursor.fetchall()
    connection.commit()
    tryIdentifyCodeCount = rows[0][17]
    if(tryIdentifyCodeCount == None):
        tryIdentifyCodeCount = 0
    if(tryIdentifyCodeCount == 5):
        errors.append("already try 5 times")

    if(len(errors)==0):
        cursor.execute("SELECT IdentityCode from Users WHERE userID = %(userID)s",{'userID':userid})
        rows = cursor.fetchall()
        connection.commit()
        cursor.execute("UPDATE Users SET tryIdentifyCodeCount = %(tryIdentifyCodeCount)s WHERE userID = %(userID)s",{'tryIdentifyCodeCount':str(tryIdentifyCodeCount+1),'userID':userid})
        connection.commit()
        if rows[0][0]!=IdentityCode:
            errors.append('IdentityCode error')
        else:
            accessKey = ''.join(random.choices(string.ascii_letters + string.digits, k = 16))+'-'+userid
            now = datetime.datetime.today()
            now = now.strftime('%Y-%m-%d')
            cursor.execute("UPDATE Users SET accessKey = %(accessKey)s, lastAccessTime = %(lastAccessTime)s WHERE userID = %(userID)s",{'accessKey':accessKey,'lastAccessTime':now,'userID':userid})
            connection.commit()
            info['accessKey']=accessKey
    info['errors']=errors
    return jsonify(info)

@user.route('/resetPassword',methods=['POST'])
def resetPassword():
    connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])
    info = dict()
    errors=[]
    cursor=connection.cursor()
    info['passwd'] = request.values.get('passwd')
    info['passwdConfirm'] = request.values.get('passwdConfirm')
    info['accessKey'] = request.values.get('accessKey')
    errors = checkPassWord(info)
    cursor.execute("SELECT userID from Users WHERE accessKey = %(accessKey)s",{'accessKey':info['accessKey']})
    rows = cursor.fetchall()
    connection.commit()
    if not len(rows):
        errors.append('not pass identityCode yet!')
    info['errors'] = errors
    if len(info['errors'])==0:
        try:
            md5 = hashlib.md5()
            md5.update((request.values.get('passwd')).encode("utf8"))
            cursor.execute("UPDATE Users SET password = %(password)s WHERE accessKey = %(accessKey)s", {'password':md5.hexdigest(),'accessKey':info['accessKey']})
            connection.commit()
        except Exception:
            traceback.print_exc()
            connection.rollback()
            info['errors'] = 'register fail'
    del info['passwd']
    del info['passwdConfirm']
    return jsonify(info)