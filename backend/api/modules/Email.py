from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from flask import Blueprint,request,jsonify
import pymysql
import yaml
import traceback
import string
import smtplib

with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])

with open('GmailConfig.yml','r') as a:
    mailUserData=yaml.safe_load(a)

GmailAccount=mailUserData['GmailAccount']['Account']
Gmailpasswd=mailUserData['GmailAccount']['password']

Email=Blueprint("Email",__name__)

@Email.route('/')
def index():
    return "Email route"

@Email.route('/sendEmail',methods=['POST'])    
def sendEmail():
    info = dict()
    cursor = connection.cursor()
    userid = request.values.get('userid')
    cursor.execute("SELECT email, IdentityCode from Users WHERE userID = %s",userid)
    rows = cursor.fetchall()
    connection.commit()
    content = MIMEMultipart()  #建立MIMEMultipart物件
    content["subject"] = "We Are Family"  #郵件標題
    content["from"] = mailUserData['GmailAccount']['Account']  #寄件者
    content["to"] = rows[0][0]#收件者
    message='your identityCode is '+rows[0][1]
    content.attach(MIMEText(message))  #郵件內容


    with smtplib.SMTP(host="smtp.gmail.com", port="587") as smtp:  # 設定SMTP伺服器
        try:
            smtp.ehlo()  # 驗證SMTP伺服器
            smtp.starttls()  # 建立加密傳輸
            smtp.login(GmailAccount, Gmailpasswd)  # 登入寄件者gmail
            smtp.send_message(content)  # 寄送郵件
            print('Sent message successfully....')
        except Exception as e:
            info['error']='Sent message failed'
            e.traceback()
    info['email']=rows[0][0]
    info['userId']=userid
    return jsonify(info)
