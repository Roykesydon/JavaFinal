# blog.py
from flask import Blueprint
import pymysql
import yaml

with open('config.yml', 'r') as f:
    cfg = yaml.safe_load(f)

connection = pymysql.connect(host=cfg['db']['host'],user=cfg['db']['user'],password=cfg['db']['password'],db=cfg['db']['database'])


posts=Blueprint("posts",__name__)

@posts.route('/')
def index():
    return"Posts route"
