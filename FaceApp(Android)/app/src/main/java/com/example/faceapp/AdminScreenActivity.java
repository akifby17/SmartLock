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
public class AdminScreenActivity extends AppCompatActivity implements IDataManagement {
    /*Bu sınıf uygulamanın ana ekranı IDataManagemnet interface ni kullanarak veri tabanındaki giriş-çıkış
    bilgilerini CustomAdapter kullanarak verileri ekrana uygun bir şekilde getiriyor.
    *
    * */
    private PersonsAdapter adapter;//Custom Adapter Nesnemiz
    private ArrayList<Persons> personsList;//Veri tabanından gelecek personel bilgilerini tutacak liste
    private String apiUrl="https://fndbr4lqf1.execute-api.eu-central-1.amazonaws.com/GetLoginLogout";
    //Api endpointi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Button camButton=findViewById(R.id.cstCamButton);
            Button btnSettings=findViewById(R.id.btnSettings);
            Button btnMain=findViewById(R.id.cstMainButton);
            Button btnBack=findViewById(R.id.btnAdminBack);
            Button btnExit=findViewById(R.id.cstLockButton);
            btnExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   finishAffinity();
                }
            });
            btnMain.setOnClickListener(new View.OnClickListener()
            //Bu butona 2 kez tıklanınca veri tabanındaki veriler tekrar yükleniyor
            {
                @Override
                public void onClick(View view) {
                    LoadData();
                }
            });
            btnSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(AdminScreenActivity.this,SettingsActivity.class);
                    startActivity(intent);
                }
            });
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            personsList = new ArrayList<>();
            LoadData();
            RecyclerView recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new PersonsAdapter(personsList);
            recyclerView.setAdapter(adapter);
            camButton.setOnClickListener(new View.OnClickListener() {
                //Canlı yayına geçme butonu
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(AdminScreenActivity.this,CamLiveActivity.class);
                    startActivity(intent);
                }
            });
            return insets;
        });
    }
    public void LoadData(){
        /*Bu fonksiyon çağrıldığında veri tabanındaki bilgileri önce JSON olarak getiriyor*
        Ardından bu JSON tipindeki verileri Persons nesnesine dönüştürerek bir listeye atıyor
         */
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
