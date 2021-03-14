# admin.py
from flask import Blueprint,request,jsonify
import pymysql
import yaml

with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])

user=Blueprint("user",__name__)

@user.route('/')
def index():
    return "User route"

@user.route('/register',methods=['POST'])
def register():
    info = dict()
    info['name'] = request.values.get('name')
    info['passwd'] = request.values.get('passwd')
    info['email'] = request.values.get('email')
    return jsonify(info)