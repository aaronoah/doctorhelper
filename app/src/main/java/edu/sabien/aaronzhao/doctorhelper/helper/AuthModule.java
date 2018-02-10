package edu.sabien.aaronzhao.doctorhelper.helper;

import android.util.Log;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.rest.ToStringConverterFactory;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.scopes.AuthScope;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by AaronZhao on 28/04/16.
 */
@Module
public class AuthModule {

    private String baseUrl;
    private ToStringConverterFactory factory;

    public AuthModule(String baseUrl, ToStringConverterFactory factory){
        this.baseUrl = baseUrl;
        this.factory = factory;
    }

    public interface AuthInterface {
        @POST("/RESTConnector/RequestXFIPAStream")
        Call<String> auth(@Body String jsonString);
    }

    @Provides
    @Named("baseUrl")
    @ActivityScope
    public String provideBaseUrl(){
        return this.baseUrl;
    }

    @Provides
    @ActivityScope
    public ToStringConverterFactory provideToStringConverterFactory(){
        return this.factory;
    }


    @Provides
    @Named("non_cached")
    @ActivityScope
    OkHttpClient provideOkHttpClient() {
        return new OkHttpClient.Builder()
                .build();
    }

    @Provides
    @Named("non_cached")
    @ActivityScope
    @Inject
    Retrofit provideRetrofit(@Named("non_cached") OkHttpClient okHttpClient, @Named("baseUrl") String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(factory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();
        return retrofit;
    }


    @Provides
    @Named("non_cached")
    @ActivityScope
    @Inject
    public AuthInterface provideAuthInterface(@Named("non_cached") Retrofit retrofit){
        return retrofit.create(AuthInterface.class);
    }

}
