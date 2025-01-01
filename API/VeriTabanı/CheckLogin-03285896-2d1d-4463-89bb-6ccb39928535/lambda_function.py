import json
import os
import pymysql  # MySQL için
import hashlib  # Hashleme için
import boto3

# Ortam değişkenlerinden RDS bağlantı bilgilerini alıyoruz.
RDS_HOST = os.getenv('RDS_HOST')
RDS_USER = os.getenv('RDS_USER')
RDS_PASSWORD = os.getenv('RDS_PASSWORD')
RDS_DB_NAME = os.getenv('RDS_DB_NAME')


def get_db_connection():
    return pymysql.connect(
        host=RDS_HOST,
        user=RDS_USER,
        password=RDS_PASSWORD,
        db=RDS_DB_NAME,
        cursorclass=pymysql.cursors.DictCursor
    )


# Şifreyi hashleyen fonksiyon
def hash_password(password):
    return hashlib.sha256(password.encode()).hexdigest()


def lambda_handler(event, context):
    try:
        body = json.loads(event.get('body', '{}'))
        username = body.get('username')
        password = body.get('password')
        user_type = body.get('user_type')  # 'user' veya 'admin' olmalı

        if not username or not password or not user_type:
            return {
                "statusCode": 400,
                "body": json.dumps({"error": "Username, password, and user_type are required."})
            }

        # Kullanıcının girdiği şifreyi hashliyoruz.
        hashed_password = hash_password(password)

        with get_db_connection() as connection:
            with connection.cursor() as cursor:
                # Kullanıcı tipine göre doğru tabloyu seçiyoruz.
                if user_type == 'user':
                    cursor.execute("SELECT * FROM users WHERE UserLogin=%s AND UserPassword=%s", (username, hashed_password))
                elif user_type == 'admin':
                    cursor.execute("SELECT * FROM Manager WHERE ManagerLogin=%s AND Password=%s",
                                   (username, hashed_password))
                else:
                    return {
                        "statusCode": 400,
                        "body": json.dumps({"error": "Invalid user type."})
                    }

                result = cursor.fetchone()

                if result:
                    return {
                        "statusCode": 200,
                        "body": json.dumps({"message": f"{user_type.capitalize()} login successful."})
                    }
                else:
                    return {
                        "statusCode": 401,
                        "body": json.dumps({"error": "Invalid credentials."})
                    }

    except Exception as e:
        return {
            "statusCode": 500,
            "body": json.dumps({"error": str(e)})
        }
