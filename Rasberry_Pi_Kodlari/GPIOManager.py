import RPi.GPIO as GPIO
from gpiozero import AngularServo
from gpiozero.pins.pigpio import PiGPIOFactory
import time
import asyncio
class GPIOManager:
    """GPIO pinlerini birden fazla sınıfda kullanmamız gerekiyordu fakat gpio pinleri yapılandırlırken
    en son hangi sınıfta GPIO.setmode() fonksiyonu kullanıldaysa sadece o snıftaki pinler aktif oluyordu
    süreci tek bir sınıftan kontrol etmek için GPIOManager sınıfını oluşturduk
    bu sayede tek bir GPIOManager nesnesi ile farklı sınıflardan gpio pinlerini yönetebildik"""
    def __init__(self):
        GPIO.setmode(GPIO.BCM)
        self.configured_pins = {}

    def setup_pin(self, pin, mode):
        """pin ayarlama fonksiyonu"""
        if pin not in self.configured_pins:
            GPIO.setup(pin, mode)
            self.configured_pins[pin] = mode

    def output(self, pin, state):
        if pin in self.configured_pins and self.configured_pins[pin] == GPIO.OUT:
            GPIO.output(pin, state)
        else:
            raise RuntimeError(f"error {pin} ")

    def cleanup(self, pin=None):
        """pinleri sıfırlama"""
        if pin:
            GPIO.cleanup(pin)
            if pin in self.configured_pins:
                del self.configured_pins[pin]
        else:
            GPIO.cleanup()
            self.configured_pins.clear()
            
    def add_interrupt_handler(self, pin, edge, callback, down,bouncetime=200):
        """kesme ekleme fonksiyonu"""
        if(down):
            GPIO.setup(pin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
        else:
            GPIO.setup(pin, GPIO.IN, pull_up_down=GPIO.PUD_UP)
        GPIO.add_event_detect(pin, edge, callback=callback, bouncetime=bouncetime)
    async def open_door(self, pin, delay=2):
        factory = PiGPIOFactory()
        servo = AngularServo(pin, min_angle=0, max_angle=90, pin_factory=factory)
        print("Opening door...")

        try:
            servo.angle = 90
            time.sleep(delay)

            servo.angle = 0
            time.sleep(0.5)
        finally:
            print("finaly")
            servo.detach()
            
            
            
