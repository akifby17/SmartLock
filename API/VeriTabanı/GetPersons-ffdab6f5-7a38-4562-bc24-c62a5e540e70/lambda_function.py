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

    # Veritabanı bağlantısını oluşturuyoruz
    connection = pymysql.connect(
        host=db_host,
        user=db_user,
        password=db_password,
        database=db_name
    )

    try:
        with connection.cursor() as cursor:
            # Tüm verileri almak için SQL sorgusu
            sql = "SELECT id, name, surname, Date FROM Persons"
            cursor.execute(sql)
            results = cursor.fetchall()  # Tüm kayıtları al

            # Sonuçları JSON formatına dönüştür
            people = []
            for row in results:
                person = {
                    "ID": row[0],
                    "Name": row[1],
                    "Surname": row[2],
                }
                
                # Date alanı varsa, datetime objesini ISO 8601 formatında string'e dönüştür
                if row[3]:
                    # row[3] timestamp değeri, datetime objesine dönüştürülüyor
                    person["Date"] = row[3].strftime('%Y-%m-%d')
                else:
                    person["Date"] = None  # Eğer tarih yoksa None olarak ayarlayabiliriz

                people.append(person)

            return {
                'statusCode': 200,
                'body': json.dumps(people)  # Tüm kişileri JSON formatında döndür
            }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({"error": str(e)})  # Hata mesajını JSON formatında döndür
        }

    finally:
        connection.close()
