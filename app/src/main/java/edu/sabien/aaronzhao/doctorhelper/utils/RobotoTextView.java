package edu.sabien.aaronzhao.doctorhelper.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by AaronZhao on 4/20/16.
 */
public class RobotoTextView extends TextView {

    public RobotoTextView(Context context) {
        super(context);
        style(context);
    }

    public RobotoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        style(context);
    }

    public RobotoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        style(context);
    }

    private void style(Context context) {
        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/Roboto-Light.ttf");
        setTypeface(tf);
    }

}
