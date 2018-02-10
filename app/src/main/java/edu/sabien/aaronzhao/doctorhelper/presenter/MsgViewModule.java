package edu.sabien.aaronzhao.doctorhelper.presenter;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import edu.sabien.aaronzhao.doctorhelper.domain.model.MessageList;
import edu.sabien.aaronzhao.doctorhelper.scopes.ActivityScope;
import edu.sabien.aaronzhao.doctorhelper.view.MsgActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MsgAdapter;

/**
 * Created by AaronZhao on 4/18/16.
 */
@Module
public class MsgViewModule {

    private static MsgActivity mMsgActivity;

    public MsgViewModule(MsgActivity msgActivity){
        mMsgActivity = msgActivity;
    }

    @Provides
    @ActivityScope
    static MsgActivity provideMsgActivity(){
        return mMsgActivity;
    }

    @Provides
    @ActivityScope
    MsgAdapter provideMsgAdapter(){
        return new MsgAdapter(mMsgActivity, new MessageList());
    }

    @Provides
    @Named("online")
    @ActivityScope
    static MsgPresenter provideOnlineMsgPresenter(){
        return new MsgPresenterImpl(mMsgActivity, "online");
    }

    @Provides
    @Named("offline")
    @ActivityScope
    static MsgPresenter provideOfflineMsgPresenter(){
        return new MsgPresenterImpl(mMsgActivity, "offline");
    }
}
