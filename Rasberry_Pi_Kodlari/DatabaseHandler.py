import requests

class DatabaseHandler:
    """Bu class da veritabanı API işlemleri tanımlandı ilgili api Url kullanılarak veritabanı işlemleri
     yapılıyor.API ler aws lambda da bulunuyor ve API URl ile erişim sağlanıyor"""
    def __init__(self, base_url):
        self.base_url = base_url  # API'nin temel URL'si
    def add_user(self,  name, surname):
        """Yeni bir kullanıcı eklemek için API'ye istek gönderir."""
        payload = {
            "Name": name,
            "Surname": surname
        }
        response = requests.post(f"{self.base_url}/PostPerson", json=payload)
        return response.json()  # API yanıtını döndür

    def delete_user(self, user_id):
        """Kullanıcıyı silmek için API'ye istek gönderir."""
        payload = {
            "ID": user_id,
        }
        response = requests.post("https://wo6stmbasf.execute-api.eu-central-1.amazonaws.com/DeletePerson",json=payload)
        return response.json()  # API yanıtını döndür

    def is_exists(self,user_id):
        payload = {
            "ID": user_id
        }
        response = requests.post("https://a8577f05gc.execute-api.eu-central-1.amazonaws.com/IsExist",json=payload)
        response_json = response.json()
        if response_json.get("exists") == True:
            return True
        else:
            return False

    def post_login(self,user_id):
        payload = {
            "ID": user_id
        }
        response = requests.post(f"https://22ya1w9rxc.execute-api.eu-central-1.amazonaws.com/PostLogin",json=payload)
        return response.json()
    def post_logout(self,user_id):#çıkış işlemi olduğu zaman api ye istek gönderir
        payload = {
            "ID": user_id
        }
        response = requests.post(f"https://2hyehvfk65.execute-api.eu-central-1.amazonaws.com/PostLogout",json=payload)
        return response.json()
