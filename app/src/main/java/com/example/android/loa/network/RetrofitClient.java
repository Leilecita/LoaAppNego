package com.example.android.loa.network;

import com.example.android.loa.LoaApp;
import com.example.android.loa.data.SessionPrefs;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;
    private static Retrofit sessionRetrofit = null;

    public static Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getSessionClient(String baseUrl) {
        if (sessionRetrofit==null) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request request = chain.request();

                    String session = SessionPrefs.get(LoaApp.getAppContext()).getToken();
                    if(session!= null) {
                        Request.Builder requestBuilder = request.newBuilder()
                                .header("Session", session); // <-- this is the important line

                        request = requestBuilder.build();
                    }
                    return chain.proceed(request);
                }
            });
            sessionRetrofit = new Retrofit.Builder()
                    .client(httpClient.build())
                    .baseUrl(baseUrl)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return sessionRetrofit;
    }
}
