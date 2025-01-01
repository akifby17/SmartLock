package com.example.faceapp;
import androidx.annotation.NonNull;
import okhttp3.*;
import okio.ByteString;
public class WebSocketClient {
    /*
     * WebSocketClient sınıfı, bir WebSocket bağlantısını yönetmek için tasarlanmıştır.
     *
     * Özellikler:
     * - WebSocket üzerinden sunucu ile çift yönlü iletişim sağlar.
     * - `start(String url)` metodu, belirtilen URL'ye bir WebSocket bağlantısı başlatır.
     * - `sendMessage(String message)` metodu, WebSocket üzerinden bir mesaj göndermek için kullanılır.
     *   Bağlantı kesilmişse, önce bağlantıyı yeniden başlatır.
     * - `stop()` metodu, mevcut WebSocket bağlantısını güvenli bir şekilde kapatır.
     *
     * Dahili Metotlar:
     * - `handleVideoData(byte[] videoData)`: Gelen video verilerini CamLiveActivity sınıfına iletir.
     *
     * WebSocketListener ile gelen olaylar:
     * - `onOpen`: Bağlantı başarıyla açıldığında isConnected durumu güncellenir.
     * - `onMessage`: Gelen mesajları DeletePersonActivity veya AddPersonActivity'ye yönlendirir.
     * - `onMessage(ByteString)`: Gelen binary video verisini işler.
     * - `onFailure`: Bağlantı hatası durumunda isConnected durumu güncellenir.
     * - `onClosed`: Bağlantı kapatıldığında isConnected durumu güncellenir.
     *
     * Bu sınıf, uygulamanın farklı bölümleri arasında gerçek zamanlı iletişimi kolaylaştırır ve WebSocket işlemlerini soyutlar.
     */

    private final OkHttpClient client;
    private String url;
    public boolean isConnected;
    private WebSocket webSocket;
    public WebSocketClient() {
        client = new OkHttpClient();
    }
    public void start(String url) {
        this.url=url;
        Request request = new Request.Builder().url(url).build();
      webSocket=client.newWebSocket(request, new WebSocketListener() {
          @Override
          public void onOpen(WebSocket webSocket, okhttp3.Response response) {
              isConnected=true;
          }
          @Override
          public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
              if (DeletePersonActivity.getInstance()!=null)
                    DeletePersonActivity.listenMessage(text);
              else if (AddPersonActivity.getInstance()!=null)
                  AddPersonActivity.listenMessage(text);
          }
          @Override
          public void onMessage(WebSocket webSocket, ByteString bytes) {
                handleVideoData(bytes.toByteArray());
          }
          @Override
          public void onFailure(WebSocket webSocket, Throwable t, Response response) {
              isConnected=false;
          }
          @Override
          public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
              super.onClosed(webSocket, code, reason);
              isConnected=false;
          }
      });
    }
    private void handleVideoData(byte[] videoData) {
            CamLiveActivity.updateVideoData(videoData);
    }
    public void sendMessage(String message) {
        if (!(isConnected)) {
            start(url);
            webSocket.send(message);
        }
       else {
            webSocket.send(message);
        }
    }
    public void stop() {
        if (webSocket != null && isConnected) {
            webSocket.close(1000, "Normal closure");
            isConnected = false;
        }
    }
}
