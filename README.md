# WeAreFamily

A platform that meets the needs of people forming groups to buy family plans
<br/><br/>
[![video](https://user-images.githubusercontent.com/64363701/123120199-b2725980-d476-11eb-92ce-8c3cee218059.png)](https://youtu.be/4Lg8X0q9xzQ)

## Switch Local/Network Server
```
更改sample.global.GlobalVariable.server'
因為polling的關係，目前很容易超過每小時3600次的詢問次數限制
Local: "http://127.0.0.1:13261/"
Network: "https://java-waf-api.herokuapp.com/"
```


## 需要填的config
```
backend/api/config.yml
backend/api/GMailConfig.yml
backend/database/docker-compose.yml
```

## API
先用pyenv把python版本切換到3.8


前置作業
```
cd backend/api/
pipenv install
pipenv run python initDatabase.py
```

```
cd backend/api/
pipenv run python app.py

or

pipenv shell
python app.py
```
## Database
啟用前記得先設定docker-compose.yml
```
cd backend/database/
docker-compose up
```
## Port
 - 13261 : API
 - 3306 &nbsp; : MySQL
 - 5000 &nbsp; : PhpMyAdmin
