package edu.sabien.aaronzhao.doctorhelper.view;

import android.app.Activity;
import android.provider.Settings;

import edu.sabien.aaronzhao.doctorhelper.components.NetworkComponent;

/**
 * Created by AaronZhao on 4/21/16.
 */
public class BaseActivity extends Activity{

    protected NetworkComponent networkComponent;

    public NetworkComponent getNetworkComponent(){
        return this.networkComponent;
    }

    public void popupMenu(){
        this.openOptionsMenu();
    }

    public void dismiss(){
        this.finish();
        System.exit(0);
    }

    public void scrollUpList(){}

    public void scrollDownList(){}
}
