package edu.sabien.aaronzhao.doctorhelper.components;

import dagger.Subcomponent;
import edu.sabien.aaronzhao.doctorhelper.domain.dao.DaoModule;
import edu.sabien.aaronzhao.doctorhelper.presenter.MsgPresenterImpl;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.scopes.LocalScope;

/**
 * Created by AaronZhao on 29/04/16.
 */
@ActivityScope
@Subcomponent(
        modules = {DaoModule.class}
)
public interface DbComponent {
    void inject(MsgPresenterImpl msgPresenter);
}
