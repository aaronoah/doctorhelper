package edu.sabien.aaronzhao.doctorhelper.presenter;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DeviceList;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.view.MainActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MainListAdapter;

/**
 * Created by AaronZhao on 4/18/16.
 */
@Module
public class MainViewModule {

    private static MainActivity mMainActivity;

    public MainViewModule(MainActivity mainActivity){
        mMainActivity = mainActivity;
    }

    @Provides
    @ActivityScope
    MainActivity provideMainActivity(){
        return mMainActivity;
    }

    @Provides
    @ActivityScope
    MainListAdapter provideMainListAdapter(){
        return new MainListAdapter(mMainActivity, new DeviceList());
    }

    @Provides
    @ActivityScope
    static MainPresenter provideMainPresenter(){
        return new MainPresenterImpl(mMainActivity);
    }

}
