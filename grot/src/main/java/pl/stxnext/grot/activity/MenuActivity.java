package pl.stxnext.grot.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.example.games.basegameutils.BaseGameActivity;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;

/**
 * Created by Tomasz Konieczny on 2015-01-07.
 */
public class MenuActivity extends BaseGameActivity {

    public static final String START_SCREEN_ARG = "is_start_screen";

    public static final int RESTART_GAME = 1421;

    private static final int REQUEST_LEADERBOARD = 1546;
    private static final int REQUEST_ACHIEVEMENTS = 1236;

    public boolean isStartScreen;
    public int userRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_layout);

        isStartScreen = getIntent().getBooleanExtra(START_SCREEN_ARG, false);

        View playButton = findViewById(R.id.play_game_button);
        View restartButton = findViewById(R.id.restart_button);
        if (isStartScreen) {
            findViewById(R.id.start_background).setVisibility(View.VISIBLE);

            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MenuActivity.this, GameActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            playButton.setVisibility(View.GONE);
            restartButton.setVisibility(View.VISIBLE);
            restartButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setResult(RESTART_GAME);
                    finish();
                }
            });
        }


        findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.game_center_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MenuActivity.this, GameServicesActivity.class);
//                startActivity(intent);
            }
        });

        findViewById(R.id.about_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.leaderboard_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuActivity.this.userRequest = REQUEST_LEADERBOARD;
                if (isSignedIn()) {
                    showLeaderboard();
                } else {
                    beginUserInitiatedSignIn();
                }
            }
        });

        findViewById(R.id.achievements_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuActivity.this.userRequest = REQUEST_ACHIEVEMENTS;
                if (isSignedIn()) {
                    showAchievements();
                } else {
                    beginUserInitiatedSignIn();
                }
            }
        });

        SharedPreferences preferences = getSharedPreferences(AppConfig.SHARED_PREFS, MODE_PRIVATE);
        if (preferences.getBoolean(AppConfig.GOOGLE_PLAY_GAMES_STATUS, true)) {
            beginUserInitiatedSignIn();
        }

    }

    @Override
    public void onSignInFailed() {
        SharedPreferences preferences = getSharedPreferences(AppConfig.SHARED_PREFS, MODE_PRIVATE);
        preferences.edit().putBoolean(AppConfig.GOOGLE_PLAY_GAMES_STATUS, false).apply();
    }

    @Override
    public void onSignInSucceeded() {
        SharedPreferences preferences = getSharedPreferences(AppConfig.SHARED_PREFS, MODE_PRIVATE);
        preferences.edit().putBoolean(AppConfig.GOOGLE_PLAY_GAMES_STATUS, true).apply();
        switch (userRequest) {
            case REQUEST_LEADERBOARD:
                showLeaderboard();
                break;
            case REQUEST_ACHIEVEMENTS:
                showAchievements();
                break;
        }
    }

    private void showLeaderboard() {
        Intent intent = Games.Leaderboards.getLeaderboardIntent(getApiClient(), getString(R.string.leaderboard_id));
        startActivityForResult(intent, REQUEST_LEADERBOARD);
    }

    private void showAchievements() {
        Intent intent = Games.Achievements.getAchievementsIntent(getApiClient());
        startActivityForResult(intent, REQUEST_ACHIEVEMENTS);
    }

    @Override
    protected void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        if (request == REQUEST_LEADERBOARD || request == REQUEST_ACHIEVEMENTS) {
            if (response == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED) {
                if (isSignedIn()) {
                    reconnectClient();
                } else {
                    beginUserInitiatedSignIn();
                }
            }
        }
    }
}
