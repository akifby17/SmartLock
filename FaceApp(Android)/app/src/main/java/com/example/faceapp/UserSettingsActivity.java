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

public class UserSettingsActivity extends AppCompatActivity {
    /*Bu aktivite kullanıcı rolünde oturum açan kişinin ayar ekranı
    * Kişi sadece içerideki personelleri ve personel listesini görüntüleyebiliyor.
    * */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Button btnCam=findViewById(R.id.cstCamButton);
            Button btnBack=findViewById(R.id.btnUserBack1);
            Button btnExit=findViewById(R.id.cstLockButton);
            Button btnMain=findViewById(R.id.cstMainButton);
            Button getInside=findViewById(R.id.btnUserInside);
            Button getPersonList=findViewById(R.id.btnUserPersonList);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finishAffinity();
                }
            });
            btnMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FaceApplication.getGlobalVariable()){
                        Intent intent=new Intent(UserSettingsActivity.this,AdminScreenActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent=new Intent(UserSettingsActivity.this,UserScreenActivity.class);
                        startActivity(intent);
                    }
                }
            });
            btnCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(UserSettingsActivity.this,CamLiveActivity.class);
                    startActivity(intent);
                }
            });
            getInside.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(UserSettingsActivity.this,GetInsideActivity.class);
                    startActivity(intent);
                }
            });
            getPersonList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(UserSettingsActivity.this,PersonListActivity.class);
                    startActivity(intent);
                }
            });
            return insets;
        });
    }
}