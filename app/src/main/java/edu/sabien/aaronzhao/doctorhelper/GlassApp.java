package edu.sabien.aaronzhao.doctorhelper;

import android.app.Application;
import android.content.Context;

import edu.sabien.aaronzhao.doctorhelper.components.AppComponent;
import edu.sabien.aaronzhao.doctorhelper.components.DaggerAppComponent;
import edu.sabien.aaronzhao.doctorhelper.components.NetworkComponent;
import edu.sabien.aaronzhao.doctorhelper.helper.NetworkModule;


/**
 * Created by AaronZhao on 2/28/16.
 */
public class GlassApp extends Application {

    private static AppComponent appComponent;

    public static GlassApp get(Context context) {
        return (GlassApp) context.getApplicationContext();
    }

    @Override
    public void onCreate(){
        super.onCreate();
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    @Override
    public void onTerminate(){
        appComponent = null;
        super.onTerminate();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}
