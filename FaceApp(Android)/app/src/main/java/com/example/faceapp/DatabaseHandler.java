package com.example.faceapp;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;
public  class DatabaseHandler implements ICallBack {
    /*API istekleri için bu sınıfı kullandık.Api ye istek atarken main thread den istek atıldığı zaman
    hata aldık çünkü ağ ile ilgili ilemler javada servis kullanarak yapılması gerekiyor.
    bunun için ExecutorService nesnesi ile api isteklerini işledik.
    * */
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private static final Gson gson = new Gson();
    public static void getLoginLogout(String apiUrl, ICallBack<List<Persons>> callback) {
        executorService.submit(() -> {
            //executor service nesnesi ile giriş-çıkış bilgilerini getirme metodu
            List<Persons> personsList = new ArrayList<>();
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");//istek metodumuz
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        content.append(line);
                    }
                    JSONArray jsonArray = new JSONArray(content.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        //JSON olarak gelen veriyi parse ederek nesne haline getiriyoruz
                        //oluşan nesneleri bir listeye atıyoruz
                        JSONObject personObject = jsonArray.getJSONObject(i);
                        int userId = personObject.getInt("ID");
                        String name = personObject.getString("Name");
                        String surName = personObject.getString("Surname");
                        String loginDate = personObject.getString("LoginDate");
                        String logoutDate = personObject.getString("LogoutDate");
                        Persons person = new Persons(userId, name, surName, loginDate,logoutDate);
                        personsList.add(person);
                    }
                }
                callback.onSuccess(personsList);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
    public static void getInside(String apiUrl, ICallBack<List<Persons>> callback) {
        //Giriş yapmış ve çıkış yapmamış personelleri getiren metod
        executorService.submit(() -> {
            List<Persons> personsList = new ArrayList<>();
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        content.append(line);
                    }
                    JSONArray jsonArray = new JSONArray(content.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject personObject = jsonArray.getJSONObject(i);
                        int userId = personObject.getInt("ID");
                        String name = personObject.getString("Name");
                        String surName = personObject.getString("Surname");
                        String date = personObject.getString("Date");
                        Persons person = new Persons(userId, name, surName, date);
                        personsList.add(person);
                    }
                }
                callback.onSuccess(personsList);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
    public static void checkLogin(String username, String password, String userType, ICallBack<Boolean> callback) {
        /*Bu metodta kullanıcı giriş ekranı için giriş kontrolü yapıyoruz
        * eğer kullanıcı yönetici modu ile giriş yapmaya çalışırsa ilgili API endpointi çağrılıyor
        * */
        executorService.submit(() -> {
            try {
                URL url = new URL("https://0k7lgzdne2.execute-api.eu-central-1.amazonaws.com/CheckLogin");
                //CheckLogin endpointi
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\", \"user_type\": \"%s\"}",
                        username, password, userType);
                connection.setDoOutput(true);
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        System.out.println(response.toString());
                        boolean success = response.toString().contains("User login successful") || response.toString().contains("Admin login successful") ;
                        //eğer dönen cevap succesfull içeriyorsa giriş başarılı
                        callback.onSuccess(success);
                    }
                } else {
                    callback.onFailure(new IOException("Error: " + responseCode));
                }

            } catch (IOException e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
    public static void getPersons(String apiUrl, ICallBack<List<Persons>> callback) {
        /*Kayıtlı personelleri getiren metod.
        *
        * */
        executorService.submit(() -> {
            List<Persons> personsList = new ArrayList<>();
            try {
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        content.append(line);
                    }
                   JSONArray jsonArray=new JSONArray(content.toString());
                    for (int i = 0; i < jsonArray.length() ; i++) {
                        JSONObject personObject=jsonArray.getJSONObject(i);
                        int userId = personObject.getInt("ID");
                        String name = personObject.getString("Name");
                        String surName = personObject.getString("Surname");
                        String Date=personObject.getString("Date");//Kayıt Tarihi
                        Persons person = new Persons(userId, name, surName,Date);
                        personsList.add(person);
                    }
                }
                callback.onSuccess(personsList);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onFailure(e);
            }
        });
    }
    public static void isExist(int personId, URL url,ICallBack<Boolean> callback) {
        /*Personel Kayıtlımı kontrol eden metod
        *
        * */
        executorService.submit(() -> {
            boolean exists = false;
            try {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                String jsonInputString = String.format("{\"ID\": %d}", personId);
                conn.setDoOutput(true);
                conn.getOutputStream().write(jsonInputString.getBytes("UTF-8"));
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    content.append(line);
                }
                in.close();
                String response = content.toString();
                if (response.contains("\"exists\": true")) {
                    exists = true;
                } else {
                    exists = false;
                }
                callback.onSuccess(exists);
            } catch (IOException e) {
                Log.e("DatabaseHandler", "Error during API request", e);
                callback.onFailure(e);
            }
        });
    }
    public static void addEntity(String url, String jsonInputString, ICallBack<String> callback)
    /*Bu metod gönderilen API url ne göre veritabanına veri ekliyor.
    *
    * */
    {
        executorService.submit(() -> {
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = conn.getResponseCode();
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        responseCode >= 200 && responseCode < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream(), "UTF-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                if (responseCode >= 200 && responseCode < 300) {
                    callback.onSuccess(response.toString());
                } else {
                    callback.onFailure(new Exception("Error: HTTP " + responseCode + ", Response: " + response));
                }

            } catch (Exception e) {
                callback.onFailure(e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }
    public static void deleteEntity(String url, String jsonInputString, ICallBack<String> callback){
        /*Bu metod ise gönderilen api endpointine göre veri tabanından veri siliyor.
        *
        * */
        executorService.submit(()->{
            HttpURLConnection conn = null;
            try {
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes("UTF-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = conn.getResponseCode();
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(
                        responseCode >= 200 && responseCode < 300
                                ? conn.getInputStream()
                                : conn.getErrorStream(), "UTF-8"))) {
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                }
                if (responseCode >= 200 && responseCode < 300) {
                    callback.onSuccess(response.toString());
                } else {
                    callback.onFailure(new Exception("Error: HTTP " + responseCode + ", Response: " + response));
                }

            } catch (Exception e) {
                callback.onFailure(e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        });
    }

    @Override
    public void onSuccess(Object result) {
    }

    @Override
    public void onFailure(Exception e) {

    }
}
