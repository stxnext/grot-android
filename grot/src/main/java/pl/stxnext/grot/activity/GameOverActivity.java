package pl.stxnext.grot.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over_activity);

        if (getIntent() != null ) {
            SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            int personalBest = prefs.getInt(BEST_RESULT_PREF, 0);

            final int score = getIntent().getIntExtra(GAME_RESULT_ARG, 0);
            final Timer timer = new Timer();
            final TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
            final ProgressBar progressBar = (ProgressBar) findViewById(R.id.scoreProgress);

            int maxProgress = score > personalBest ? score : personalBest;
            progressBar.setMax(maxProgress);

            if (score > personalBest) {
                prefs.edit().putInt(BEST_RESULT_PREF, score).apply();
            }

            int duration = score / 30;
            timer.scheduleAtFixedRate(new TimerTask() {

                @Override
                public void run() {
                    if (progressBar.getProgress() < score) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int progress = progressBar.getProgress();
                                int difference = score - progress;
                                int value = 1;
                                if (difference > 200) {
                                    value = 10;
                                } else if (difference > 50) {
                                    value = 2;
                                }

                                progressBar.setProgress(progress + value);
                                scoreLabel.setText(String.valueOf(progressBar.getProgress()));
                            }
                        });
                    } else {
                        cancel();
                    }
                }
            }, 0, duration);

        }
    }
}
