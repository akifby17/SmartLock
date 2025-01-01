import json
import os
import pymysql
import hashlib

# Ortam değişkenlerinden RDS bağlantı bilgilerini alıyoruz.
RDS_HOST = os.getenv('DB_HOST')
RDS_USER = os.getenv('DB_USER')
RDS_PASSWORD = os.getenv('DB_PASSWORD')
RDS_DB_NAME = os.getenv('DB_NAME')


def get_db_connection():
    """RDS bağlantısını kurar."""
    return pymysql.connect(
        host=RDS_HOST,
        user=RDS_USER,
        password=RDS_PASSWORD,
        db=RDS_DB_NAME,
        cursorclass=pymysql.cursors.DictCursor
    )


def hash_password(password):
    """Şifreyi SHA-256 ile hashler."""
    return hashlib.sha256(password.encode()).hexdigest()


def validate_manager_data(data):
    """Manager kayıt verilerini doğrular."""
    required_fields = ['ManagerName', 'ManagerSurname', 'ManagerLogin', 'Password']
    for field in required_fields:
        if field not in data or not data[field]:
            return f"Field {field} is required."
    return None


def validate_user_data(data):
    """User kayıt verilerini doğrular."""
    required_fields = ['UserName', 'UserSurname', 'UserLogin', 'UserPassword']
    for field in required_fields:
        if field not in data or not data[field]:
            return f"Field {field} is required."
    return None


def lambda_handler(event, context):
    try:
        # Request body'yi alıyoruz
        body = json.loads(event.get('body', '{}'))
        action = event.get('path', '')  # Path'den endpoint belirleniyor

        # /manager işlemi
        if action == "/manager":
            validation_error = validate_manager_data(body)
            if validation_error:
                return {
                    "statusCode": 400,
                    "body": json.dumps({"error": validation_error})
                }

            manager_name = body['ManagerName']
            manager_surname = body['ManagerSurname']
            username = body['ManagerLogin']
            password = body['Password']
            hashed_password = hash_password(password)

            with get_db_connection() as connection:
                with connection.cursor() as cursor:
                    # Kullanıcı adı benzersiz mi kontrol ediyoruz
                    cursor.execute("SELECT ID FROM Manager WHERE ManagerLogin = %s", (username,))
                    if cursor.fetchone():
                        return {
                            "statusCode": 409,
                            "body": json.dumps({"HATA": "Kullanici Adi Zaten Alinmis!!!"})
                        }

                    # Yeni manager kaydını ekliyoruz
                    cursor.execute(
                        """
                        INSERT INTO Manager (ManagerName, ManagerSurname, ManagerLogin, Password)
                        VALUES (%s, %s, %s, %s)
                        """,
                        (manager_name, manager_surname, username, hashed_password)
                    )
                    connection.commit()

                    return {
                        "statusCode": 201,
                        "body": json.dumps({"mesaj":"Kayit basarili."})
                    }

        # /user işlemi
        elif action == '/user':
            validation_error = validate_user_data(body)
            if validation_error:
                return {
                    "statusCode": 400,
                    "body": json.dumps({"error": validation_error})
                }

            user_name = body['UserName']
            user_surname = body['UserSurname']
            user_login = body['UserLogin']
            user_password = body['UserPassword']
            hashed_password = hash_password(user_password)

            with get_db_connection() as connection:
                with connection.cursor() as cursor:
                    # Kullanıcı giriş adı benzersiz mi kontrol ediyoruz
                    cursor.execute("SELECT ID FROM users WHERE UserLogin = %s", (user_login,))
                    if cursor.fetchone():
                        return {
                            "statusCode": 409,
                            "body": json.dumps({"HATA": "Kullanici Adi Zaten Alinmis!!!."})
                        }

                    # Yeni user kaydını ekliyoruz
                    cursor.execute(
                        """
                        INSERT INTO users (UserName, UserSurname, UserLogin, UserPassword)
                        VALUES (%s, %s, %s, %s)
                        """,
                        (user_name, user_surname, user_login, hashed_password)
                    )
                    connection.commit()

                    return {
                        "statusCode": 201,
                        "body": json.dumps({"Mesaj": "Kayit Basarili."})
                    }

        else:
            return {
                "statusCode": 404,
                "body": json.dumps({"error": "Invalid endpoint. Use /manager or /user."})
            }

    except Exception as e:
        return {
            "statusCode": 500,
            "body": json.dumps({"error": str(e)})
        }
