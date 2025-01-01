
import numpy as np
import os
import cv2
import time

"""1) Yapıcı metoda yüz verisini kaydı tutması için bir dataset klasörü veriyoruz bu sayede fotoğrafı
 çekilen yüzler buraya kaydediliyor.Ayrıca yüz tanıma işlemleeirnin yapılması için bir yüz modeli
 dosyasıda veriliyor.
 
 2) Gpio pin kontrolünün tek bir sınıftan yapılması için kendi oluşturduğumuz gpıomanager adlı
 sınıf ile soyutlamasını gerçekleştirdik.Bu sayede GPIO pin işlemleri tek bir nesne üzerinden 
 farklı sınıflar tarafından kullanılabiliyor.FaceRecognizer sınıfındada gpıo işlemleri yapıldığı için
 yapıcı metodu gpıo nesnelerini gönderdik(gpio_manager ve led_control)
 
 3)add_face(self, user_id, video_index,RED_PIN, GREEN_PIN, BLUE_PIN,BUZZER_PIN) fonksiyonu parametre olarak
 aldığı user_id ile kullanıcı yüzünü video_index olarak aldığı kamerayı kullanarak kaydeder.Kayıt işlemi
 yapılırken kulllanıcın 30 adet yüz fotoğrafı çekilir.Mavi led bu süre boyunca yanıp söner ve buzzer da
 sürekli olarak öter.Kayıt işlemi tamamlandıktan sonra yüz verisi id ve fotoğraf numarası ile birlikte
 siyah-beyaz olarak kaydedilir.örneğin id si 5 olan kullanıcının fotoğraf adı user_5_2 buradaki 2 2.
 çekilen fotoğraf olduğunu gösteriyor.Her yüz kaydı ve yüz silme sonrası model yeniden eğitilir(train_model)
 
 4)Yüz tanıma için 2 adet fonksiyon bulunmaktadır.def recognize_faces_input(self,video_index,timeout=10)
 fonksiyonu giriş için kullanılır bu fonskyion aldığı kamera inexini kullanarak 10sn içerisinde yüzü tanır.
 Yüz tanınırsa id değeri döner.Tanınmazsa id değeri varsayılan olarak -1 döner.
 def recognize_faces_output(self,video_index,timeout=10) fonksiyonu ise inputla tamamen aynıdır sadece
 aldığı çıkış kamera index değeriniparametre olarak alır.Bu fonksyonlar
 
 5)delete_face(self, user_id) fonskiyonu parametre olarak aldığı user_id değerini kullanarak eşleşen yüz
 verilerini siler.
 """
class FaceRecognizer:
    def __init__(self,gpio_manager,led_control, dataset_path='dataset', model_path='face_model.yml'):
        self.dataset_path = dataset_path
        self.led_control=led_control
        self.gpio_manager = gpio_manager
        self.model_path = model_path
        self.recognizer = cv2.face.LBPHFaceRecognizer.create()  # Bu satırı değiştirin
        self.face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
        # Modeli ilk başta yükle
        self.load_model()
    def load_model(self):
        # Eğer model dosyası mevcutsa, modeli yükle
        if os.path.exists(self.model_path):
            self.recognizer.read(self.model_path)
            print("Model başarıyla yüklendi.")
        else:
            print("Model dosyası bulunamadı, yeni model oluşturulacak.")

    def save_face(self, user_id, face_image, face_index):
        # Yüz görüntüsünü kaydet
        if not os.path.exists(self.dataset_path):
            os.makedirs(self.dataset_path)
        face_path = os.path.join(self.dataset_path, f'user_{user_id}_{face_index}.jpg')
        cv2.imwrite(face_path, face_image)

    def add_face(self, user_id, video_index,RED_PIN, GREEN_PIN, BLUE_PIN,BUZZER_PIN):
        # Yeni bir yüz ekle
        cap = cv2.VideoCapture(video_index)
        count = 0
        # Mevcut kullanıcının fotoğraf sayısını kontrol et
        existing_faces = [f for f in os.listdir(self.dataset_path) if f.startswith(f'user_{user_id}_')]
        last_face_index = len(existing_faces)  # Eğer daha önce fotoğraf çekildiyse, son fotoğraf numarasını al
        while True:
            ret, frame = cap.read()
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = self.face_cascade.detectMultiScale(gray, 1.3, 5)
            for (x, y, w, h) in faces:
                cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 2)
                face_image = gray[y:y + h, x:x + w]
                # Her fotoğraf için numarayı güncelle
                self.save_face(user_id, face_image, last_face_index + count)
                #alınan yüz verisi kaydedilir.
                count += 1
                #her bir fotoğraf çekilirken mavi led ve buzzer 200 ms yanıp sönüyor.
            self.led_control.buzzer_on(BUZZER_PIN)
            self.led_control.set_color(RED_PIN, GREEN_PIN, BLUE_PIN, red=0, green=0, blue=1)
            time.sleep(0.2)  # 200 ms yanık kalma süresi
            self.led_control.turn_off(RED_PIN, GREEN_PIN, BLUE_PIN)
            self.led_control.buzzer_off(BUZZER_PIN)
            if count >= 30:  # 30 fotoğraf tamamlandı
                break
            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
        cap.release()
        cv2.destroyAllWindows()

    def train_model(self):
        # Modeli eğit
        images = []
        labels = []
        for file in os.listdir(self.dataset_path):
            if file.endswith('.jpg'):
                label = int(file.split('_')[1])  # user_id'den label'ı al
                img_path = os.path.join(self.dataset_path, file)
                img = cv2.imread(img_path, cv2.IMREAD_GRAYSCALE)
                images.append(img)
                labels.append(label)
        if images and labels:  # Eğer yüz verisi varsa
            self.recognizer.train(images, np.array(labels))
            self.recognizer.save(self.model_path)
            print("Model başarıyla eğitildi ve kaydedildi.")
        else:
            print("Yüz verisi bulunamadı, model eğitilemedi.")
    def get_registered_ids(self):
        # Kayıtlı kullanıcıların ID'lerini döndürür
        ids = []
        for file in os.listdir(self.dataset_path):
            if file.endswith('.jpg'):
                label = int(file.split('_')[1])
                if label not in ids:
                    ids.append(label)
        return ids

    def recognize_faces_input(self,video_index,timeout=10):
        #Kapı Girişi için yüz tanıma fonksiyonu video_index değeri olarak 0 alacak
        # Yüz tanıma işlemi
        if not os.path.exists(self.model_path):
            print("Model henüz eğitilmedi veya bulunamadı.")
            return  # Model yoksa tanıma işlemi yapılmaz
        cap = cv2.VideoCapture(video_index)
        recognized = False
        return_value=-1
        start_time=time.time()
        while not recognized:
            ret, frame = cap.read()
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = self.face_cascade.detectMultiScale(gray, 1.3, 5)
            for (x, y, w, h) in faces:
                label, confidence = self.recognizer.predict(gray[y:y + h, x:x + w])

                # Kayıtlı kullanıcılar ve güven skoru kontrolü
                #güven aralığı değeri 30 bu değer ne kadar düşük olursa programı yüzü daha zor tanıyor.
                if confidence < 30 and label in self.get_registered_ids():
                    cv2.putText(frame, f'ID: {label}', (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 0, 0), 2)
                    print("yüz id:",label)
                    recognized = True
                    return_value=label
                else:
                    cv2.putText(frame, 'Taninmiyor', (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 0, 255), 2)

                cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 2)

            """print('Face Recognition', frame)"""

            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
            elapsed_time=time.time()-start_time
            if elapsed_time>timeout:
                break

        cap.release()
        cv2.destroyAllWindows()
        return return_value
    def recognize_faces_output(self,video_index,timeout=10):
        # Yüz tanıma işlemi
        #Giriş fonksyonun çıkış için olan hali sadece vide_index olarak 1 değerini alacak
        if not os.path.exists(self.model_path):
            print("Model henüz eğitilmedi veya bulunamadı.")
            return  # Model yoksa tanıma işlemi yapılmaz
        cap = cv2.VideoCapture(video_index)
        recognized = False
        return_value=-1
        start_time=time.time()
        while not recognized:
            ret, frame = cap.read()
            gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
            faces = self.face_cascade.detectMultiScale(gray, 1.3, 5)
            for (x, y, w, h) in faces:
                label, confidence = self.recognizer.predict(gray[y:y + h, x:x + w])

                # Kayıtlı kullanıcılar ve güven skoru kontrolü
                if confidence < 30 and label in self.get_registered_ids():
                    cv2.putText(frame, f'ID: {label}', (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (255, 0, 0), 2)
                    print("yüz id:",label)
                    recognized = True
                    return_value=label
                else:
                    cv2.putText(frame, 'Taninmiyor', (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.8, (0, 0, 255), 2)

                cv2.rectangle(frame, (x, y), (x + w, y + h), (255, 0, 0), 2)

            """print('Face Recognition', frame)"""

            if cv2.waitKey(1) & 0xFF == ord('q'):
                break
            elapsed_time=time.time()-start_time
            if elapsed_time>timeout:
                break

        cap.release()
        cv2.destroyAllWindows()
        return return_value
    
    def delete_face(self, user_id):
        # Yüz verisini sil
        deleted = False
        for file in os.listdir(self.dataset_path):
            if file.startswith(f'user_{user_id}_'):
                os.remove(os.path.join(self.dataset_path, file))
                deleted = True
        if deleted:
            print(f'Kullanıcı ID: {user_id} için yüz verisi silindi.')
            self.train_model()  # Silme işlemi sonrasında modeli yeniden eğit
        else:
            print(f'Kullanıcı ID: {user_id} bulunamadı.')