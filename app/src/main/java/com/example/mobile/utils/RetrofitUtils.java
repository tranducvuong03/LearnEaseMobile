package com.example.mobile.utils; // Đổi tên package nếu bạn đặt ở thư mục khác

import com.example.mobile.api.ApiService; // Cần import ApiService để lấy BASE_URL

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class RetrofitUtils {

    // Phương thức này tạo Retrofit instance bỏ qua mọi xác minh SSL và Hostname
    public static Retrofit getUnsafeRetrofitInstance() {
        try {
            // Tạo một TrustManager chấp nhận mọi thứ (không kiểm tra chứng chỉ nào)
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            // Không làm gì cả, chấp nhận mọi client
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                            // Không làm gì cả, chấp nhận mọi server
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

            // Thêm Logging Interceptor để xem log mạng
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.addInterceptor(logging); // Thêm logging
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);

            // Thêm HostnameVerifier bỏ qua xác minh Hostname
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true; // Luôn trả về true để chấp nhận mọi hostname
                }
            });

            OkHttpClient okHttpClient = builder.build();

            return new Retrofit.Builder()
                    .baseUrl(ApiService.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();

        } catch (Exception e) {
            // In stack trace để xem lỗi nếu có
            e.printStackTrace();
            throw new RuntimeException(e); // Ném RuntimeException để dừng ứng dụng và dễ debug
        }
    }
}