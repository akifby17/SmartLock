package com.example.faceapp;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class AddPersonActivity extends AppCompatActivity {
    private String name;
    private String surName;
    private String message;
    private  WebSocketClient commandClient;
    private static AddPersonActivity instance;
    public static AddPersonActivity getInstance() {
        return instance;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*
        * Bu aktivite personel eklemek için WebSocketClient sınıfını kullanarak eklecek personelin
        * bilgilerini rasberry pi ye gönderiyor.
        * */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_person);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            EditText tbxName=findViewById(R.id.tvPersonName);
            EditText tbxSurName=findViewById(R.id.tvPersonSurname);
            Button btnAdd=findViewById(R.id.btnAdd);
            Button btnBack=findViewById(R.id.btnBack);
            Button btnCancel=findViewById(R.id.btnCancel);
            if (instance == null) {
                instance = this;
                if (commandClient == null || !commandClient.isConnected)
                //WebSocket nesnesinin kontrolü
                {
                    commandClient = new WebSocketClient();
                    commandClient.start("ws://3.64.126.188:9999");
                }
            }
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    name=tbxName.getText().toString().trim();
                    surName=tbxSurName.getText().toString().trim();
                    if (name.isEmpty() || surName.isEmpty()){
                        Toast.makeText(AddPersonActivity.this,"Lütfen Tüm Kutucukları Doldurun",Toast.LENGTH_SHORT).show();
                    }
                    else if (!(name.matches(("[a-zA-Z]+")) && surName.matches("[a-zA-Z]+"))){
                        //sadece harf girişi kontrolü
                        Toast.makeText(AddPersonActivity.this,"Lütfen sadece harf girişi yapın",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        //Burada gelen isim soyisim verisini sadece baş harfleri büyük olacak şekilde değiştiriyoruz
                        name=name.substring(0, 1).toUpperCase()+name.substring(1).toLowerCase();
                        surName=surName.substring(0, 1).toUpperCase()+surName.substring(1).toLowerCase();
                        message="REGISTER,"+name+","+surName;
                        commandClient.sendMessage(message);
                    }
                }
            });
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tbxName.setText(null);
                    tbxSurName.setText(null);
                    finish();
                }
            });
            return insets;
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        commandClient.stop();
        commandClient=null;
        instance=null;
    }
    public static void listenMessage(String message) {
        //Rasberry den gelen mesajı dinlemek için
        if (getInstance() != null) {
            System.out.println(message);
            getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getInstance(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}