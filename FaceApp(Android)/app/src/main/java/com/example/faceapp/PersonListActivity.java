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
/*
* Bu aktivite Kayıtlı personelleri listelemek için oluşturuldu.
* Diğer Aktiviteler gibi kendine ait olan CustomAdapter sınıfını kullanarak verileri getiriyor.
*
* */
public class PersonListActivity extends AppCompatActivity implements IDataManagement {
    private String apiKey="https://be8n39u17b.execute-api.eu-central-1.amazonaws.com/GetPersons";
    private GetPersonsAdapter adapter;
    private ArrayList<Persons> personsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_person_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Button btnBack=findViewById(R.id.btnListBack);
            Button btnCam=findViewById(R.id.cstCamButton);
            Button btnExit=findViewById(R.id.cstLockButton);
            Button btnMain=findViewById(R.id.cstMainButton);
            personsList = new ArrayList<>();
            LoadData();
            RecyclerView recyclerView = findViewById(R.id.recyclerViewPerson);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new GetPersonsAdapter(personsList);
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
                    Intent intent=new Intent(PersonListActivity.this,CamLiveActivity.class);
                    startActivity(intent);
                }
            });
            btnMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (FaceApplication.getGlobalVariable()){
                        Intent intent=new Intent(PersonListActivity.this,AdminScreenActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Intent intent=new Intent(PersonListActivity.this,UserScreenActivity.class);
                        startActivity(intent);
                    }
                }
            });
            return insets;
        });
    }
    public void LoadData(){
        DatabaseHandler.getPersons(apiKey, new ICallBack<List<Persons>>() {
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