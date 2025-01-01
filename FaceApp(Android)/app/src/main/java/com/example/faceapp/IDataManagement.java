package com.example.faceapp;
/*
 * Bu interface, veri yönetimi ile ilgili temel işlevleri tanımlamak için kullanılır.
 *
 * - `LoadData()` metodu, verilerin yüklenmesi veya alınması işlemini gerçekleştirmek için çağrılır.
 *
 * Bu yapı, veri yönetimiyle ilgili farklı sınıflarda ortak bir davranış belirlemek amacıyla tasarlanmıştır.
 */

public interface IDataManagement {
    void LoadData();
}
