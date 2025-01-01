import time

class LedControl:
    """Bu sınıf sadece rgb ledleri tek bir yerden kullanarak ledleri kontrol etmek amacıyla yazılmıştır
    yapıcı metodunda gpio_manager nesnesini alarak gpıo pinlerine erişim sağlar"""
    def __init__(self, gpio_manager):
        self.gpio_manager = gpio_manager

    def turn_off(self, RED_PIN, GREEN_PIN, BLUE_PIN):
        self.gpio_manager.output(RED_PIN, 0)
        self.gpio_manager.output(GREEN_PIN, 0)
        self.gpio_manager.output(BLUE_PIN, 0)

    def turn_on(self, RED_PIN, GREEN_PIN, BLUE_PIN):
        self.gpio_manager.output(RED_PIN, 1)
        self.gpio_manager.output(GREEN_PIN, 1)
        self.gpio_manager.output(BLUE_PIN, 1)

    def set_color(self, RED_PIN, GREEN_PIN, BLUE_PIN, red, green, blue):
        self.gpio_manager.output(RED_PIN, red)
        self.gpio_manager.output(GREEN_PIN, green)
        self.gpio_manager.output(BLUE_PIN, blue)

    def play_buzzer(self, BUZZER_PIN, melody, duration=0.5):
        import pigpio
        pi = pigpio.pi()
        if not pi.connected:
            print("error")
            return

        try:
            for freq in melody:
                pi.set_PWM_frequency(BUZZER_PIN, freq)
                pi.set_PWM_dutycycle(BUZZER_PIN, 128)
                time.sleep(duration)
            pi.set_PWM_dutycycle(BUZZER_PIN, 0)
        except KeyboardInterrupt:
            print("Melodi kesildi.")
        finally:
            pi.stop()

    def buzzer_on(self, BUZZER_PIN):
        self.gpio_manager.output(BUZZER_PIN, 1)

    def buzzer_off(self, BUZZER_PIN):
        self.gpio_manager.output(BUZZER_PIN, 0)
