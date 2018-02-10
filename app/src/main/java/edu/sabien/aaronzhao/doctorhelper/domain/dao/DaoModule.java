package edu.sabien.aaronzhao.doctorhelper.domain.dao;

import android.content.Context;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.domain.DbHelper;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.scopes.AuthScope;
import edu.sabien.aaronzhao.doctorhelper.scopes.LocalScope;

/**
 * Created by AaronZhao on 29/04/16.
 */
@Module
public class DaoModule {

    private Context mContext;

    public DaoModule(Context context){mContext = context;}

    @Provides
    @ActivityScope
    public DbHelper provideDbHelper(){
        return new DbHelper(mContext);
    }

    @Provides
    @ActivityScope
    @Inject
    public OnlineDao provideOnlineDao(DbHelper dbHelper){
        return new OnlineDao(dbHelper);
    }

    @Provides
    @ActivityScope
    @Inject
    public OfflineDao provideOfflineDao(DbHelper dbHelper){
        return new OfflineDao(dbHelper);
    }

}
