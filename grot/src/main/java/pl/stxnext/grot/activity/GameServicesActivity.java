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
 * @author Mieszko Stelmach @ STXNext
 */
public class GameServicesActivity extends BaseGameActivity {

    private static final int REQUEST_LEADERBOARD = 1546;
    private static final int REQUEST_ACHIEVEMENTS = 1236;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_service_activity);

        beginUserInitiatedSignIn();
    }

    @Override
    public void onSignInFailed() {
        findViewById(R.id.sign_in_container).setVisibility(View.VISIBLE);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginUserInitiatedSignIn();
                v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onSignInSucceeded() {
        SharedPreferences preferences = getSharedPreferences(AppConfig.SHARED_PREFS, MODE_PRIVATE);
        preferences.edit().putBoolean(AppConfig.GOOGLE_PLAY_GAMES_STATUS, true).apply();

        findViewById(R.id.game_center_menu).setVisibility(View.VISIBLE);
        findViewById(R.id.leaderboard_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLeaderboard();
            }
        });

        findViewById(R.id.achievements_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAchievements();
            }
        });
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
            if (response == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED ) {
                findViewById(R.id.game_center_menu).setVisibility(View.GONE);
                onSignInFailed();
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
