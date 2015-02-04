package pl.stxnext.grot.controller;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
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
    private final TextView scoreInfo;
    private final TextView movesInfo;

    int score = 0;
    int moves = 0;

    private final Runnable showMovesInfoRunnable = new Runnable() {
        @Override
        public void run() {
            movesInfo.setVisibility(View.INVISIBLE);
        }
    };

    public ScoreBoardViewController(Context context, TextSwitcher scoreSwitcher, TextView scoreInfo, TextSwitcher movesSwitcher, TextView movesInfo) {
        this.context = context;
        this.scoreSwitcher = scoreSwitcher;
        this.scoreInfo = scoreInfo;
        this.movesSwitcher = movesSwitcher;
        this.movesInfo = movesInfo;
        init();
    }

    private void init() {
        final Typeface typefaceBold = Typeface.createFromAsset(context.getAssets(), "Lato-Bold.ttf");

        ViewSwitcher.ViewFactory factory = new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                View view = LayoutInflater.from(context).inflate(R.layout.score_text_view, null);
                TextView textView = (TextView) view.findViewById(R.id.scoreTextView);
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                textView.setTypeface(typefaceBold);

                return textView;
            }
        };

        scoreSwitcher.setFactory(factory);
        movesSwitcher.setFactory(factory);
        scoreInfo.setTypeface(typefaceBold);
    }

    public void resetMoves(int initMoves) {
        moves = initMoves;
        movesInfo.setVisibility(View.INVISIBLE);
        if (movesSwitcher.getInAnimation() != null) {
            movesSwitcher.getInAnimation().setAnimationListener(null);
        }

        movesSwitcher.setText(String.format("%d", initMoves));
    }

    public void resetScore(int initScore) {
        score = initScore;
        scoreInfo.setVisibility(View.INVISIBLE);
        if (scoreSwitcher.getInAnimation() != null) {
            scoreSwitcher.getInAnimation().setAnimationListener(null);
        }
        scoreSwitcher.setText(String.format("%d", score));
    }

    public void showScoreInfo(final int currentScore) {
        String value = String.valueOf(currentScore - score);
        scoreInfo.setText("+" + value);
        scoreInfo.setVisibility(View.VISIBLE);
    }

    public void updateScore(final int currentScore) {
        scoreInfo.setVisibility(View.INVISIBLE);
        int difference = currentScore - score;
        if (difference > 50) {
            score += 40;
        } else if (difference > 30) {
            score += 20;
        } else if (difference > 20) {
            score += 10;
        } else if (difference > 10) {
            score += 5;
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

    public void onMove() {
        updateMoves(moves - 1);
        movesInfo.setText("-1");
        movesInfo.setVisibility(View.VISIBLE);
    }

    public void showMovesInfo(final int currentMoves) {
        final int difference = currentMoves - moves;
        final String value = String.valueOf(difference);
        String currentValue = movesInfo.getText().toString();
        if (currentValue.equals("-1")) {
            movesInfo.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (difference > 0) {
                        movesInfo.setText("+" + value);
                    }
                }
            }, 1500);
        } else if (difference > 0 && !currentValue.equals(value)) {
            movesInfo.setText("+" + value);
        }

        if (movesInfo.getVisibility() == View.VISIBLE) {
            movesInfo.removeCallbacks(showMovesInfoRunnable);
            movesInfo.postDelayed(showMovesInfoRunnable, 1500);
        }
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
                        updateMoves(currentMoves);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

}
