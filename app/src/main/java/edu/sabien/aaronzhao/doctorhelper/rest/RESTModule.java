package edu.sabien.aaronzhao.doctorhelper.rest;

import android.app.Application;
import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.AppModule;
import edu.sabien.aaronzhao.doctorhelper.GlassApp;
import edu.sabien.aaronzhao.doctorhelper.helper.AuthModule;
import edu.sabien.aaronzhao.doctorhelper.scopes.AuthScope;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by AaronZhao on 4/19/16.
 */
@Module
public class RESTModule {

    private GlassApp application;
    public interface RESTInterface {
        @POST("/RESTConnector/RequestXFIPAStream")
        Call<String> getDevices(@Body String jsonString);

        @POST("/RESTConnector/RequestXFIPAStream")
        Call<String> getDeviceMeasure(@Body String jsonString);
    }

    public RESTModule(GlassApp app){
        application = app;
    }

    @Provides
    @AuthScope
    @Inject
    Cache provideOkHttpCache() {
        int cacheSize = 10 * 1024 * 1024; // 10 MiB
        Cache cache = new Cache(application.getCacheDir(), cacheSize);
        return cache;
    }

    @Provides
    @Named("cached")
    @AuthScope
    @Inject
    OkHttpClient provideOkHttpClient(Cache cache) {
        return new OkHttpClient.Builder()
                .cache(cache)
                .build();
    }


    @Provides
    @Named("intercepted")
    @AuthScope
    OkHttpClient provideOkHttpClientIntercept(){
        return new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Interceptor.Chain chain) throws IOException {
                                Request request = chain.request();
                                Log.d("REST intercept!", bodyToString(request.body()));
                                return chain.proceed(request);
                            }
                        }).build();
    }

    @Provides
    @Named("cached")
    @AuthScope
    @Inject
    Retrofit provideRetrofit(ToStringConverterFactory factory, @Named("intercepted") OkHttpClient okHttpClient, @Named("baseUrl") String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(factory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
        return retrofit;
    }

    @Provides
    @Named("cached")
    @AuthScope
    @Inject
    public RESTInterface provideRESTInterface(@Named("cached") Retrofit retrofit){
        return retrofit.create(RESTInterface.class);
    }

    @Provides
    @AuthScope
    @Inject
    DeviceListCallback provideDeviceListCallback(RESTClient restClient){
        return new DeviceListCallback(restClient);
    }

    @Provides
    @AuthScope
    @Inject
    DeviceMeasureCallback provideDeviceMeasureCallback(RESTClient restClient){
        return new DeviceMeasureCallback(restClient);
    }

    public static String bodyToString(final RequestBody request){
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if(copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        }
        catch (final IOException e) {
            return "did not work";
        }
    }
}
