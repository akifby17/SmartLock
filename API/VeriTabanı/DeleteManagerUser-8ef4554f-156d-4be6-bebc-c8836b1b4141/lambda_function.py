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


def lambda_handler(event, context):
    try:
        # Request body'yi alıyoruz
        body = json.loads(event.get('body', '{}'))
        action = event.get('path', '')  # Path'den endpoint belirleniyor

        # /delete_manager işlemi
        if action == "/delete_manager":
            if 'ManagerLogin' not in body or not body['ManagerLogin']:
                return {
                    "statusCode": 400,
                    "body": json.dumps({"error": "ManagerLogin is required."})
                }

            manager_login = body['ManagerLogin']

            with get_db_connection() as connection:
                with connection.cursor() as cursor:
                    # ManagerLogin ile eşleşen bir kayıt var mı kontrol ediyoruz
                    cursor.execute("SELECT ID FROM Manager WHERE ManagerLogin = %s", (manager_login,))
                    manager = cursor.fetchone()
                    if not manager:
                        return {
                            "statusCode": 404,
                            "body": json.dumps({"error": "Manager not found."})
                        }

                    # Manager'ı siliyoruz
                    cursor.execute("DELETE FROM Manager WHERE ManagerLogin = %s", (manager_login,))
                    connection.commit()

                    return {
                        "statusCode": 200,
                        "body": json.dumps({"message": "Manager deleted successfully."})
                    }

        # /delete_user işlemi
        elif action == "/delete_user":
            if 'UserLogin' not in body or not body['UserLogin']:
                return {
                    "statusCode": 400,
                    "body": json.dumps({"error": "UserLogin is required."})
                }

            user_login = body['UserLogin']

            with get_db_connection() as connection:
                with connection.cursor() as cursor:
                    # UserLogin ile eşleşen bir kayıt var mı kontrol ediyoruz
                    cursor.execute("SELECT ID FROM users WHERE UserLogin = %s", (user_login,))
                    user = cursor.fetchone()
                    if not user:
                        return {
                            "statusCode": 404,
                            "body": json.dumps({"error": "User not found."})
                        }

                    # User'ı siliyoruz
                    cursor.execute("DELETE FROM users WHERE UserLogin = %s", (user_login,))
                    connection.commit()

                    return {
                        "statusCode": 200,
                        "body": json.dumps({"message": "User deleted successfully."})
                    }

        else:
            return {
                "statusCode": 404,
                "body": json.dumps({"error": "Invalid endpoint. Use /delete_manager or /delete_user."})
            }

    except Exception as e:
        return {
            "statusCode": 500,
            "body": json.dumps({"error": str(e)})
        }
