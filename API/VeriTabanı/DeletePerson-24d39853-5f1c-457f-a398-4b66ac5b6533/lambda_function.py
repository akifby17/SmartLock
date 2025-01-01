import pymysql
import os
import json

def lambda_handler(event, context):
    # MySQL bağlantı bilgileri (bunları environment variables olarak ekleyin)
    db_host = os.environ['DB_HOST']
    db_user = os.environ['DB_USER']
    db_password = os.environ['DB_PASSWORD']
    db_name = os.environ['DB_NAME']

    connection = pymysql.connect(
        host=db_host,
        user=db_user,
        password=db_password,
        database=db_name
    )

    try:
        # API üzerinden gelecek verileri JSON'dan yükle
        body = json.loads(event['body'])  # Burada event'in body kısmını alıyoruz
        person_id = body['ID']  # JSON'dan ID al

        with connection.cursor() as cursor:
            # SQL sorgusu
            sql = "DELETE FROM Persons WHERE id = %s"
            cursor.execute(sql, (person_id,))
            connection.commit()

            # Etkili kayıt sayısını kontrol et
            if cursor.rowcount == 0:
                return {
                    'statusCode': 404,
                    'body': json.dumps({"error": "Person not found."})  # Kişi bulunamadı hatası
                }

            return {
                'statusCode': 200,
                'body': json.dumps({"Sonuc:": f"{person_id} Numarali Personel Silindi."})  # Başarılı silme mesajı
            }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': json.dumps({"error": str(e)})  # Hata mesajını JSON formatında döndür
        }

    finally:
        connection.close()
