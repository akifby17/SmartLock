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

public class DeleteUserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Bu aktivite kullanıcı rolündeki kişileri veri tabanından siliyor.
        *
        * */
        String apiKey="https://yi6vb9dkdd.execute-api.eu-central-1.amazonaws.com/prod/delete_user";
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            EditText etvUserName=findViewById(R.id.deleteUserName);
            Button btnDelete=findViewById(R.id.deleteUserBtn);
            Button btnCancel=findViewById(R.id.deleteUserCancel);
            Button btnBack=findViewById(R.id.btnDeleteUserBack);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String userName=etvUserName.getText().toString();
                    if(userName.isEmpty())
                        Toast.makeText(DeleteUserActivity.this,"Lütfen Değer Giriniz!",Toast.LENGTH_SHORT).show();
                    else{
                        JsonObject deleteJson=new JsonObject();
                        deleteJson.addProperty("UserLogin",userName);
                        String json=deleteJson.toString();
                        DatabaseHandler.deleteEntity(apiKey, json, new ICallBack<String>() {
                            @Override
                            public void onSuccess(String result) {
                                runOnUiThread(() ->
                                        Toast.makeText(DeleteUserActivity.this, result, Toast.LENGTH_SHORT).show()
                                );
                            }
                            @Override
                            public void onFailure(Exception e) {
                                String ex=e.toString();
                                runOnUiThread(() ->
                                        Toast.makeText(DeleteUserActivity.this, ex, Toast.LENGTH_SHORT).show()
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