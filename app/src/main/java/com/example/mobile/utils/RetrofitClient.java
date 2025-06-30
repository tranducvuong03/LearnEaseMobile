package com.example.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobile.api.CompareSpeakingAPI;
import com.example.mobile.api.LoginAPI;

import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.io.IOException;

/**
 * Lớp tiện ích để cấu hình Retrofit và OkHttpClient.
 * Cung cấp các instance ApiService cho cả API công khai (không cần token)
 * và API được bảo vệ (cần JWT token).
 *
 * KHÔNG NÊN DÙNG CẤU HÌNH SSL BỎ QUA TRONG MÔI TRƯỜNG PRODUCTION!
 */
public class RetrofitClient {

    private static LoginAPI apiService;
    private static CompareSpeakingAPI compareSpeakingAPI;

    private static Retrofit retrofit;

    /**
     * Trả về một instance của LoginAPI đã được cấu hình sẵn.
     * Phương thức này sẽ khởi tạo Retrofit và OkHttpClient
     * với cấu hình SSL bỏ qua và Interceptor để thêm token vào header.
     *
     * @param context Context của ứng dụng/activity để truy cập SharedPreferences.
     * @return Instance của LoginAPI.
     */
    public static synchronized LoginAPI getApiService(Context context) {
        if (apiService == null) {
            SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            String authToken = prefs.getString("auth_token", null);

            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                            @Override
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                };

                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                Interceptor authInterceptor = chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder();
                    if (authToken != null) {
                        builder.header("Authorization", "Bearer " + authToken);
                    }
                    return chain.proceed(builder.build());
                };

                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier((hostname, session) -> true)
                        .addInterceptor(loggingInterceptor)
                        .addInterceptor(authInterceptor)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

                retrofit = new Retrofit.Builder()
                        .baseUrl(LoginAPI.BASE_URL) // hoặc LoginAPI.BASE_URL nếu LoginAPI chứa URL
                        .client(client)
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                apiService = retrofit.create(LoginAPI.class);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create RetrofitClient: " + e.getMessage(), e);
            }
        }
        return apiService;
    }

    public static synchronized CompareSpeakingAPI getCompareApiService(Context context) {
        if (compareSpeakingAPI == null) {
            try {
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                            public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                            public X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0];
                            }
                        }
                };

                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient client = new OkHttpClient.Builder()
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier((hostname, session) -> true)
                        .addInterceptor(loggingInterceptor)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(CompareSpeakingAPI.BASE_URL) // bạn cần khai báo BASE_URL trong CompareSpeakingAPI
                        .client(client)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                compareSpeakingAPI = retrofit.create(CompareSpeakingAPI.class);

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to create CompareSpeakingAPI: " + e.getMessage(), e);
            }
        }

        return compareSpeakingAPI;
    }

}
