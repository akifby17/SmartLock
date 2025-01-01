import pymysql
import os
import json


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
            # API üzerinden gelecek verileri JSON'dan yükle
            body = json.loads(event['body'])  # Burada event'in body kısmını alıyoruz
            name = body['Name']  # JSON'dan Name al
            surname = body['Surname']  # JSON'dan Surname al

            # SQL sorgusu
            sql = "INSERT INTO Persons (name, surname) VALUES (%s, %s)"
            cursor.execute(sql, (name, surname))
            connection.commit()

            # Son eklenen ID'yi al
            last_id = cursor.lastrowid

        return {
            'statusCode': 200,
            'body': json.dumps({
                "Sonuc:": f"Personel {name} {surname} basariyla kaydedildi.",
                "Personel_No:": last_id
            })
        }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({"error": str(e)})  # Hata mesajını JSON formatında döndür
        }

    finally:
        connection.close()
