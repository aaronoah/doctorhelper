package edu.sabien.aaronzhao.doctorhelper.components;

import dagger.Subcomponent;
import edu.sabien.aaronzhao.doctorhelper.domain.dao.DaoModule;
import edu.sabien.aaronzhao.doctorhelper.helper.AuthModule;
import edu.sabien.aaronzhao.doctorhelper.helper.ClientModule;
import edu.sabien.aaronzhao.doctorhelper.helper.NetworkModule;
import edu.sabien.aaronzhao.doctorhelper.presenter.MainPresenterImpl;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;

/**
 * Created by AaronZhao on 2/25/16.
 */
@ActivityScope
@Subcomponent(
        modules = {NetworkModule.class}
)
public interface NetworkComponent {
    void inject(MainPresenterImpl mainPresenter);
    AuthComponent plus(AuthModule authModule, ClientModule clientModule);
    DbComponent plus(DaoModule daoModule);
}
