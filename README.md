# JavaFinalProject


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