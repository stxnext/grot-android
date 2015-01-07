package pl.stxnext.grot.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Created by Tomasz Konieczny on 2015-01-07.
 */
public class ScoreProgressView extends ProgressBar {

    private int progress = 0;

    public ScoreProgressView(Context context) {
        super(context);
    }

    public ScoreProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScoreProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ScoreProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint p = new Paint();
        RectF rectF = new RectF(30, 30, canvas.getWidth() - 30, canvas.getHeight() - 30);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(30);
        p.setAntiAlias(true);


        int progress = (int) ((getProgress() / (double) getMax()) * 360);

        canvas.drawArc(rectF, 270, progress, false, p);
    }

    @Override
    public void setProgress(int progress) {
        this.progress = progress;
        postInvalidate();
    }

    @Override
    public int getProgress() {
        return this.progress;
    }
}
