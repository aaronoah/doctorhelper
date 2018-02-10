package edu.sabien.aaronzhao.doctorhelper.presenter;

import java.io.Serializable;

import edu.sabien.aaronzhao.doctorhelper.helper.Choreographer;

/**
 * Created by AaronZhao on 2/28/16.
 */
public interface MsgPresenter extends Presenter{
    void operateOnline();
    void operateOffline();
    void periodCheck();
    void setData(Serializable data, int metadata);
    Choreographer getCgh();
}
