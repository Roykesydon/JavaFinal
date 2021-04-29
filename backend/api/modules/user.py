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

connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'],charset='utf8')



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
        if not re.search(r"(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$)",str):
            self.__Errors.append('email format error')

    def getErrors(self):
        return self.__Errors


class CheckRepeat():
    global connection

    def __init__(self):
        self.__Errors=[]
        self.__cursor = connection.cursor()
    def userid(self,str):
        self.__cursor.execute("SELECT * from Users WHERE userID = %s",str)
        rows = self.__cursor.fetchall()
        if len(rows):
            self.__Errors.append('ID has been registered')

    def email(self,str):
        self.__cursor.execute("SELECT * from Users WHERE email = %s",str)
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
            insertString = 'INSERT INTO Users(name,userid,password,email,bad,good)values(%s,%s,%s,%s,%s,%s)'
            md5 = hashlib.md5()
            md5.update((request.values.get('passwd')).encode("utf8"))
            cursor.execute(insertString, (info['name'], info['userid'], md5.hexdigest(),info['email'],0,0))
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
    info = dict()
    userid = request.values.get('userid')
    passwd = request.values.get('passwd')
    cursor = connection.cursor()
    cursor.execute("SELECT * from Users WHERE userID = %s",userid)
    rows = cursor.fetchall()
    Errors = []
    if not len(rows):
        Errors.append('userID doesn\'t exist')
    else:
        cursor.execute("SELECT * from Users WHERE userID = %s",userid)
        rows = cursor.fetchall()
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
                cursor.execute("UPDATE Users SET accessKey = %s, lastAccessTime = %s WHERE userID = %s",(accessKey,now,userid))
                connection.commit()
            except:
                Errors.append('login error')
        else:
            Errors.append('password error')

    info['errors'] = Errors
    return jsonify(info)