package com.example.faceapp;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
public class CamLiveActivity extends AppCompatActivity {
    private static SurfaceView surfaceView;
    private WebSocketClient videoClient;
    private  WebSocketClient commandClient;
    public static CamLiveActivity instance;
    int onChange=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*Bu aktivite rasberry üzerinden byte-codelar halinde gönderilen görüntü verisini alarak
        * surfaceview bileşeni üzerinde decode edip ekranda video gösteriyor.
        *
        * */
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cam_live);
        surfaceView = findViewById(R.id.surface_view);//surfaceView bileşeni
        TextView tbxCam=findViewById(R.id.tbxCam);
        Button btnMain=findViewById(R.id.cstMainButton1);
        Button btnExit=findViewById(R.id.cstLockButton1);
        Button btnCam=findViewById(R.id.cstCamButton1);
        Button btnBack=findViewById(R.id.btnCamBack);
        Button btnOpen=findViewById(R.id.btnOpenDoor);

        String[] items = {"Kamera İç", "Kamera Dış"};
        if (instance == null) {
            instance = this;
            if (commandClient == null || !commandClient.isConnected ) {
                //WebSocket Bağlantı kontrolü
                commandClient = new WebSocketClient();
                commandClient.start("ws://3.64.126.188:9999");
                videoClient = new WebSocketClient();
                videoClient.start("ws://3.64.126.188:8080");
            }
        }
        btnExit.setOnClickListener(new View.OnClickListener() {
            //çıkış butonu
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        commandClient.sendMessage("CAM_ON,0");//giriş kamerasını açma
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
            }
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }
        });
        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //canlı yayın esnasında kapıyı açmak istersek bu butona tıklıyoruz.Sunucuya OPEN_DOOR komutu gidiyor
                commandClient.sendMessage("OPEN_DOOR");
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Geri çıkarsak kamerayı kapatıp nesneleri yok ediyor.
                isConnected();
                commandClient.sendMessage("CAM_OFF");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                commandClient.sendMessage("CAM_OFF");
                commandClient.stop();
                commandClient=null;
                videoClient.stop();
                videoClient=null;
                instance=null;
                finish();
            }
        });
        btnCam.setOnClickListener(new View.OnClickListener() {
            //Kamera butonuna birkez daha basarsak kamera değişiyor
            @Override
            public void onClick(View view) {
                isConnected();
                if (onChange==0){
                    onChange=1;
                }
                else{
                    onChange=0;
                }
                commandClient.sendMessage("CAM_OFF");//burada önce varolan canlı yayını kapatıp bir süre beklememiz gerekiyor.
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                tbxCam.setText(items[onChange]);
                String is= String.valueOf(onChange);
                if (onChange==1){
                    is=String.valueOf(onChange+1);
                }
                commandClient.sendMessage("CAM_OFF");
                String message="CAM_ON,"+is;//sonra yeni kamera indexi ile canlı yayına geçiyoruz
                commandClient.sendMessage(message);
            }
        });
        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isConnected();
                if (FaceApplication.getGlobalVariable()){
                    //Burada yönetici modda giriş yapmışsak bu blok çalışıyor
                    Intent intent=new Intent(CamLiveActivity.this,AdminScreenActivity.class);
                    commandClient.sendMessage("CAM_OFF");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    commandClient.sendMessage("CAM_OFF");
                    commandClient.stop();
                    commandClient=null;
                    videoClient.stop();
                    videoClient=null;
                    instance=null;
                    startActivity(intent);
                }
                else{
                    //Burası ise kullanıcı modu için
                    isConnected();
                    Intent intent=new Intent(CamLiveActivity.this,UserScreenActivity.class);
                    commandClient.sendMessage("CAM_OFF");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    commandClient.sendMessage("CAM_OFF");
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    commandClient.sendMessage("CAM_OFF");
                    commandClient.stop();
                    commandClient=null;
                    videoClient.stop();
                    videoClient=null;
                    instance=null;
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        //uygulamayı kapattığımızda tüm nesneler yok ediliyor
        super.onDestroy();
        if(commandClient!=null && videoClient!=null){
            commandClient.sendMessage("CAM_OFF");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            commandClient.sendMessage("CAM_OFF");
            commandClient.stop();
            commandClient=null;
            videoClient.stop();
            videoClient=null;
        }
        instance=null;
    }
    public static void updateVideoData(byte[] videoData) {
        //Burası ise gelen byte-codeları decode ettiğimiz kısım bu sayede byte codeler video verisine dönüşüyor.
        Bitmap bitmap = BitmapFactory.decodeByteArray(videoData, 0, videoData.length);
        if (bitmap != null) {
            Canvas canvas = surfaceView.getHolder().lockCanvas();
            if (canvas != null) {
                int surfaceWidth = surfaceView.getWidth();
                int surfaceHeight = surfaceView.getHeight();
                Rect destRect = new Rect(0, 0, surfaceWidth, surfaceHeight);
                canvas.drawBitmap(bitmap, null, destRect, null);
                surfaceView.getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }
    private void isConnected (){
        //websocket bağlantı kontrolü için bir fonksiyon
        if (commandClient == null || !commandClient.isConnected ) {
            commandClient = new WebSocketClient();
            commandClient.start("ws://18.194.33.215:9999");
            videoClient = new WebSocketClient();
            videoClient.start("ws://18.194.33.215:8080");
        }
    }
}