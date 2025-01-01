package com.example.faceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    /*
    * Bu aktivite ayar ekranını açıyor ve uygulama özelliklerine erişim sağlıyor.
    * Özellikler:İçerideki personlleri görüntüleme,Personel listesi görüntülemei(Ortak)
    * Yönetici Rolü Özellikleri:
    * Personel ekleme-çıkarma
    * Yönetici ekleme-çıkarma
    * Kullanıcı ekleme-çıkarma
    * Tıklanan butonlar ile ilgili aktivetelere yönlendiriliyor
    * */
    {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Button btnInside=findViewById(R.id.btnInside);
            Button btnPersonList=findViewById(R.id.btnPersonList);
            Button btnBack=findViewById(R.id.btnSBack);
            Button btnDeletePerson=findViewById(R.id.btnSDeletePerson);
            Button btnAddAdmin=findViewById(R.id.btnAddAdmin);
            Button btnDeleteAdmin=findViewById(R.id.btnDeletAdmin);
            Button btnAddUser=findViewById(R.id.btnSaddUser);
            Button btnAddPerson=findViewById(R.id.btnSAddPerson);
            Button btnDeleteUser=findViewById(R.id.btnDeleteUser);
            Button btnCam=findViewById(R.id.cstCamButton);
            Button btnLock=findViewById(R.id.cstLockButton);
            Button btnMain=findViewById(R.id.cstMainButton);
            btnCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,CamLiveActivity.class);
                    startActivity(intent);
                }
            });
            btnMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FaceApplication.getGlobalVariable()){
                        Intent intent=new Intent(SettingsActivity.this,AdminScreenActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent=new Intent(SettingsActivity.this,UserScreenActivity.class);
                        startActivity(intent);
                    }
                }
            });
            btnInside.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,GetInsideActivity.class);
                    startActivity(intent);
                }
            });
            btnPersonList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,PersonListActivity.class);
                    startActivity(intent);
                }
            });
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            btnDeletePerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,DeletePersonActivity.class);
                    startActivity(intent);
                }
            });
            btnAddAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,AddManagerActivity.class);
                    startActivity(intent);
                }
            });
            btnDeleteAdmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,DeleteManagerActivity.class);
                    startActivity(intent);
                }
            });
            btnAddUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,AddUserActivity.class);
                    startActivity(intent);
                }
            });
            btnAddPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,AddPersonActivity.class);
                    startActivity(intent);
                }
            });
            btnDeleteUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SettingsActivity.this,DeleteUserActivity.class);
                    startActivity(intent);
                }
            });
            return insets;
        });
    }
}