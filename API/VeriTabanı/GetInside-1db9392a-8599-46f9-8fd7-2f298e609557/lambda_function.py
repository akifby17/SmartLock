import pymysql
import os
import json
from datetime import datetime

def lambda_handler(event, context):
    # MySQL bağlantı bilgileri (bunları environment variables olarak ekleyin)
    db_host = os.environ['DB_HOST']  # DB_HOST, veritabanı adresini içermeli
    db_user = os.environ['DB_USER']  # DB_USER, veritabanı kullanıcı adını içermeli
    db_password = os.environ['DB_PASSWORD']  # DB_PASSWORD, veritabanı parolasını içermeli
    db_name = os.environ['DB_NAME']  # DB_NAME, veritabanı adını içermeli
    db_port = int(os.environ.get('DB_PORT', 3306))  # Varsayılan MySQL portu

    try:
        # Veritabanı bağlantısını oluşturuyoruz
        connection = pymysql.connect(
            host=db_host,
            user=db_user,
            password=db_password,
            database=db_name,
            port=db_port,
            cursorclass=pymysql.cursors.DictCursor  # Sonuçları sözlük formatında almak için
        )

        with connection.cursor() as cursor:
            # SQL sorgusu: En son giriş yapmış ve çıkışı olmayan kullanıcılar
            sql = """
            SELECT L.ID, L.Name, L.Surname, L.date AS login_time
            FROM Login L
            LEFT JOIN Logout R ON L.ID = R.ID AND L.date < R.date
            WHERE R.ID IS NULL
              AND L.date = (
                SELECT MAX(date)
                FROM Login
                WHERE ID = L.ID
              );
            """
            
            cursor.execute(sql)
            results = cursor.fetchall()  # Sonuçları al

            # Sonuçları JSON formatına dönüştür
            data = []
            for row in results:
                login_info = {
                    "ID": row['ID'],
                    "Name": row['Name'],
                    "Surname": row['Surname'],
                    "Date": row['login_time'].strftime('%H:%M:%S') if isinstance(row['login_time'], datetime) else None
                }
                data.append(login_info)

            # Veriyi doğrudan JSON array olarak döndürüyoruz
            return {
                'statusCode': 200,
                'body': json.dumps(data),  # JSON Array olarak döndürüyoruz
                'headers': {'Content-Type': 'application/json'}
            }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({"error": str(e)}),
            'headers': {'Content-Type': 'application/json'}
        }

    finally:
        connection.close()
