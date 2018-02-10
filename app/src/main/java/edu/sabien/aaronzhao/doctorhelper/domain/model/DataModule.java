package edu.sabien.aaronzhao.doctorhelper.domain.model;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.scopes.AuthScope;
import edu.sabien.aaronzhao.doctorhelper.view.BaseActivity;

/**
 * Created by AaronZhao on 4/15/16.
 */
@Module
public class DataModule {

    private BaseActivity activity;

    public DataModule(BaseActivity activity) {
        this.activity = activity;
    }

    @Provides
    @AuthScope
    DeviceList provideDeviceList(){
        return new DeviceList(activity);
    }

    @Provides
    @AuthScope
    Device provideDevice(){
        return new Device(activity);
    }

    @Provides
    @AuthScope
    MessageGram provideMessageGram(){
        return new MessageGram();
    }

}
