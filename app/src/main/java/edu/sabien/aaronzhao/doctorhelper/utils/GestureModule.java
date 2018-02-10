package edu.sabien.aaronzhao.doctorhelper.utils;


import com.google.android.glass.touchpad.GestureDetector;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.view.BaseActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MainActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MsgActivity;

/**
 * Created by AaronZhao on 4/18/16.
 */
@Module
public class GestureModule {

    private BaseActivity activity;

    public GestureModule(BaseActivity activity){
        this.activity = activity;
    }

    @Provides
    @Named("Main")
    @ActivityScope
    GestureDetector provideMainGestureDetector(){
        return new AppGestureHelper((MainActivity)activity).getGestureDetectorInstance((MainActivity)activity);
    }

    @Provides
    @Named("Msg")
    @ActivityScope
    GestureDetector provideMsgGestureDetector(){
        return new AppGestureHelper((MsgActivity)activity).getGestureDetectorInstance((MsgActivity)activity);
    }
}
