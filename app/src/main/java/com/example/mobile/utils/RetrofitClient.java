package com.example.mobile.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobile.api.ApiService;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Lớp tiện ích để cấu hình Retrofit và OkHttpClient.
 * Bao gồm:
 * 1. Cấu hình để bỏ qua xác minh chứng chỉ SSL (KHÔNG NÊN DÙNG TRONG MÔI TRƯỜNG PRODUCTION!).
 * 2. Thêm một Interceptor để tự động đính kèm JWT token vào header Authorization.
 * 3. Thêm HttpLoggingInterceptor để ghi log các yêu cầu và phản hồi HTTP.
 */
public class RetrofitClient {

    private static Retrofit retrofit = null;
    private static ApiService apiService = null; // Để giữ một instance duy nhất của ApiService

    /**
     * Trả về một instance của ApiService đã được cấu hình sẵn.
     * Phương thức này sẽ khởi tạo Retrofit và OkHttpClient
     * với cấu hình SSL bỏ qua và Interceptor để thêm token vào header.
     *
     * @param context Context của ứng dụng/activity để truy cập SharedPreferences.
     * @return Instance của ApiService.
     */
    public static synchronized ApiService getApiService(Context context) {
        if (apiService == null) {
            // Lấy token từ SharedPreferences
            // "MyAppPrefs" phải khớp với tên bạn dùng để lưu token trong LoginActivity
            SharedPreferences prefs = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
            String authToken = prefs.getString("auth_token", null);

            try {
                // Tạo một TrustManager chấp nhận mọi thứ (KHÔNG kiểm tra chứng chỉ nào).
                final TrustManager[] trustAllCerts = new TrustManager[]{
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                                // Không làm gì cả
                            }

                            @Override
                            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                                // Không làm gì cả
                            }

                            @Override
                            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                                return new X509Certificate[0]; // Trả về mảng rỗng
                            }
                        }
                };

                // Khởi tạo SSLContext với TrustManager chấp nhận mọi thứ
                final SSLContext sslContext = SSLContext.getInstance("SSL");
                sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

                // Tạo SSLSocketFactory từ SSLContext
                final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

                // Cấu hình HttpLoggingInterceptor để ghi log các request/response
                HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Đặt mức log body để xem chi tiết

                // Tạo Interceptor để thêm Authorization header
                Interceptor authInterceptor = chain -> {
                    Request originalRequest = chain.request();
                    Request.Builder requestBuilder = originalRequest.newBuilder();

                    if (authToken != null) {
                        // Nếu có token, thêm vào header "Authorization"
                        // Định dạng chuẩn là "Bearer <token>"
                        requestBuilder.header("Authorization", "Bearer " + authToken);
                    }

                    // Xây dựng request mới và tiếp tục chuỗi
                    Request newRequest = requestBuilder.build();
                    return chain.proceed(newRequest);
                };

                // Xây dựng OkHttpClient
                OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
                okHttpClientBuilder.addInterceptor(loggingInterceptor); // Thêm logging interceptor
                okHttpClientBuilder.addInterceptor(authInterceptor);    // Thêm auth interceptor
                okHttpClientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

                // Thêm HostnameVerifier bỏ qua xác minh Hostname
                // CỰC KỲ KHÔNG AN TOÀN TRONG MÔI TRƯỜNG PRODUCTION.
                okHttpClientBuilder.hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true; // Luôn trả về true để chấp nhận mọi hostname
                    }
                });

                OkHttpClient okHttpClient = okHttpClientBuilder.build();

                // Khởi tạo Retrofit instance
                retrofit = new Retrofit.Builder()
                        .baseUrl(ApiService.BASE_URL) // Lấy BASE_URL từ interface ApiService
                        .addConverterFactory(GsonConverterFactory.create()) // Sử dụng Gson để chuyển đổi JSON
                        .client(okHttpClient) // Gán OkHttpClient đã cấu hình
                        .build();

                apiService = retrofit.create(ApiService.class);

            } catch (Exception e) {
                // In stack trace để xem lỗi nếu có
                e.printStackTrace();
                // Ném RuntimeException để làm crash ứng dụng và dễ dàng debug hơn
                throw new RuntimeException("Failed to initialize RetrofitClient with SSL bypass: " + e.getMessage(), e);
            }
        }
        return apiService;
    }
}
