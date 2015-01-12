package pl.stxnext.grot.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.example.games.basegameutils.BaseGameActivity;

import java.util.Timer;
import java.util.TimerTask;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;

/**
 * Created by Tomasz Konieczny on 2015-01-07.
 */
public class GameOverActivity extends BaseGameActivity {

    public static final int RESTART_GAME = 1421;

    public static final String BEST_RESULT_PREF = "personal_result";

    public static final String GAME_RESULT_ARG = "game_result";
    private TextSwitcher scoreSwitcher;
    private ProgressBar progressBar;
    private int score;
    private volatile int currentScore = 0;
    private Typeface typefaceBold;
    private int personalBest;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over_activity);

        if (getIntent() != null) {
            score = getIntent().getIntExtra(GAME_RESULT_ARG, 0);

            SharedPreferences prefs = getSharedPreferences(AppConfig.SHARED_PREFS, MODE_PRIVATE);
            personalBest = prefs.getInt(BEST_RESULT_PREF, 0);

            progressBar = (ProgressBar) findViewById(R.id.scoreProgress);

            int maxProgress = score > personalBest ? score : personalBest;
            progressBar.setMax(maxProgress);

            if (score > personalBest) {
                personalBest = score;
                prefs.edit().putInt(BEST_RESULT_PREF, score).apply();
                if (prefs.getBoolean(AppConfig.GOOGLE_PLAY_GAMES_STATUS, false)) {
                    beginUserInitiatedSignIn();
                }
            }

            typefaceBold = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
            ViewSwitcher.ViewFactory factory = new ViewSwitcher.ViewFactory() {
                @Override
                public View makeView() {
                    View view = LayoutInflater.from(GameOverActivity.this).inflate(R.layout.game_score_text_view, null);

                    TextView textView = (TextView) view.findViewById(R.id.scoreLabel);
                    textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

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

            (new Timer()).scheduleAtFixedRate(timerTask, 0, 8);
        }

        updateScore();
        prepareTopLabel();
    }

    private void prepareTopLabel() {
        TextView gameOverLabel = (TextView) findViewById(R.id.game_over_label);
        gameOverLabel.setTypeface(typefaceBold);

        Integer labelRes;
        if (score > 1200) {
            labelRes = R.string.masterpiece;
        } else if (score > 1000) {
            labelRes = R.string.perfect;
        } else if (score > 800) {
            labelRes = R.string.excellent;
        } else if (score > 600) {
            labelRes = R.string.great;
        } else if (score > 500) {
            labelRes = R.string.good;
        } else if (score > 400) {
            labelRes = R.string.quite_good;
        } else if (score > 200) {
            labelRes = R.string.fair;
        } else {
            labelRes = R.string.poor;
        }

        gameOverLabel.setText(labelRes);
    }

    private void prepareBottomContainer() {
        View bottomContainer = findViewById(R.id.game_over_menu);
        bottomContainer.setVisibility(View.VISIBLE);

        bottomContainer.findViewById(R.id.game_center_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this, GameServicesActivity.class);
                startActivity(intent);
            }
        });

        bottomContainer.findViewById(R.id.restart_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESTART_GAME);
                finish();
            }
        });
    }

    private void showRecord() {
        if (score == personalBest) {
            View recordView = findViewById(R.id.record);
            recordView.setVisibility(View.VISIBLE);
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.record_animation);
            recordView.startAnimation(anim);
        }
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
                    scoreSwitcher.setInAnimation(GameOverActivity.this, R.anim.score_rescale_in);
                    scoreSwitcher.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            scoreSwitcher.setText(String.format("%d", score));
                            scoreSwitcher.getInAnimation().setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    prepareBottomContainer();
                                    showRecord();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {

                                }
                            });
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

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
        GoogleApiClient googleApiClient = getApiClient();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            Games.Leaderboards.submitScore(googleApiClient, getString(R.string.leaderboard_id), score);
        }
    }
}
