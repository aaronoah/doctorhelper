package edu.sabien.aaronzhao.doctorhelper.components;

import dagger.Component;
import edu.sabien.aaronzhao.doctorhelper.AppModule;
import edu.sabien.aaronzhao.doctorhelper.presenter.MainViewModule;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgViewModule;
import edu.sabien.aaronzhao.doctorhelper.utils.GestureModule;
import edu.sabien.aaronzhao.doctorhelper.helper.NetworkModule;
import edu.sabien.aaronzhao.doctorhelper.scopes.AppScope;

/**
 * Created by AaronZhao on 4/18/16.
 */
@AppScope
@Component(
        modules = {AppModule.class}
)
public interface AppComponent {
    MainActivityComponent plus(MainViewModule mainViewModule, GestureModule gestureModule);
    MsgActivityComponent plus(MsgViewModule msgViewModule, GestureModule gestureModule);
}
