package edu.sabien.aaronzhao.doctorhelper.components;

import dagger.Subcomponent;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DataModule;
import edu.sabien.aaronzhao.doctorhelper.helper.AuthModule;
import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.helper.ClientModule;
import edu.sabien.aaronzhao.doctorhelper.rest.RESTModule;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.scopes.AuthScope;

/**
 * Created by AaronZhao on 28/04/16.
 */
@ActivityScope
@Subcomponent(
        modules = {AuthModule.class, ClientModule.class}
)
public interface AuthComponent {
    void inject(Choreographer choreographer);
    RESTComponent plus(RESTModule restModule, DataModule dataModule);
}
