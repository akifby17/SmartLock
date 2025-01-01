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

    connection = pymysql.connect(
        host=db_host,
        user=db_user,
        password=db_password,
        database=db_name
    )

    try:
        with connection.cursor() as cursor:
            # SQL ile giriş ve çıkışları eşleştirme
            query = """
    SELECT 
        l.id AS LoginID, l.name, l.surname, l.date AS LoginDate,
        o.date AS LogoutDate
    FROM 
        Login l
    JOIN 
        Logout o
    ON 
        l.name = o.name AND l.surname = o.surname
    WHERE 
        o.date > l.date
    AND 
        o.date = (SELECT MIN(o2.date) FROM Logout o2 WHERE o2.name = l.name AND o2.surname = l.surname AND o2.date > l.date)
    ORDER BY 
        l.date DESC;
"""

            cursor.execute(query)
            results = cursor.fetchall()

            # Sonuçları JSON formatına dönüştürme
            login_logout_data = []
            for row in results:
                login_logout_data.append({
                    "ID": row[0],
                    "Name": row[1],
                    "Surname": row[2],
                    "LoginDate": row[3].strftime('%Y-%m-%d %H:%M:%S'),
                    "LogoutDate": row[4].strftime('%Y-%m-%d %H:%M:%S'),
                })

            return {
                'statusCode': 200,
                'body': json.dumps(login_logout_data)  # Verileri döndür
            }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({"error": str(e)})  # Hata mesajını JSON formatında döndür
        }

    finally:
        connection.close()
