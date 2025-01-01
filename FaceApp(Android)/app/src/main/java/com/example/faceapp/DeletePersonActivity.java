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
import java.net.MalformedURLException;
import java.net.URL;
public class DeletePersonActivity extends AppCompatActivity {
    private String deletedId;
    private int deletedIdInt;
    URL apiUrl;
    private WebSocketClient commandClient;
    private static DeletePersonActivity instance;
    /*Bu aktivite kayıtlı personelleri silmek için rasberry pi ye komut gönderiyor.
    * Öncelikle kullanıcı numarası isExist() metodu ile kontrol ediliyor.
    * eğer kullanıcı mevcutsa rasberry e silme komutu ile beraber id değeri gönderilierek
    * silme işlemi yaplıyor.Ayrıca işlem sonucu ekranda TOAST mesajı olarak gösteriliyor.
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_delete_person);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            if (instance == null) {
                instance = this;
                if (commandClient == null || !commandClient.isConnected) {
                    commandClient = new WebSocketClient();
                    commandClient.start("ws://3.64.126.188:9999");
                }
            }
            EditText tbxDelete=findViewById(R.id.tvDeleteText);
            Button btnDelete=findViewById(R.id.btnDelete);
            Button btnBack=findViewById(R.id.btnDltBack);
            Button btnCancel=findViewById(R.id.btnDltCancel);
            try {
                apiUrl=new URL("https://a8577f05gc.execute-api.eu-central-1.amazonaws.com/IsExist");
                //api endpointi
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletedId=tbxDelete.getText().toString().trim();
                    if (deletedId.isEmpty())
                        Toast.makeText(DeletePersonActivity.this,"Lütfen Tüm Kutucukları Doldurun",Toast.LENGTH_SHORT).show();
                    else {
                        deletedIdInt=Integer.parseInt(deletedId);
                            DatabaseHandler.isExist(deletedIdInt, apiUrl, new ICallBack<Boolean>() {
                                @Override
                                public void onSuccess(Boolean result) {
                                    if (result){
                                        String message="DELETE,"+ deletedIdInt;
                                        commandClient.sendMessage(message);
                                    }
                                    else{
                                        runOnUiThread(() ->
                                                Toast.makeText(DeletePersonActivity.this, "Geçersiz Personel No!", Toast.LENGTH_SHORT).show()
                                        );
                                    }
                                }
                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
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
                    finish();
                }
            });
            return insets;
        });
    }
    public static DeletePersonActivity getInstance() {
        return instance;
    }
    public static void listenMessage(String message) {
        //rasberry pi den gelen yanıtı dinlemek için
        if (getInstance() != null) {
            getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getInstance(), message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        commandClient.stop();
        commandClient=null;
        instance=null;
    }
}