package pl.stxnext.grot.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameLayout extends GridLayout {
    public GameLayout(Context context) {
        super(context);
        setChildrenDrawingOrderEnabled(true);
    }

    public GameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setChildrenDrawingOrderEnabled(true);
    }

    public GameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setChildrenDrawingOrderEnabled(true);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setChildrenDrawingOrderEnabled(true);
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        return i;
    }

    public void lock() {
        disableEnableControls(false, this);
    }

    public void unlock() {
        disableEnableControls(true, this);
    }

    private void disableEnableControls(boolean enable, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            if (child instanceof ViewGroup){
                disableEnableControls(enable, (ViewGroup)child);
            } else {
                child.setEnabled(enable);
            }
        }
    }

}
