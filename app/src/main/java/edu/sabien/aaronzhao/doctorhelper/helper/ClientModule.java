package edu.sabien.aaronzhao.doctorhelper.helper;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.rest.RESTClient;
import edu.sabien.aaronzhao.doctorhelper.tcp.TCPClient;
import edu.sabien.aaronzhao.doctorhelper.view.BaseActivity;

/**
 * Created by AaronZhao on 30/04/16.
 */
@Module
public class ClientModule {  //providing client classes for REST or TCP connection helpers

    private BaseActivity activity;
    private Choreographer cgh;

    public ClientModule(BaseActivity activity, Choreographer choreographer){
        this.activity = activity;
        this.cgh = choreographer;
    }

    @Provides
    public RESTClient provideRESTClient(){
        return new RESTClient(activity, cgh);
    }

    @Provides
    public TCPClient provideTCPClient(){
        return new TCPClient();
    }
}
