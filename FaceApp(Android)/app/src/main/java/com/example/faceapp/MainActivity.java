package com.example.faceapp;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
public class MainActivity extends AppCompatActivity {
private String loginType=null;
private String userName;
private String password;
public static boolean isAdmin=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            Button btnAdminLogin=findViewById(R.id.btnAdminLogin);
            Button btnUserLogin=findViewById(R.id.btnUserLogin);
            Button btnShowPassword=findViewById(R.id.btnShowPassword);
            EditText tbxUserName=findViewById(R.id.tbxUserName);
            EditText tbxPassword=findViewById(R.id.tbxPassword);
            ImageView imgPassword=findViewById(R.id.imageViewPassword);
            final boolean[] isPasswordVisible = {false};
            btnUserLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginType="user";
                    userName=tbxUserName.getText().toString().trim();
                    password=tbxPassword.getText().toString().trim();
                    if(userName.isEmpty()||password.isEmpty()){
                        Toast.makeText(MainActivity.this, "Lütfen Tüm Boşlukları Doldurunuz!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    DatabaseHandler.checkLogin(userName, password, loginType, new ICallBack<Boolean>() {
                        @Override
                        public void onSuccess(Boolean result) {
                            if(result){
                                Intent intent=new Intent(MainActivity.this, UserScreenActivity.class);
                                FaceApplication.setGlobalVariable(false);
                                startActivity(intent);}
                        }
                        @Override
                        public void onFailure(Exception e) {
                            runOnUiThread(() ->
                                    Toast.makeText(MainActivity.this, "Kullanici Adi veya Şifre Hatali", Toast.LENGTH_SHORT).show()
                            );
                        }
                    });

                }
            });
            btnAdminLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loginType="admin";
                    userName=tbxUserName.getText().toString().trim();
                    password=tbxPassword.getText().toString().trim();
                    if(userName.isEmpty()||password.isEmpty()){
                        Toast.makeText(MainActivity.this, "Lütfen Tüm Boşlukları Doldurunuz!!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                DatabaseHandler.checkLogin(userName, password, loginType, new ICallBack<Boolean>() {
                    @Override
                    public void onSuccess(Boolean result) {
                        if(result){
                            Intent intent=new Intent(MainActivity.this, AdminScreenActivity.class);
                            FaceApplication.setGlobalVariable(true);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() ->
                                Toast.makeText(MainActivity.this, "Kullanici Adi Veya Şifre Hatali", Toast.LENGTH_SHORT).show()
                        );
                    }
                });
                }
            });
            btnShowPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isPasswordVisible[0]) {
                        tbxPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imgPassword.setImageResource(R.drawable.ic_eye_close);
                    }
                    else{
                       tbxPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        imgPassword.setImageResource(R.drawable.ic_eye_open);
                    }
                    isPasswordVisible[0] = !isPasswordVisible[0];
                    tbxPassword.setSelection(tbxPassword.getText().length());
                }
            });
            return insets;
        });
    }
}