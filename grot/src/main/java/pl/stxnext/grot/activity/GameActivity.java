package pl.stxnext.grot.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.google.example.games.basegameutils.BaseGameActivity;

import java.util.List;

import pl.stxnext.grot.R;
import pl.stxnext.grot.config.AppConfig;
import pl.stxnext.grot.controller.GameController;
import pl.stxnext.grot.controller.ScoreBoardViewController;
import pl.stxnext.grot.fragment.GameFragment;
import pl.stxnext.grot.fragment.WarningDialogFragment;
import pl.stxnext.grot.listener.GameStateChangedListener;
import pl.stxnext.grot.model.FieldTransition;
import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class GameActivity extends BaseGameActivity implements GameStateChangedListener, GameController.GameControllerListener {

    public static final int GAME_OVER_REQUEST = 1324;
    public static final int PAUSE_MENU_REQUEST = 1325;

    private static final String GAME_FRAGMENT_TAG = "game_fragment_tag";
    private GameController gameController;
    private GameFragment gameFragment;

    ScoreBoardViewController scoreBoardViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        this.gameController = new GameController(this, this);

        SharedPreferences prefs = getSharedPreferences(AppConfig.SHARED_PREFS, MODE_PRIVATE);
        if (prefs.getBoolean(AppConfig.GOOGLE_PLAY_GAMES_STATUS, false)) {
            beginUserInitiatedSignIn();
        }

        TextSwitcher scoreSwitcher = (TextSwitcher) findViewById(R.id.scoreViewId);
        TextSwitcher movesSwitcher = (TextSwitcher) findViewById(R.id.movesViewId);
        TextView scoreInfo = (TextView) findViewById(R.id.scoreInfoLabel);
        TextView movesInfo = (TextView) findViewById(R.id.movesInfoLabel);
        this.scoreBoardViewController = new ScoreBoardViewController(this, scoreSwitcher, scoreInfo, movesSwitcher, movesInfo);

        Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabelId);
        scoreLabel.setTypeface(typefaceRegular);
        TextView movesLabel = (TextView) findViewById(R.id.movesLabelId);
        movesLabel.setTypeface(typefaceRegular);
        addGameFragment();

        findViewById(R.id.help_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.pauseButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameActivity.this, MenuActivity.class);
                startActivityForResult(intent, PAUSE_MENU_REQUEST);
            }
        });
    }

    private void addGameFragment() {
        this.gameFragment = GameFragment.newInstance(gameController.getNewGamePlainModel());
        getFragmentManager()
                .beginTransaction()
                .add(R.id.game_plain_container, gameFragment, GAME_FRAGMENT_TAG)
                .commit();
    }

    @Override
    public void onGameStarted(final GamePlainModel model) {
        scoreBoardViewController.resetScore();
        scoreBoardViewController.resetMoves(model.getMoves());
        gameController.setNewGamePlainModel(model);
    }

    @Override
    public void onFieldPressed(int position) {
        scoreBoardViewController.onMove();
        gameController.updateGameState(position);
    }

    public void restartGame() {
        gameFragment.restartGame(gameController.getNewGamePlainModel());
    }

    @Override
    public void onAnimationStart(GamePlainModel model) {
        scoreBoardViewController.showScoreInfo(model.getScore());
        scoreBoardViewController.showMovesInfo(model.getMoves());
    }

    @Override
    public void onAnimationEnd(final GamePlainModel model, List<FieldTransition> fieldTransitions) {
        scoreBoardViewController.updateScore(model.getScore());
        scoreBoardViewController.updateMoves(model.getMoves());
        gameController.updateGamePlain(fieldTransitions);
    }

    @Override
    public void updateGameInfo(GamePlainModel model, List<FieldTransition> fieldTransitions) {
        gameFragment.updateGameBoard(model, fieldTransitions);
    }

    @Override
    public void onGameFinished(final GamePlainModel model) {
        (new Handler()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                intent.putExtra(GameOverActivity.GAME_RESULT_ARG, model.getScore());
                startActivityForResult(intent, GAME_OVER_REQUEST);
            }
        }, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GAME_OVER_REQUEST) {
            if (resultCode == GameOverActivity.RESTART_GAME || resultCode == RESULT_CANCELED) {
                restartGame();
            }
        } else if (requestCode == PAUSE_MENU_REQUEST) {
            if (resultCode == MenuActivity.RESTART_GAME) {
                restartGame();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (gameController.getCurrentScore() > 0) {
            WarningDialogFragment dialog = new WarningDialogFragment();
            dialog.show(getFragmentManager(), "warning_dialog");
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onGamePlainUpdated() {
        gameFragment.enablePlain();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
        gameController.setGoogleApiClient(getApiClient());
    }
}
