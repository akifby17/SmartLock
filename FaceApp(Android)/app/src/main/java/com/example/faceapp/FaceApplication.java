package com.example.faceapp;

import android.app.Application;

public class FaceApplication extends Application {
    /*Bu sınıfın oluşturulma sebebi Yönetici kontrolü için bir bayrak değişkene ihtiyaç duymamuz.
    * Ama bu değişkene tüm sınıflardan erişmemiz gerektiç
    * Bu sınıf sayesinde Uygulama yaşam döngüsü boyunca aktif olacak bir değişkenimiz oldu.
    * */
    private static boolean isAdmin;
    @Override
    public void onCreate() {
        super.onCreate();
        isAdmin=false;
    }
    public static boolean getGlobalVariable() {
        return isAdmin;
    }
    public static void setGlobalVariable(boolean value) {
        isAdmin = value;
    }
}
