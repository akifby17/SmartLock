import json
import socket
import cv2
import asyncio
import websockets

class WebSocketHandler:
    """Bu sınıf WebSocket işlemleri için yazılmıştır,Sınıfın 2 işlevi vardır 1)telefonla rasberyy arasındaki
    komut iletişimi 2)rasbery den telefona canlı video yayaını yapmak
    Sınıf AWS üzerinde bulunan WebSocket API larına (video için 8080 portu komut için 9999
    ) bağlanara telefonla çift yönlü iletişim kuruyur.
    Bu işlemlerin asenkron olarak yapılmasu gerekiyor çünkü webscoket protokolü sürekli bağlantı gerektiren
    bir protokol program çalışırken arka planda sürekli sunucu ile iletişim halinde olurken,
    aynı zamanda yüz tanıma işlemleride gerçekleştiriliyor.
    Yapıcı metod içerisine gpio manager ve servo pin nesneleri kullanıldı çünkü
    telefondan da pinleri kontrol ediyoruz.
    check_internet_connection() metodu öncelikle interneti kontrol ediyor.
    send video metodu cam_indexi kullanırken ilgili kamerayı açıyor ve görüntüyğ byte_codelara dönüştürüp
     sunucuya gönderiyor sunucu tarafından telefon bu byte kodları alaarak tekrar görüntüye çeviriyor.
     Canlı yayın esnasında kullanıcı el ile kapıyı açmak isterse ilgili komut alınıyor ve gpiomanager ile kapı
     ayrı bir task içerisinde açılıyor.Böylelikle video yayınnı yarıda kesilmemiş oluyor.
     listen_for_commands fonksyonu ise arka planda sürekli olarak sunucudan(telefondan) gelen komutları
     dinliyor.Telefondan komutlar şu şekilde geliyor;{Komut,Veri} fonksyon gelen komutu kontrol ediyor
     eğer işlem komutlarından birisi ise(REGİSTER,DELETE vs) Komutun ilk kısmını ayırarak komut
     değişkenine atıyor virgülden sonraki kısmı ise data değişkenlerine atayarak komut işleniyor.
     send_message fonksiyonunda ise rasberyy pi den telefona işlem geri bildirimleri gönderiliyor.
     bu sayded yapılan işlemlerin sonucu telefondan da görülüyor.
     """
    def __init__(self,gpio_manager,SERVO_PIN):
        self.id=-1
        self.name="default"
        self.surName="default"
        self.cam_index=0;
        self.gpio_manager=gpio_manager
        self.SERVO_PIN=SERVO_PIN
        self.stop = None
        self.command = None
        self.streaming_task = None  # Video yayını için asyncio görevi
        self.cap = None  # Kamera nesnesi
        self.uri_command = "ws://3.64.126.188:9999"  # Komut sunucu URI
        self.uri_video = "ws://3.64.126.188:8080"  # Video sunucu URI
    def check_internet_connection(self):
        try:
            socket.create_connection(("8.8.8.8", 53), timeout=5)
            return True
        except OSError:
            return False

    async def send_video(self):
        async with websockets.connect(self.uri_video) as websocket:
            if self.cap is None:  # Kamera açık değilse
                self.cap = cv2.VideoCapture(self.cam_index)  # Kamerayı açıyoruz
                self.cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)  # Çözünürlük ayarları
                self.cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)
                print("Kamera açıldı ve yayın başlıyor.")
            try:
                while self.command == 'CAM_ON' or self.command=="OPEN_DOOR":  # Yayın başlatma komutu
                    success, frame = self.cap.read()
                    if not success:
                        print("Kamera görüntüsü alınamadı.")
                        break

                    # Görüntüyü JPEG formatına çevir ve kalite ayarla
                    _, buffer = cv2.imencode('.jpg', frame, [int(cv2.IMWRITE_JPEG_QUALITY), 70])
                    # Base64 formatında gönder
                    await websocket.send(buffer.tobytes())  # Asenkron olarak gönder

                    await asyncio.sleep(1 / 24)  # 24 FPS için 41.67 ms bekleyin
                    if self.command=="OPEN_DOOR":
                        await self.gpio_manager.open_door(self.SERVO_PIN, delay=4)
                        self.command="CAM_ON"
                        
                    
                    if self.command == 'CAM_OFF':  
                        self.cap.release() 
                        self.cap = None
                        print("Kamera kapatıldı.")
                        break

            except websockets.exceptions.ConnectionClosed as e:
                print(f"Connection closed: {e}")
            finally:
                if self.cap is not None:  # self.cap None değilse
                    print("finally")

    async def stop_video(self):
        if self.cap:
            self.cap.release()
            self.cap = None
            print("Video yayını durduruldu.")

    async def listen_for_commands(self):
        async with websockets.connect(self.uri_command) as websocket:
            while True:
                self.command = await websocket.recv()  # Komut al
                #self.command = byte_command.decode('utf-8')  # Baytları string'e çevir
                print(f"Komut alındı: {self.command}")
                if self.command.startswith('REGISTER'):
                    parts =self.command.split(',')
                    if len(parts) > 1:  # Eğer virgülden sonra bir değer varsa
                        self.command=parts[0].strip() # Virgülden önceki değeri al
                        try:
                            self.name = parts[1].strip()  # Virgülden sonraki değeri integer'a dönüştür
                            self.surName=parts[2].strip()
                        except ValueError:
                            print("Hata: Virgülden sonraki değer geçerli bir tamsayı değil!")
                        else:
                            print("Hata: Komut eksik veya hatalı!")
                elif self.command.startswith('DELETE'):
                    parts =self.command.split(',')
                    if len(parts) > 1:  # Eğer virgülden sonra bir değer varsa
                        self.command=parts[0].strip() # Virgülden önceki değeri al
                        try:
                            self.id = int(parts[1].strip())  # Virgülden sonraki değeri integer'a dönüştür

                        except ValueError:
                            print("Hata: Virgülden sonraki değer geçerli bir tamsayı değil!")
                elif self.command.startswith('CAM_ON'):
                    parts=self.command.split(',')
                    if len(parts)>1:
                        self.command=parts[0].strip()
                        try:
                            self.cam_index=int(parts[1].strip())
                        except ValueError:
                            print("hata")
                    else:
                            print("Hata: Komut eksik veya hatalı!")

    async def send_message(self, message):
        async with websockets.connect(self.uri_command) as websocket:
            try:
                if isinstance(message, (dict, list)):  # Eğer mesaj dict veya list ise
                    message = json.dumps(message)  # JSON formatına dönüştür
                await websocket.send(message)  # Mesajı gönder
                print(f"Mesaj gönderildi: {message}")
            except websockets.exceptions.ConnectionClosed as e:
                print(f"Mesaj gönderilemedi. Bağlantı kapalı: {e}")
            except TypeError as e:
                print(f"Hatalı veri türü: {e}")

    async def run(self):
        await self.listen_for_commands()
        
