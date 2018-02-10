package edu.sabien.aaronzhao.doctorhelper.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.os.Handler;
import android.view.MotionEvent;

import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;

/**
 * Created by AaronZhao on 2/17/16.
 */
public class AlertDialog extends Dialog {
    private final Context mContext;
    private final DialogInterface.OnClickListener mOnClickListener;
    private final DialogInterface.OnDismissListener mOnDismissListener;
    private final AudioManager mAudioManager;
    private final GestureDetector mGestureDetector;

    /**
     * Handles the tap gesture to call the dialog's
     * onClickListener if one is provided.
     */
    private final GestureDetector.BaseListener mBaseListener =
            new GestureDetector.BaseListener() {
                @Override
                public boolean onGesture(Gesture gesture) {
                    if (gesture == Gesture.TAP) {
                        mAudioManager.playSoundEffect(Sounds.TAP);
                        if (mOnClickListener != null) {
                            // Since Glass dialogs do not have buttons,
                            // the index passed to onClick is always 0.
                            mOnClickListener.onClick(AlertDialog.this, 0);
                        }
                        return true;
                    }else if (gesture == Gesture.SWIPE_DOWN){
                        mAudioManager.playSoundEffect(Sounds.DISMISSED);
                        if (mOnDismissListener != null){
                            mOnDismissListener.onDismiss(AlertDialog.this);
                        }
                    }
                    return false;
                }
            };

    public AlertDialog(Context context, int iconResId,
                       int textResId, int footnoteResId,
                       DialogInterface.OnClickListener onClickListener, DialogInterface.OnDismissListener onDismissListener) {
        super(context);
        mContext = context;
        mOnClickListener = onClickListener;
        mOnDismissListener = onDismissListener;
        mAudioManager =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector =
                new GestureDetector(context).setBaseListener(mBaseListener);

        setContentView(new CardBuilder(context, CardBuilder.Layout.ALERT)
                .setIcon(iconResId)
                .setText(textResId)
                .setFootnote(footnoteResId)
                .getView());
    }

    /** Overridden to let the gesture detector handle a possible tap event. */
    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event)
                || super.onGenericMotionEvent(event);
    }

    @Override
    public void show(){
        if (mContext != null){
            super.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.this.dismiss();
                }
            }, 1200);
        }
    }
}
