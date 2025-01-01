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
        # HTTP API kullanıldığı için body'den veriyi alıyoruz
        body = json.loads(event['body'])  # Gövdeyi JSON formatında çöz
        person_id = body.get('ID')  # Gövdeden ID'yi al

        if not person_id:
            return {
                'statusCode': 400,
                'body': json.dumps({"error": "ID parametresi gerekli"})  # ID yoksa hata mesajı döndür
            }

        with connection.cursor() as cursor:
            # ID'yi kontrol eden SQL sorgusu
            sql = "SELECT COUNT(*) FROM Persons WHERE id = %s"
            cursor.execute(sql, (person_id,))
            result = cursor.fetchone()

            # Eğer COUNT 1 ise, ID var demektir, yoksa yok
            if result[0] > 0:
                return {
                    'statusCode': 200,
                    'body': json.dumps({"exists": True})  # ID varsa True döndür
                }
            else:
                return {
                    'statusCode': 200,
                    'body': json.dumps({"exists": False})  # ID yoksa False döndür
                }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({"error": str(e)})  # Hata mesajını JSON formatında döndür
        }

    finally:
        connection.close()
