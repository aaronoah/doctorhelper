package edu.sabien.aaronzhao.doctorhelper;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.media.AudioManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.scopes.AppScope;
import edu.sabien.aaronzhao.doctorhelper.view.AlertDialog;

/**
 * Created by AaronZhao on 2/28/16.
 */
@Module
public class AppModule { //provides modules that are globally used

    private static Application mApplication;

    public AppModule(Application application){
        mApplication = application;
    }

    @AppScope
    @Provides
    Application provideApplication(){
        return mApplication;
    }

    @AppScope
    @Provides
    AudioManager provideAudioManager(Application application){
        return (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);
    }


}
