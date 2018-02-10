package edu.sabien.aaronzhao.doctorhelper.components;

import dagger.Subcomponent;
import edu.sabien.aaronzhao.doctorhelper.helper.NetworkModule;
import edu.sabien.aaronzhao.doctorhelper.presenter.MainViewModule;
import edu.sabien.aaronzhao.doctorhelper.utils.GestureModule;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.view.MainActivity;

/**
 * Created by AaronZhao on 4/18/16.
 */
@ActivityScope
@Subcomponent(
        modules = {MainViewModule.class, GestureModule.class}
)
public interface MainActivityComponent {
    void inject(MainActivity mainActivity);
    NetworkComponent plus(NetworkModule networkModule);
}
