package edu.sabien.aaronzhao.doctorhelper.presenter;

/**
 * Created by AaronZhao on 4/19/16.
 */
public interface Presenter {
    void onDestroy();
    void onResume();
    void onPause();
    void onStart();
    void onStop();
}
