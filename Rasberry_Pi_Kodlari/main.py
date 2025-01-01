import os
import asyncio
from time import sleep
import RPi.GPIO as GPIO
from FaceRecognizer import FaceRecognizer
from LedControl import LedControl
from WebSocketHandler import WebSocketHandler
from UniqueSet import UniqueSet
from DatabaseHandler import DatabaseHandler
from GPIOManager import GPIOManager

command='-1' #başlangıç değeri -1 atanıyor
SERVO_PIN=2 #sensörlerin ve aktüatörlerin bağlı olduğu pinler tanımlanıyor
sensor_input_pin = 16
sensor_output_pin=22
RED_PIN=23
GREEN_PIN=24
BLUE_PIN=25
interrupt_input_flag = False
interrupt_output_flag = False
success_melody = [523, 587, 659, 523] # Yüz tanıma işlemi başarılı olursa buzzerda çalacak melodi
failure_melody = [220, 196, 220, 196]  # Yüz tanıma işlemi başarısız olursa buzzerda çalacak melodi

BUZZER_PIN = 17

def input_interrupt(channel):
    #giriş sensörü için kesme fonksiyonu
    global interrupt_input_flag #sınıfın global değişkenini kullanıyor
    interrupt_input_flag = True

def output_interrupt(channel):
    # çıkış sensörü için kesme fonksiyonu
    global interrupt_output_flag
    interrupt_output_flag = True
    
async def main():
    #buradkai global ifadesi sınıfın  değişkenlerini kullancağımızı belirtiyor.
    global command
    global SERVO_PIN
    global interrupt_input_flag
    global interrupt_output_flag
    global BUZZER_PIN
    global success_melody
    global failure_melody
    global sensor_input_pin
    global sensor_output_pin
    api_base_url = "https://yyiggnn0pb.execute-api.eu-central-1.amazonaws.com"  # API temel URL'si
    file_path = os.path.join(os.path.expanduser("~"), "Documents", "unique_set_data.json")
    #Giriş çıkışı önce rasberyy de yapmak için kullanacağımız dosya yolu
    #ilgili sınıfların nesne tanımlamaları
    db_handler = DatabaseHandler(api_base_url)
    gpio_manager = GPIOManager()
    wb_handler=WebSocketHandler(gpio_manager,SERVO_PIN)
    led_control = LedControl(gpio_manager)
    face_recognizer = FaceRecognizer(gpio_manager, led_control)
    set = UniqueSet(file_path)
    #gpıo manager ile gpio pinlerini başlatma
    gpio_manager.setup_pin(RED_PIN, GPIO.OUT)
    gpio_manager.setup_pin(GREEN_PIN, GPIO.OUT)
    gpio_manager.setup_pin(BLUE_PIN, GPIO.OUT)
    gpio_manager.setup_pin(BUZZER_PIN, GPIO.OUT)
    gpio_manager.setup_pin(SERVO_PIN, GPIO.OUT)
    #gpio manager ile kesme fonksiyonlarının tanımlanması
    #burada sensörün alçalan kenar sinyali geldiğinde kesme tetikleniyor.
    gpio_manager.add_interrupt_handler(sensor_input_pin, GPIO.FALLING,callback=input_interrupt,down=False,bouncetime=200)
    gpio_manager.add_interrupt_handler(sensor_output_pin, GPIO.FALLING,callback=output_interrupt,down=False,bouncetime=200)
    #asenksron işlemler için görev oluşturma
    asyncio.create_task(wb_handler.run())
    #program başlarken 2 sn boyunca buzzer ötüyor ve kırmızı led yanıyor
    led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.HIGH, GPIO.LOW, GPIO.LOW)
    led_control.play_buzzer(BUZZER_PIN, success_melody, duration=0.5)
    sleep(2)
    led_control.turn_off(RED_PIN,GREEN_PIN,BLUE_PIN)
    while True:
        #öncelikle internet kontrolü yapılıyor eğer bağlantı başarılı ise diğer işlemlere geçiliyor.
        #eğer internet bağlantısı başarısız olursa kırmızı led ve buzzer sürekli uyarı veriyor
        if not wb_handler.check_internet_connection():
            print("waiting for internet connection")
            led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.HIGH, GPIO.LOW, GPIO.LOW)
            led_control.play_buzzer(BUZZER_PIN, failure_melody, duration=0.5)
            await asyncio.sleep(2)
            led_control.turn_off(RED_PIN,GREEN_PIN,BLUE_PIN)
            continue
        command=wb_handler.command #command değişkenine wb_handler dan gelen deperler sürekli atanıyor.
            
        if  interrupt_input_flag:
            #eğer giriş kesme si tetiklenirse giriş kamaerası açılıyor(index 0)
           user_login_id=face_recognizer.recognize_faces_input(0)
            #üstteki fonksiyondan bir yüz id değeri dönüyor(eğer tanınmazsa değer -1)
           wb_handler.command = "-1"#komut bir sonraki işlemler için başlangıç durumuna atanıyor.
           interrupt_input_flag=False#kesme bayrağıda başlangıç değerine atanıyor
           if user_login_id!=-1 and set.add(user_login_id):
               #yüz tanınırsa ve daha önce giriş yapmadıysa bu blok çalışyor
               response=db_handler.post_login(user_login_id)
               #veri tabanına yüz id si gönderiliyor.
               led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.LOW, GPIO.HIGH, GPIO.LOW)
               print(response)
               led_control.play_buzzer(BUZZER_PIN, success_melody, duration=0.5)
               #yeşil ışık ve giriş melodisi çalıyor
               await gpio_manager.open_door(pin=SERVO_PIN, delay=4)
               #kapı 4 sn açılıyor
               await wb_handler.send_message(response)
               #apı yanıtı sunucuya gönderiliyor.
           elif user_login_id!=-1 and not set.add(user_login_id):
               #kullanıcı zaten içerdeyse kapı açılmıyor
               print("kullanıcı zaten giriş yapmış")
               led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.LOW, GPIO.LOW, GPIO.HIGH)
               led_control.play_buzzer(BUZZER_PIN, failure_melody, duration=0.5)
           else:
               #yüz tanınmazsa bu blok çalışıyor
               led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.HIGH, GPIO.LOW, GPIO.LOW)
               led_control.play_buzzer(BUZZER_PIN, failure_melody, duration=0.5)
               print("taninmadi")
           led_control.turn_off(RED_PIN,GREEN_PIN,BLUE_PIN)
            #ledler sönüyor.
        elif interrupt_output_flag :
            # bu blokda aynı şekilde giriş işlemlerini çıkış için yapıyor
            #sadece tek fark çıkış kamaersını ve çıkış kesmesini kullanıyor.
            interrupt_output_flag=False
            user_logout_id=face_recognizer.recognize_faces_output(2)
            if user_logout_id!=-1 and set.remove(user_logout_id):
                led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.LOW, GPIO.HIGH, GPIO.LOW)
                led_control.play_buzzer(BUZZER_PIN, success_melody, duration=0.5)
                await gpio_manager.open_door(pin=SERVO_PIN, delay=4)
                response=db_handler.post_logout(user_logout_id)
                led_control.turn_off(RED_PIN,GREEN_PIN,BLUE_PIN)
                await wb_handler.send_message(response)
                print(response)
            elif user_logout_id!=-1 and not set.remove(user_logout_id):
               led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.LOW, GPIO.LOW, GPIO.HIGH)
               print("kullanıcı giris yapmamıs")
               led_control.play_buzzer(BUZZER_PIN, failure_melody, duration=0.5)
               led_control.turn_off(RED_PIN,GREEN_PIN,BLUE_PIN)
            else:
                led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.HIGH, GPIO.LOW, GPIO.LOW)
                led_control.play_buzzer(BUZZER_PIN, failure_melody, duration=0.5)
                print("Yüz Taninmadi")
            led_control.turn_off(RED_PIN,GREEN_PIN,BLUE_PIN)
        elif wb_handler.command == 'REGISTER':
            """
            Kayıt komutu gelirse bu blok çalışıyor.wb_handler da daha önce 
            ayrılışırılan veriler kayıt için kullanıyor(isim-soyisim) """
            response_data = db_handler.add_user(wb_handler.name, wb_handler.surName)
            face_recognizer.add_face(int(response_data.get('Personel_No:',None)),0,RED_PIN, GREEN_PIN, BLUE_PIN,BUZZER_PIN)
            face_recognizer.train_model()# Yüz eklendikten sonra model yeniden eğitiliyor
            print("API Yanıtı:", response_data)
            await wb_handler.send_message(response_data)
            wb_handler.command = "-1"
            led_control.turn_off(RED_PIN,GREEN_PIN,BLUE_PIN)
        elif wb_handler.command == 'DELETE':
            print("id değeri",wb_handler.id)
            face_recognizer.delete_face(wb_handler.id)
            response_data=db_handler.delete_user(wb_handler.id)
            print("API Yanıtı:", response_data)
            await wb_handler.send_message(response_data)
            wb_handler.command = "-1"
        elif command=='CAM_ON':
            """
            canlı yayına geçme komutu kamera indeksi wb_handlerdan geliyor.
            """
            led_control.set_color(RED_PIN,GREEN_PIN,BLUE_PIN,GPIO.HIGH, GPIO.HIGH, GPIO.HIGH)
            await wb_handler.send_video()
            wb_handler.command='-1'
        elif command=='CAM_OFF':
            #canlı yayından çıkmak için
            led_control.turn_off(RED_PIN,GREEN_PIN,BLUE_PIN)
            await wb_handler.stop_video()
            wb_handler.command='-1'
        await asyncio.sleep(0.1)
if __name__ == "__main__":
    asyncio.run(main())
