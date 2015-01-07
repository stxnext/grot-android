package pl.stxnext.grot.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.Timer;
import java.util.TimerTask;

import pl.stxnext.grot.R;

/**
 * Created by Tomasz Konieczny on 2015-01-07.
 */
public class GameOverActivity extends Activity {

    public static final String SHARED_PREFS = "grot_preferences";
    public static final String BEST_RESULT_PREF = "personal_result";

    public static final String GAME_RESULT_ARG = "game_result";
    private TextSwitcher scoreSwitcher;
    private ProgressBar progressBar;
    private int score;
    private volatile int currentScore = 0;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over_activity);

        if (getIntent() != null) {
            score = getIntent().getIntExtra(GAME_RESULT_ARG, 0);

            SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            int personalBest = prefs.getInt(BEST_RESULT_PREF, 0);

            progressBar = (ProgressBar) findViewById(R.id.scoreProgress);

            int maxProgress = score > personalBest ? score : personalBest;
            progressBar.setMax(maxProgress);

            if (score > personalBest) {
                prefs.edit().putInt(BEST_RESULT_PREF, score).apply();
            }

            final Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");

            ViewSwitcher.ViewFactory factory = new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    View view = LayoutInflater.from(GameOverActivity.this).inflate(R.layout.game_score_text_view, null);
                    TextView textView = (TextView) view.findViewById(R.id.scoreLabel);
                    textView.setTypeface(typefaceBold);

                    return textView;
                }
            };

            scoreSwitcher = (TextSwitcher) findViewById(R.id.scoreViewId);
            scoreSwitcher.setInAnimation(this, R.anim.score_scale_in);
            scoreSwitcher.setOutAnimation(this, R.anim.score_scale_out);
            scoreSwitcher.setFactory(factory);

            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (score > progressBar.getProgress()) {
                                int value = progressBar.getProgress() + 1;
                                progressBar.setProgress(value);
                                currentScore = value;
                            } else {
                                cancel();
                            }
                        }
                    });
                }
            };

            (new Timer()).scheduleAtFixedRate(timerTask, 0, 10);
        }

        updateScore();
    }

    private void updateScore() {
        String value = String.format("%d", currentScore > 10 ? currentScore - 9 : 0);
        scoreSwitcher.setText(value);
        scoreSwitcher.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (score > currentScore) {
                    scoreSwitcher.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            updateScore();
                        }
                    }, 200);
                } else if (score == currentScore) {
                    scoreSwitcher.getInAnimation().setDuration(1000);
                    scoreSwitcher.getOutAnimation().setDuration(1000);
                    scoreSwitcher.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreSwitcher.setText(String.format("%d", score));
                        }
                    }, 200);

                    currentScore++;
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
