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
import com.google.gson.JsonObject;
public class AddUserActivity extends AppCompatActivity {
    /*
    * Bu sınıf AddManager sınıfı ile aynı mantıkta çalışıyor sadece user modu için kullanıcı ekliyor
    * */
    String userName;String userSurname;
    String userPassword;String userPasswordAgain;
    String userUserName; String jsonString;
    String userApiUrl = "https://evkmnfdh00.execute-api.eu-central-1.amazonaws.com/prod/user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            EditText tbxUserName=findViewById(R.id.tvUserrName);
            EditText tbxUserSurname=findViewById(R.id.tvxUserSurname);
            EditText tbxUserPassword=findViewById(R.id.tvxUserPassword);
            EditText tbxUserPasswordAgain=findViewById(R.id.tvxUserPasswordAgain);
            EditText tbxUserUserName=findViewById(R.id.tvxUserUserName);
            Button addUser=findViewById(R.id.btnAddUser);
            Button addUserCancel=findViewById(R.id.btnAddUserCancel);
            Button addUserBack =findViewById(R.id.btnAddUserBack);
            addUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userName=tbxUserName.getText().toString();
                    userSurname=tbxUserSurname.getText().toString();
                    userPassword=tbxUserPassword.getText().toString();
                    userPasswordAgain=tbxUserPasswordAgain.getText().toString();
                    userUserName=tbxUserUserName.getText().toString();
                    if(userName.isEmpty() || userSurname.isEmpty()|| userPassword.isEmpty()|| userUserName.isEmpty()){
                        Toast.makeText(AddUserActivity.this, "Lütfen Tüm Bölümleri doldurun", Toast.LENGTH_SHORT).show();
                    }
                    else if(!(userPassword.equals(userPasswordAgain))){
                        Toast.makeText(AddUserActivity.this, "Parolalar Eşleşmiyor Tekrar Deneyin!!!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        JsonObject userJson=new JsonObject();
                        userJson.addProperty("UserName",userName);
                        userJson.addProperty("UserSurname",userSurname);
                        userJson.addProperty("UserPassword",userPassword);
                        userJson.addProperty("UserLogin",userUserName);
                        jsonString=userJson.toString();
                        DatabaseHandler.addEntity(userApiUrl, jsonString, new ICallBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                runOnUiThread(() ->
                                        Toast.makeText(AddUserActivity.this, result, Toast.LENGTH_SHORT).show()
                                );
                            }

                            @Override
                            public void onFailure(Exception e) {
                                    String ex=e.toString();
                                runOnUiThread(() ->
                                        Toast.makeText(AddUserActivity.this, ex, Toast.LENGTH_SHORT).show()
                                );
                            }
                        });
                    }
                }
            });
            addUserBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            addUserCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            return insets;
        });
    }
}