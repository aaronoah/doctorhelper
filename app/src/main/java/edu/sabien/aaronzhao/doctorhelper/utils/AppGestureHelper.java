package edu.sabien.aaronzhao.doctorhelper.utils;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

import edu.sabien.aaronzhao.doctorhelper.view.BaseActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MainActivity;
import edu.sabien.aaronzhao.doctorhelper.view.MsgActivity;

/**
 * Created by AaronZhao on 4/15/16.
 */
public class AppGestureHelper{

    private static final String TAG = "GestureHelper";
    private BaseActivity mActivity;
    private String activityType;
    private AudioManager audioManager;

    public AppGestureHelper(BaseActivity activity){
        mActivity = activity;
        String[] className = mActivity.getClass().getName().split("\\.");
        activityType = className[className.length - 1];
        audioManager = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
    }

    public GestureDetector getGestureDetectorInstance(final Context context){
        GestureDetector gestureDetector = new GestureDetector(context);

        gestureDetector.setBaseListener(new GestureDetector.BaseListener() {
            @Override
            public boolean onGesture(Gesture gesture) {
                if (gesture == Gesture.TAP) {
                    audioManager.playSoundEffect(Sounds.TAP);
                    //Log.d(TAG, activityType);
                    mActivity.popupMenu();
                    return true;
                } else if (gesture == Gesture.TWO_TAP) {
                    // do something on two finger tap
                    if (mActivity.getLocalClassName().equals("view.MainActivity")){
                        audioManager.playSoundEffect(Sounds.TAP);
                        ((MainActivity)mActivity).onGestureTwoTap();
                    }
                    return true;
                } else if (gesture == Gesture.SWIPE_RIGHT) {
                    // do something on right (forward) swipe
                    if (mActivity.getLocalClassName().equals("view.MainActivity")){
                        audioManager.playSoundEffect(Sounds.SELECTED);
                        ((MainActivity)mActivity).scrollDownList();
                    }else if (mActivity.getLocalClassName().equals("view.MsgActivity")){
                        audioManager.playSoundEffect(Sounds.SELECTED);
                        ((MsgActivity)mActivity).scrollDownList();
                    }
                    return true;
                } else if (gesture == Gesture.SWIPE_LEFT) {
                    // do something on left (backwards) swipe
                    if (mActivity.getLocalClassName().equals("view.MainActivity")){
                        audioManager.playSoundEffect(Sounds.SELECTED);
                        ((MainActivity)mActivity).scrollUpList();
                    }else if (mActivity.getLocalClassName().equals("view.MsgActivity")){
                        audioManager.playSoundEffect(Sounds.SELECTED);
                        ((MsgActivity)mActivity).scrollUpList();
                    }
                    return true;
                }else if (gesture == Gesture.SWIPE_DOWN){
                    audioManager.playSoundEffect(Sounds.DISMISSED);
                    mActivity.dismiss();
                    return true;
                }
                return false;
            }
        });

        gestureDetector.setScrollListener(new GestureDetector.ScrollListener() {
            @Override
            public boolean onScroll(float displacement, float delta, float velocity) {
                return false;
            }
        });

        gestureDetector.setFingerListener(new GestureDetector.FingerListener() {
            @Override
            public void onFingerCountChanged(int previousCount, int currentCount) {
                // do something on finger count changes
                Log.d(TAG, "" + currentCount);
            }
        });

        return gestureDetector;
    }
}
