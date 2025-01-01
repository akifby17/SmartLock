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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserScreenActivity extends AppCompatActivity implements IDataManagement {
    /*
    * Giriş yapan kişi kullanıcı rolünde oturum açarsa bu aktivite başlıyor ve bağzı özellikler kısıtlanıyor.
    *
    * */
    private PersonsAdapter adapter;
    private ArrayList<Persons> personsList;
    private String apiUrl="https://fndbr4lqf1.execute-api.eu-central-1.amazonaws.com/GetLoginLogout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user__screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Button userSettings=findViewById(R.id.btnUserSettings);
            Button userBack=findViewById(R.id.btnUserBack);
            Button btnCam=findViewById(R.id.cstCamButton);
            Button btnMain=findViewById(R.id.cstMainButton);
            Button btnExit=findViewById(R.id.cstLockButton);
            personsList = new ArrayList<>();
            LoadData();
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new PersonsAdapter(personsList);
            recyclerView.setAdapter(adapter);
            btnCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(UserScreenActivity.this,CamLiveActivity.class);
                    startActivity(intent);
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
                    LoadData();
                }
            });
            userBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            userSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(UserScreenActivity.this,UserSettingsActivity.class);
                    startActivity(intent);
                }
            });
            return insets;
        });
    }
    public void LoadData(){
        DatabaseHandler.getLoginLogout(apiUrl, new ICallBack<List<Persons>>() {
            @Override
            public void onSuccess(List<Persons> result) {
                personsList.clear();
                personsList.addAll(result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        adapter.notifyDataSetChanged();
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
            }
        });
    }
}