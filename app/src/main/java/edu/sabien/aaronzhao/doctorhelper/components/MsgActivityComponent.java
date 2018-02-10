package edu.sabien.aaronzhao.doctorhelper.components;

import dagger.Subcomponent;
import edu.sabien.aaronzhao.doctorhelper.helper.NetworkModule;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgViewModule;
import edu.sabien.aaronzhao.doctorhelper.utils.GestureModule;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.view.MsgActivity;

/**
 * Created by AaronZhao on 4/18/16.
 */
@ActivityScope
@Subcomponent(
        modules = {MsgViewModule.class, GestureModule.class}
)
public interface MsgActivityComponent {
    void inject(MsgActivity msgActivity);
    NetworkComponent plus(NetworkModule networkModule);
}
