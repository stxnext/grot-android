package pl.stxnext.grot.enums;

import android.content.Context;
import android.content.SharedPreferences;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;

/**
 * Created by Tomasz Konieczny on 2015-01-13.
 */
public enum MovesAchievements {

    MISER(15, R.string.achievement_miser),
    BANKER(20, R.string.achievement_banker),
    INVESTOR(25, R.string.achievement_investor);

    private final int moves;
    private final int idRes;

    MovesAchievements(int moves, int idRes) {
        this.moves = moves;
        this.idRes = idRes;
    }

    public static MovesAchievements getLastReached(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        String name = preferences.getString(AppConfig.GOOGLE_PLAY_GAME_MOVES_ACHIEVEMENT, null);
        if (name != null) {
            return MovesAchievements.valueOf(name);
        } else {
            return null;
        }
    }

    public static void setLastReached(Context context, MovesAchievements achievement) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(AppConfig.GOOGLE_PLAY_GAME_MOVES_ACHIEVEMENT, achievement.name()).apply();
    }


    public int getMoves() {
        return moves;
    }

    public int getIdRes() {
        return idRes;
    }
}
