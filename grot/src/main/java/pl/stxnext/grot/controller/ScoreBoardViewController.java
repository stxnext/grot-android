package pl.stxnext.grot.controller;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import pl.stxnext.grot.R;

/**
 * Created by Tomasz Konieczny on 2015-01-05.
 */
public class ScoreBoardViewController {

    private final Context context;
    private final TextSwitcher scoreSwitcher;
    private final TextSwitcher movesSwitcher;

    int score = 0;
    int moves = 0;

    public ScoreBoardViewController(Context context, TextSwitcher scoreSwitcher, TextSwitcher movesSwitcher) {
        this.context = context;
        this.scoreSwitcher = scoreSwitcher;
        this.movesSwitcher = movesSwitcher;
        init();
    }

    private void init() {
        final Typeface typefaceBold = Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");

        ViewSwitcher.ViewFactory factory = new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                View view = LayoutInflater.from(context).inflate(R.layout.score_text_view, null);
                TextView textView = (TextView) view.findViewById(R.id.scoreTextView);
                textView.setTypeface(typefaceBold);

                return textView;
            }
        };

        scoreSwitcher.setFactory(factory);
        movesSwitcher.setFactory(factory);
    }

    public void resetMoves(int initMoves) {
        moves = initMoves;
        if (movesSwitcher.getInAnimation() != null) {
            movesSwitcher.getInAnimation().setAnimationListener(null);
        }

        movesSwitcher.setText(String.format("%d", initMoves));
    }

    public void resetScore() {
        score = 0;

        if (scoreSwitcher.getInAnimation() != null) {
            scoreSwitcher.getInAnimation().setAnimationListener(null);
        }
        scoreSwitcher.setText(String.format("%d", score));
    }

    public void updateScore(final int currentScore) {
        if (currentScore - score > 20) {
            score = score + 10;
        } else if (currentScore - score > 10) {
            score = score + 5;
        } else {
            score++;
        }

        if (score == currentScore) {
            scoreSwitcher.setInAnimation(context, R.anim.score_slide_in_slow);
            scoreSwitcher.setOutAnimation(context, R.anim.score_slide_out_slow);
        } else {
            scoreSwitcher.setInAnimation(context, R.anim.score_slide_in);
            scoreSwitcher.setOutAnimation(context, R.anim.score_slide_out);
        }

        scoreSwitcher.setText(String.format("%d", score));
        scoreSwitcher.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (score < currentScore) {
                    updateScore(currentScore);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void updateMoves(final int currentMoves) {
        boolean changeValue = false;
        if (currentMoves > moves) {
            changeValue = true;
            movesSwitcher.setInAnimation(context, R.anim.score_slide_in_slow);
            movesSwitcher.setOutAnimation(context, R.anim.score_slide_out_slow);
            moves++;
        } else if (currentMoves < moves) {
            changeValue = true;
            movesSwitcher.setInAnimation(context, R.anim.score_slide_in_slow_reverse);
            movesSwitcher.setOutAnimation(context, R.anim.score_slide_out_slow_reverse);
            moves--;
        } else {
            movesSwitcher.setCurrentText(String.format("%d", moves));
        }

        if (changeValue) {
            movesSwitcher.setText(String.format("%d", moves));
            movesSwitcher.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (moves != currentMoves) {
                        updateScore(currentMoves);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

}
