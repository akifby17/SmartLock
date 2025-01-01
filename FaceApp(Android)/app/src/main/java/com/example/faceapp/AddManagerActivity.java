package com.example.faceapp;
import android.content.Context;
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
import com.google.gson.JsonObject;
public class AddManagerActivity extends AppCompatActivity {
    /*
    * Bu aktivite yönetici rolü için veri tabanındaki ilgili tabloya (Manager) veri ekliyor bu sayede
    * eklenen yönetici kullanıcı adı ve şifre ile sisteme giriş yapıyor
    * */
    String managerName;String managerSurname;
    String managerUserName;String managerPassword;
    String managerPasswordAgain;
    String managerApiUrl = "https://evkmnfdh00.execute-api.eu-central-1.amazonaws.com/prod/manager";
    //yönetici ekleme api endpointi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_manager);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            EditText tbxManagerName=findViewById(R.id.tbxName);
            EditText tbxManagerSurName=findViewById(R.id.tbxSurname);
            EditText tbxManagerUserName=findViewById(R.id.tbxManagerUserName);
            EditText tbxManagerPassword=findViewById(R.id.tbxManagerPassword);
            EditText tbxManagerPasswordAgain=findViewById(R.id.tbxPasswordAgain);
            Button addManager=findViewById(R.id.addManagerButton);
            Button btnCancel=findViewById(R.id.addManagerCancel);
            Button btnBack=findViewById(R.id.btnAddManagerBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            addManager.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //EditText deki değerleri değişkenlere atama
                    managerName=tbxManagerName.getText().toString();
                    managerSurname=tbxManagerSurName.getText().toString();
                    managerUserName=tbxManagerUserName.getText().toString();
                    managerPassword=tbxManagerPassword.getText().toString();
                    managerPasswordAgain=tbxManagerPasswordAgain.getText().toString();
                    //tüm kutucukları doldurmamız gerekiyor
                    if(managerName.isEmpty() || managerSurname.isEmpty()|| managerPassword.isEmpty()|| managerUserName.isEmpty()){
                        Toast.makeText(AddManagerActivity.this, "Lütfen Tüm Bölümleri doldurun", Toast.LENGTH_SHORT).show();
                    }
                    else if(!(managerPassword.equals(managerPasswordAgain))){
                        //parolaların aynı olması kontrolü
                        Toast.makeText(AddManagerActivity.this, "Parolalar Eşleşmiyor Tekrar Deneyin!!!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        /*
                        * Editexteki değerleri JSON objesine dnüştürme işlemi
                        * */
                        JsonObject managerJson = new JsonObject();
                        managerJson.addProperty("ManagerName", managerName);
                        managerJson.addProperty("ManagerSurname", managerSurname);
                        managerJson.addProperty("ManagerLogin", managerUserName);
                        managerJson.addProperty("Password", managerPassword);
                        String jsonString = managerJson.toString();
                        //addEntity metodu ile api ye istek atıyoruz
                        DatabaseHandler.addEntity(managerApiUrl, jsonString, new ICallBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                runOnUiThread(() ->
                                        //api isteği başarılı olursa ekrana mesaj yazıyor
                                        Toast.makeText(AddManagerActivity.this, result, Toast.LENGTH_SHORT).show()
                                );
                            }
                            @Override
                            public void onFailure(Exception e) {
                                String ex=e.toString();
                                runOnUiThread(() ->
                                        //api isteği başarısızlık durumu
                                        Toast.makeText((Context) AddManagerActivity.this,  ex, Toast.LENGTH_SHORT).show()
                                );
                            }
                        });
                    }
                }
            });
            return insets;
        });
    }
}