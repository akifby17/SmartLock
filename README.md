Hocam Merhabalar Mobil Uygulamanın APK sınıda sundum.

Mobil uygulama kullanıcı adi:akif 
Şifre:1234 Yönetici girişini kullanabilirsiniz

WebSocket Sunucuları:
ws://3.121.99.11:9999 (komut için)
ws://3.121.99.11:8080 (video için)

WebSocket Sunucularına ücretsiz kullandığımız için IP ler bazen
değişebiliyor.Eğer bağlanamazsanız Teams üzerinden  ulaşabilirsiniz.

Ayrıca Aşağıdaki API endpointlerine PostMan ile  test edebilirsiniz.


Check Login:https://0k7lgzdne2.execute-api.eu-central-1.amazonaws.com/CheckLogin

Delete Manager:https://yi6vb9dkdd.execute-api.eu-central-1.amazonaws.com/prod/delete_manager

Delete User:https://yi6vb9dkdd.execute-api.eu-central-1.amazonaws.com/prod/delete_user

Delete Person:https://wo6stmbasf.execute-api.eu-central-1.amazonaws.com/DeletePerson

Get Login-Logout:https://fndbr4lqf1.execute-api.eu-central-1.amazonaws.com/GetLoginLogout

Get Inside:https://ap94b4jqr0.execute-api.eu-central-1.amazonaws.com/GetInside

Örnek JSON: 
Delete Person:
{
  "ID": 123
}

Delete Manager:
{
  "ManagerLogin": "manager_username"
}

Delete User:
{
  "UserLogin": "user_username"
}

Get istekleri için metodu sadece GET yapmanız yeterli.

