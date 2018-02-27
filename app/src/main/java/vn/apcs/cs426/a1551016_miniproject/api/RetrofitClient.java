package vn.apcs.cs426.a1551016_miniproject.api;

import android.util.Log;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by khoanguyen on 6/20/17.
 */

public class RetrofitClient {
    private Retrofit retrofit;
    private String API_KEY;
    private String BASE_URL;

    public RetrofitClient(String apiKey, String baseUrl) {

        API_KEY = apiKey;
        BASE_URL = baseUrl;
        //create an interceptor to make sure that API KEY will always be injected
        Interceptor interceptor = new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {

                Request originalRequest = chain.request();

                HttpUrl originalUrl = originalRequest.url();

                HttpUrl newUrl = originalUrl.newBuilder()
                        .addQueryParameter("key", API_KEY)
                        .build();

                Request newRequest = originalRequest.newBuilder()
                        .url(newUrl)
                        .build();

                return chain.proceed(newRequest);
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    }

    public GooglePlacesService getPlaceService() {
        return retrofit.create(GooglePlacesService.class);
    }

    public GoogleDirectionService getDirectionService() {
        return retrofit.create(GoogleDirectionService.class);
    }
}
