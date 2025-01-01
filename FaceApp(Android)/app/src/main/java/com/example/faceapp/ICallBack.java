package com.example.faceapp;
/*
 * Bu interface, bir geri çağırma (callback) mekanizması sağlamak için yazıldı.
 * Asenkron işlemler tamamlandığında sonuçları iletmek veya hata durumlarını yönetmek amacıyla tasarlanmıştır.

 * - `onSuccess(T result)` metodu, işlem başarıyla tamamlandığında çağrılır ve sonucu döner.
 * - `onFailure(Exception e)` metodu, işlem sırasında bir hata meydana geldiğinde çağrılır ve hatayı döner.
 *
 * Generic bir yapı kullanılarak, farklı türde sonuçlarla çalışılmasını sağlar.
 */

public interface ICallBack <T>{
    void onSuccess(T result);
    void onFailure(Exception e);
}
