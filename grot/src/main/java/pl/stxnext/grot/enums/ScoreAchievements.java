package pl.stxnext.grot.enums;

import android.content.Context;
import android.content.SharedPreferences;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;

/**
 * Created by Tomasz Konieczny on 2015-01-13.
 */
public enum ScoreAchievements {

    NOOB(200, R.string.achievement_noob),
    REGULAR(500, R.string.achievement_regular_player),
    WIZARD(650, R.string.achievement_wizard),
    PARADISE(800, R.string.achievement_paradise),
    MASTER(1000, R.string.achievement_grotmaster),
    GROTSTAR(1200, R.string.achievement_grotstar),
    GOD(1600, R.string.achievement_god_of_grot);

    private final int score;
    private final int idRes;

    ScoreAchievements(int score, int idRes) {
        this.score = score;
        this.idRes = idRes;
    }

    public static ScoreAchievements getLastReached(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        String name = preferences.getString(AppConfig.GOOGLE_PLAY_GAME_SCORE_ACHIEVEMENT, null);
        if (name != null) {
            return ScoreAchievements.valueOf(name);
        } else {
            return null;
        }
    }

    public static void setLastReached(Context context, ScoreAchievements achievement) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(AppConfig.GOOGLE_PLAY_GAME_SCORE_ACHIEVEMENT, achievement.name()).apply();
    }

    public int getScore() {
        return score;
    }

    public int getIdRes() {
        return idRes;
    }
}
