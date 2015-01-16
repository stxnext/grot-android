package pl.stxnext.grot.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import pl.stxnext.grot.config.AppConfig;
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
    private int color;
    private Rotation rotation = Rotation.LEFT;
    private boolean changePainters;
    private AnimatorSet animator;
    private ObjectAnimator fallAnimator;
    private ObjectAnimator moveAnimator;
    private ObjectAnimator fadeOutAnimator;

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
        this.color = getResources().getColor(model.getFieldType().getColorId());
        this.rotation = model.getRotation();
        this.changePainters = true;
        model.setListener(this);
        animateFadeIn();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        if (paint == null) {
            this.paint = new Paint() {
                {
                    setStyle(Style.FILL);
                    setColor(getResources().getColor(android.R.color.white));
                    setAntiAlias(true);
                }
            };
        }
        LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) getLayoutParams());
        if (path == null || changePainters) {
            this.path = getArrowPath(width, layoutParams);
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
        rect.set(layoutParams.leftMargin, layoutParams.topMargin, width, height);
        canvas.drawOval(rect, backgroundPaint);
        canvas.drawPath(path, paint);
    }


    private Path getArrowPath(int size, LinearLayout.LayoutParams layoutParams) {
        Path path = getArrowPath();
        Matrix matrix = new Matrix();
        final float factorX = 0.8f * (size - layoutParams.leftMargin);
        final float factorY = 0.8f * (size - layoutParams.topMargin);
        matrix.postScale(factorX, factorY);
        matrix.postRotate(rotation.getRotation(), factorX / 2, factorY / 2);
        path.transform(matrix);
        final float shiftX = 0.1f * size + layoutParams.leftMargin;
        final float shiftY = 0.1f * size + layoutParams.topMargin;
        path.offset(shiftX, shiftY);
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
    public void onModelChanged(final GameFieldModel model, boolean animateAlpha) {
        resetView();
        this.color = getResources().getColor(model.getFieldType().getColorId());
        this.rotation = model.getRotation();
        this.changePainters = true;

        if (animateAlpha) {
            animateFadeIn();
        } else {
            setAlpha(1f);
        }

        invalidate();
    }

    @Override
    public void animateFall(int jumps, AnimatorListenerAdapter animatorListenerAdapter) {
        if (fallAnimator == null) {
            this.fallAnimator = new ObjectAnimator();
            fallAnimator.setTarget(this);
            fallAnimator.setPropertyName("translationY");
            fallAnimator.setDuration(AppConfig.ANIMATION_DURATION);
            fallAnimator.setInterpolator(new BounceInterpolator());
        } else {
            fallAnimator.removeAllListeners();
        }
        LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) getLayoutParams());
        fallAnimator.addListener(animatorListenerAdapter);
        fallAnimator.setFloatValues((layoutParams.topMargin + getHeight() + layoutParams.bottomMargin) * jumps);
        fallAnimator.start();
    }

    public void resetView() {
        LinearLayout.LayoutParams layoutParams = ((LinearLayout.LayoutParams) getLayoutParams());
        setAlpha(1f);
        setX(layoutParams.leftMargin);
        setY(layoutParams.topMargin);
    }

    private void animateFadeIn() {
        if (animator == null) {
            ObjectAnimator alpha = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
            alpha.setInterpolator(new DecelerateInterpolator());
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(this, "scaleX", 0.5f, 1f);
            scaleX.setInterpolator(new OvershootInterpolator());
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(this, "scaleY", 0.5f, 1f);
            scaleY.setInterpolator(new OvershootInterpolator());
            this.animator = new AnimatorSet();
            animator.setDuration(AppConfig.ANIMATION_DURATION);
            animator.playTogether(alpha, scaleX, scaleY);
        }
        animator.start();
    }

    public Animator getMoveAnimator(int jumps, boolean transition) {
        if (fadeOutAnimator == null) {
            this.fadeOutAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        }
        if (transition) {
            if (moveAnimator == null) {
                switch (rotation) {
                    case LEFT:
                        moveAnimator = ObjectAnimator.ofFloat(this, "translationX", -getWidth() * jumps);
                        break;
                    case RIGHT:
                        moveAnimator = ObjectAnimator.ofFloat(this, "translationX", getWidth() * jumps);
                        break;
                    case UP:
                        moveAnimator = ObjectAnimator.ofFloat(this, "translationY", -getHeight() * jumps);
                        break;
                    case DOWN:
                        moveAnimator = ObjectAnimator.ofFloat(this, "translationY", getHeight() * jumps);
                        break;
                }
            } else {
                switch (rotation) {
                    case LEFT:
                        moveAnimator.setPropertyName("translationX");
                        moveAnimator.setFloatValues(-getWidth() * jumps);
                        break;
                    case RIGHT:
                        moveAnimator.setPropertyName("translationX");
                        moveAnimator.setFloatValues(getWidth() * jumps);
                        break;
                    case UP:
                        moveAnimator.setPropertyName("translationY");
                        moveAnimator.setFloatValues(-getHeight() * jumps);
                        break;
                    case DOWN:
                        moveAnimator.setPropertyName("translationY");
                        moveAnimator.setFloatValues(getHeight() * jumps);
                        break;
                }
            }
        }

        AnimatorSet animatorSet = new AnimatorSet();
        if (transition) {
            animatorSet.setDuration(AppConfig.ANIMATION_DURATION * jumps);
            animatorSet.playTogether(fadeOutAnimator, moveAnimator);
        } else {
            animatorSet.setDuration(AppConfig.ANIMATION_DURATION);
            animatorSet.play(fadeOutAnimator);
        }
        return animatorSet;
    }
}
