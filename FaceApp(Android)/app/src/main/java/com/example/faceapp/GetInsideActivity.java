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

public class GetInsideActivity extends AppCompatActivity implements IDataManagement {
    private String apiKey="https://ap94b4jqr0.execute-api.eu-central-1.amazonaws.com/GetInside";
    private GetInsideAdapter adapter;
    private ArrayList<Persons> personsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    /*Bu aktivite giriş yapıp çıkış yapmayanları listelemek için oluşturuldu
    *
    * */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_inside);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Button btnCam=findViewById(R.id.cstCamButton);
            Button btnBack=findViewById(R.id.btnInsideBack);
            Button btnExit=findViewById(R.id.cstLockButton);
            Button btnMain=findViewById(R.id.cstMainButton);
            personsList = new ArrayList<>();
            LoadData();
            RecyclerView recyclerView = findViewById(R.id.recyclerViewInside);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new GetInsideAdapter(personsList);
            recyclerView.setAdapter(adapter);
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
            btnCam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(GetInsideActivity.this,CamLiveActivity.class);
                    startActivity(intent);
                }
            });
            btnMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FaceApplication.getGlobalVariable()){
                        Intent intent=new Intent(GetInsideActivity.this,AdminScreenActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent=new Intent(GetInsideActivity.this,UserScreenActivity.class);
                        startActivity(intent);
                    }
                }
            });
            return insets;
        });

    }
    public void LoadData(){
        //Listeyi doldurma fonksiyonu
        DatabaseHandler.getInside(apiKey, new ICallBack<List<Persons>>() {
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