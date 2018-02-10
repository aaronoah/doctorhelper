package edu.sabien.aaronzhao.doctorhelper.helper;

import android.app.Activity;
import android.content.Context;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.helper.Wifi;
import edu.sabien.aaronzhao.doctorhelper.presenter.MainPresenter;
import edu.sabien.aaronzhao.doctorhelper.presenter.Presenter;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.view.BaseActivity;

/**
 * Created by AaronZhao on 2/25/16.
 */
@Module
public class NetworkModule { //consists of Wifi and Choreographer connectivity check, wiring ConnectivityComponent

    private static BaseActivity mActivity;

    public NetworkModule(BaseActivity activity){
        mActivity = activity;
    }

    @Provides
    @ActivityScope
    public Wifi provideWifi(){
        return new Wifi(mActivity);
    }

    @Provides
    @ActivityScope
    @Named("unauthorized")
    public Choreographer provideChoreographer(){
        return new Choreographer(mActivity, false); //false notifies the auth status;
    }

    @Provides
    @ActivityScope
    @Named("authorized")
    public Choreographer provideAuthChoreographer(){
        return new Choreographer(mActivity, true); //true notifies the auth status
    }
}
