package pl.stxnext.grot.enums;

import android.content.Context;
import android.content.SharedPreferences;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;

/**
 * Created by Tomasz Konieczny on 2015-01-13.
 */
public enum PlayedGamesAchievements {

    GUEST(10, R.string.achievement_guest),
    COUNTRYMAN(50, R.string.achievement_countryman),
    CITIZEN(100, R.string.achievement_grot_citizen),
    PRESIDENT(200, R.string.achievement_mr_president);

    private final int playedGames;
    private final int idRes;

    PlayedGamesAchievements(int playedGames, int idRes) {
        this.playedGames = playedGames;
        this.idRes = idRes;
    }

    public static PlayedGamesAchievements getLastReached(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        String name = preferences.getString(AppConfig.GOOGLE_PLAY_GAME_PLAYED_GAMES_ACHIEVEMENT, null);
        if (name != null) {
            return PlayedGamesAchievements.valueOf(name);
        } else {
            return null;
        }
    }

    public static void setLastReached(Context context, PlayedGamesAchievements achievement) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        preferences.edit().putString(AppConfig.GOOGLE_PLAY_GAME_PLAYED_GAMES_ACHIEVEMENT, achievement.name()).apply();
    }


    public int getPlayedGames() {
        return playedGames;
    }

    public int getIdRes() {
        return idRes;
    }

    public static void addPlayedGame(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        int counter = preferences.getInt(AppConfig.GOOGLE_PLAY_GAME_PLAYED_GAMES_COUNTER, 0);
        preferences.edit().putInt(AppConfig.GOOGLE_PLAY_GAME_PLAYED_GAMES_COUNTER, counter + 1).apply();
    }

    public static int getPlayedGames(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(AppConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        int counter = preferences.getInt(AppConfig.GOOGLE_PLAY_GAME_PLAYED_GAMES_COUNTER, 0);
        return counter;
    }
}
