package pl.stxnext.grot.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextSwitcher;
import android.widget.TextView;

import java.util.List;

import pl.stxnext.grot.R;
import pl.stxnext.grot.controller.GameController;
import pl.stxnext.grot.controller.ScoreBoardViewController;
import pl.stxnext.grot.fragment.FinishedGameFragment;
import pl.stxnext.grot.fragment.GameFragment;
import pl.stxnext.grot.listener.GameStateChangedListener;
import pl.stxnext.grot.model.FieldTransition;
import pl.stxnext.grot.model.GamePlainModel;

/**
 * @author Mieszko Stelmach @ STXNext
 */
public class MainActivity extends Activity implements GameStateChangedListener, GameController.GameControllerListener {

    private static final String GAME_FRAGMENT_TAG = "game_fragment_tag";
    private static final String FINISHED_GAME_FRAGMENT_TAG = "finished_game_fragment_tag";
    private GameController gameController;
    private GameFragment gameFragment;

    ScoreBoardViewController scoreBoardViewController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.gameController = new GameController(this);

        TextSwitcher scoreSwitcher = (TextSwitcher) findViewById(R.id.scoreViewId);
        TextSwitcher movesSwitcher = (TextSwitcher) findViewById(R.id.movesViewId);
        this.scoreBoardViewController = new ScoreBoardViewController(this, scoreSwitcher, movesSwitcher);

        Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabelId);
        scoreLabel.setTypeface(typefaceRegular);
        TextView movesLabel = (TextView) findViewById(R.id.movesLabelId);
        movesLabel.setTypeface(typefaceRegular);
        addGameFragment();
    }

    private void addGameFragment() {
        this.gameFragment = new GameFragment();
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

    @Override
    public void onRestartGame() {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.game_plain_container, gameFragment, GAME_FRAGMENT_TAG)
                .commit();
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
    public void onGameFinished(GamePlainModel model) {
        FinishedGameFragment finishedGameFragment = new FinishedGameFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.game_plain_container, finishedGameFragment, FINISHED_GAME_FRAGMENT_TAG)
                .commit();
    }
}
