package pl.stxnext.grot.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import java.util.List;

import pl.stxnext.grot.R;
import pl.stxnext.grot.controller.GameController;
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
    private TextView scoreView;
    private TextView movesView;
    private Handler handler;
    private GameFragment gameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.gameController = new GameController(this, this);
        setContentView(R.layout.activity_main);
        Typeface typefaceRegular = Typeface.createFromAsset(getAssets(), "Lato-Regular.ttf");
        Typeface typefaceBold = Typeface.createFromAsset(getAssets(), "Lato-Bold.ttf");
        TextView scoreLabel = (TextView) findViewById(R.id.scoreLabelId);
        scoreLabel.setTypeface(typefaceRegular);
        this.scoreView = (TextView) findViewById(R.id.scoreViewId);
        scoreView.setTypeface(typefaceBold);
        TextView movesLabel = (TextView) findViewById(R.id.movesLabelId);
        movesLabel.setTypeface(typefaceRegular);
        this.movesView = (TextView) findViewById(R.id.movesViewId);
        movesView.setTypeface(typefaceBold);
        this.handler = new Handler();
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                scoreView.setText(String.format("%d", model.getScore()));
                movesView.setText(String.format("%d", model.getMoves()));
            }
        });
        gameController.setNewGamePlainModel(model);
    }

    @Override
    public void onFieldPressed(int position) {
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                movesView.setText(String.format("%d", model.getMoves()));
            }
        });

        updateScore(model.getScore());

        gameController.updateGamePlain(fieldTransitions);
    }

    private void updateScore(final int currentScore) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int score = Integer.valueOf(scoreView.getText().toString());
                score++;
                scoreView.setText(String.format("%d", score));
                if (score < currentScore) {
                    updateScore(currentScore);
                }
            }
        }, 80);
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
