package com.linkcircle.fj.agorasignal.http;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * HttpClient
 *
 * @author lifuhai@linkcircle.cn
 * @date 2018/5/15 14:04
 */
public class HttpClient {
    private final static String BASE_URL = "http://sfapp.linkcircle.net/";
    private Retrofit mRetrofit;
    private static HttpClient sInstance;

    public static HttpClient getInstance() {
        if (null == sInstance) {
            synchronized (HttpClient.class) {
                sInstance = new HttpClient();
            }
        }

        return sInstance;
    }

    private HttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public LoginApi createLoginApi() {
        return mRetrofit.create(LoginApi.class);
    }
}
