import os
import json
class UniqueSet:
    """Bu sınıfın oluşturulma amacı mevcut giriş yapmış ve çıkış yapmamış kişileri
     veya giriş yapmamış ama çıkış yapmaya çalışan kişilerin
     kontrolünü veri tabanına yazma gereği duymadan dosya işlemi ile kontrol etmek
     add fonksyonu eklenen kişi dosyada yoksa ekleme yapıyor ve true dönüuor
     eğer dosyada varsa ekleme yapmıyor ve false dönüyor
     remove fonksşyonu ise parametre olrak gönderilen kişi dosyada var ise siliyor ve true değer döndürüyor
     yok ise false değer döndürüyor Aslında bu sınıf bir hashset gibi çalışıyor
     1 kişi 1 defa eklenebilir mantığı mevcut"""
    def __init__(self, file_path):
        self.file_path = file_path
        # Dosyanın olduğu dizin
        os.makedirs(os.path.dirname(self.file_path), exist_ok=True)
        self.elements = self.load_data()  # Set verisini dosyadan yükleme

    def add(self, element):
        if element in self.elements:
            return False  # Eleman zaten mevcutsa False
        else:
            self.elements.add(element)  # Set'e ekleme yapma
            self.save_data()  # Yeni eleman eklendiğinde dosyaya kaydetme
            return True  # Başarıyla eklendiğinde True

    def remove(self, element):
        if element in self.elements:
            self.elements.remove(element)  # Set'ten elemanı çıkarma
            self.save_data()  # Eleman çıkarıldığında dosyaya kaydetme
            return True  # Başarıyla çıkarıldığında True
        else:
            return False  # Eleman mevcut değilse False

    def save_data(self):
        """Set'i dosyaya kaydetme"""
        with open(self.file_path, 'w') as f:
            json.dump(list(self.elements), f)  # Set'i listeye çevirip JSON olarak kaydetme

    def load_data(self):
        """Dosyadan set'i yükleme"""
        if os.path.exists(self.file_path):
            with open(self.file_path, 'r') as f:
                data = json.load(f)
                return set(data)  # JSON listesini set'e çevirme
        return set()  # Eğer dosya yoksa boş bir set döndürme

    def __str__(self):
        return str(self.elements)