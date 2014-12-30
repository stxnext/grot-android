package pl.stxnext.grot.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageButton;

import pl.stxnext.grot.enums.Rotation;
import pl.stxnext.grot.model.GameFieldModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameButtonView extends ImageButton implements GameFieldModel.ModelChangedListener {
    private Path path;
    private Paint paint;
    private Paint backgroundPaint;
    private RectF rect = new RectF();
    private GameFieldModel model;
    private int color;
    private Rotation rotation = Rotation.LEFT;
    private boolean changePainters;

    public GameButtonView(Context context) {
        super(context);
    }

    public GameButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public GameButtonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setModel(GameFieldModel model) {
        this.model = model;
        this.color = getResources().getColor(model.getFieldType().getColorId());
        this.rotation = model.getRotation();
        model.setListener(this);
    }

    public GameFieldModel getModel() {
        return model;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        if (path == null || changePainters) {
            this.path = getArrowPath(width);
        }
        if (paint == null || changePainters) {
            this.paint = new Paint() {
                {
                    setStyle(Style.FILL);
                    setColor(getResources().getColor(android.R.color.white));
                    setAntiAlias(true);
                }
            };
        }
        if (backgroundPaint == null || changePainters) {
            this.backgroundPaint = new Paint() {
                {
                    setStyle(Style.FILL);
                    setColor(color);
                    setAntiAlias(true);
                }
            };
            this.changePainters = false;
        }
        rect.set(0, 0, width, width);
        canvas.drawOval(rect, backgroundPaint);
        canvas.drawPath(path, paint);
    }


    private Path getArrowPath(int size) {
        Path path = getArrowPath();
        Matrix matrix = new Matrix();
        final float factor = 0.8f * size;
        matrix.postScale(factor, factor);
        matrix.postRotate(rotation.getRotation(), factor / 2, factor / 2);
        path.transform(matrix);
        final float shift = 0.1f * size;
        path.offset(shift, shift);
        return path;
    }

    private Path getArrowPath() {
        Path path = new Path();
        path.moveTo(0.145f, 0.495f);
        path.lineTo(0.1453f, 0.4758f);
        path.cubicTo(0.1453f, 0.4758f, 0.1388f, 0.4321f, 0.1904f, 0.4321f);
        path.lineTo(0.6364f, 0.4321f);
        path.lineTo(0.4353f, 0.2362f);
        path.cubicTo(0.4353f, 0.2362f, 0.3966f, 0.2175f, 0.4353f, 0.18f);
        path.lineTo(0.4675f, 0.1489f);
        path.cubicTo(0.4675f, 0.1489f, 0.4933f, 0.1177f, 0.5255f, 0.1489f);
        path.lineTo(0.8478f, 0.4607f);
        path.cubicTo(0.8478f, 0.4607f, 0.8865f, 0.4919f, 0.8478f, 0.5293f);
        path.lineTo(0.5255f, 0.8411f);
        path.cubicTo(0.5255f, 0.8411f, 0.4998f, 0.8723f, 0.4675f, 0.8411f);
        path.lineTo(0.4353f, 0.81f);
        path.cubicTo(0.4353f, 0.81f, 0.3966f, 0.7912f, 0.4353f, 0.7538f);
        path.lineTo(0.6364f, 0.5579f);
        path.lineTo(0.1904f, 0.5579f);
        path.cubicTo(0.1904f, 0.5579f, 0.1453f, 0.5641f, 0.1453f, 0.5242f);
        return path;
    }

    @Override
    public void onModelChanged(final GameFieldModel model) {
        setAlpha(1.0f);
        this.color = getResources().getColor(model.getFieldType().getColorId());
        this.rotation = model.getRotation();
        this.changePainters = true;
        postInvalidate();
    }
}
