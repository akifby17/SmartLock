package com.example.faceapp;
public class Persons
/*Bu sınıf veri tabanındaki bulunan sütunları uygun formatta sunmak için yazıld
* sınıfın değişkenleri aslında veri tabanındaki sütun değerlerini temsil ediyor.
* Veri tabanından veriler direkt tablo olarak getirilemediği için bu sınıfa aktarılıyor.
* */

{
    private int userId;
    private String name;
    private String surName;
    private String Date;
    private String loginDate;
    private String logoutDate;
    public Persons(int userId, String name, String surName,String loginDate,String logoutDate) {
        this.loginDate=loginDate;
        this.logoutDate=logoutDate;
        this.userId = userId;
        this.name = name;
        this.surName = surName;
    }

    public Persons(int userId, String name, String surName, String Date) {
        this.Date=Date;
        this.userId = userId;
        this.name = name;
        this.surName = surName;
    }
    public String getDate(){
        return Date;
    }
    public int getUserId() {
        return userId;
    }
    public void showLogin(){
    }
    public String getName() {
        return name;
    }
    public String getSurName() {
        return surName;
    }
    public void setSurName(String surName) {
        this.surName = surName;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(String loginDate) {
        this.loginDate = loginDate;
    }

    public String getLogoutDate() {
        return logoutDate;
    }

    public void setLogoutDate(String logoutDate) {
        this.logoutDate = logoutDate;
    }
}
