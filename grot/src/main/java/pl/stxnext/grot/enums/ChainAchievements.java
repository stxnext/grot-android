package pl.stxnext.grot.enums;

import android.content.Context;
import android.content.SharedPreferences;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;

/**
 * Created by Tomasz Konieczny on 2015-01-13.
 */
public enum ChainAchievements {

    SNAKE(100, R.string.achievement_snake),
    BREAKER(150, R.string.achievement_combo_breaker),
    SNIPER(200, R.string.achievement_sniper);

    private final int score;
    private final int idRes;

    ChainAchievements(int score, int idRes) {
        this.score = score;
        this.idRes = idRes;
    }

    public static ChainAchievements getLastReached(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        String name = preferences.getString(AppConfig.GOOGLE_PLAY_GAME_CHAIN_ACHIEVEMENT, null);
        if (name != null) {
            return ChainAchievements.valueOf(name);
        } else {
            return null;
        }
    }

    public static void setLastReached(Context context, ChainAchievements achievement) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(AppConfig.GOOGLE_PLAY_GAME_CHAIN_ACHIEVEMENT, achievement.name()).apply();
    }


    public int getScore() {
        return score;
    }

    public int getIdRes() {
        return idRes;
    }
}
