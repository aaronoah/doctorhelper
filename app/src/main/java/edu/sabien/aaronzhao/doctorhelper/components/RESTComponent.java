package edu.sabien.aaronzhao.doctorhelper.components;

import dagger.Component;
import dagger.Subcomponent;
import edu.sabien.aaronzhao.doctorhelper.AppModule;
import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;
import edu.sabien.aaronzhao.doctorhelper.domain.model.DataModule;
import edu.sabien.aaronzhao.doctorhelper.rest.RESTClient;
import edu.sabien.aaronzhao.doctorhelper.rest.RESTModule;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.scopes.AuthScope;

/**
 * Created by AaronZhao on 4/19/16.
 */
@AuthScope
@Subcomponent(
        modules = {RESTModule.class, DataModule.class}
)
public interface RESTComponent {
    void inject(RESTClient restClient);
}
