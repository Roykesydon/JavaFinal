# app.py
from flask import Flask
from modules.user import user
from modules.posts import posts
from modules.Email import Email

app=Flask(__name__)
@app.route('/')
def index():
    return "Hello Flask"

app.register_blueprint(posts,url_prefix='/posts')
app.register_blueprint(user,url_prefix='/user')
app.register_blueprint(Email,url_prefix='/Email')


if __name__=='__main__':
    app.run(port='13261')